#!/usr/bin/env bash

set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "Usage: $0 <distribution-id> [ec2-origin-hostname-or-ip]"
  echo "Example: $0 E123ABC456DEF7 13.206.75.146"
  exit 1
fi

DISTRIBUTION_ID="$1"
EC2_ORIGIN_DOMAIN="${2:-13.206.75.146}"
EC2_ORIGIN_ID="EC2ApiOrigin"
API_PATH_PATTERN="api/*"

WORKDIR="$(mktemp -d)"
trap 'rm -rf "$WORKDIR"' EXIT

CURRENT_CONFIG_JSON="$WORKDIR/current-config.json"
UPDATED_CONFIG_JSON="$WORKDIR/updated-config.json"

echo "Fetching current CloudFront distribution config for ${DISTRIBUTION_ID}"
aws cloudfront get-distribution-config --id "$DISTRIBUTION_ID" --output json > "$CURRENT_CONFIG_JSON"

python3 - "$CURRENT_CONFIG_JSON" "$UPDATED_CONFIG_JSON" "$EC2_ORIGIN_DOMAIN" "$EC2_ORIGIN_ID" "$API_PATH_PATTERN" <<'PY'
import json
import sys

input_path, output_path, ec2_origin_domain, ec2_origin_id, api_path_pattern = sys.argv[1:]

with open(input_path, 'r', encoding='utf-8') as handle:
    payload = json.load(handle)

config = payload['DistributionConfig']
etag = payload['ETag']

origins = config.get('Origins', {})
origin_items = list(origins.get('Items', []))

api_origin = {
    'Id': ec2_origin_id,
    'DomainName': ec2_origin_domain,
    'OriginPath': '',
    'CustomHeaders': {
        'Quantity': 0,
    },
    'CustomOriginConfig': {
        'HTTPPort': 80,
        'HTTPSPort': 443,
        'OriginProtocolPolicy': 'http-only',
        'OriginSslProtocols': {
            'Quantity': 1,
            'Items': ['TLSv1.2'],
        },
        'OriginReadTimeout': 30,
        'OriginKeepaliveTimeout': 5,
    },
    'ConnectionAttempts': 3,
    'ConnectionTimeout': 10,
}

for index, origin in enumerate(origin_items):
    if origin.get('Id') == ec2_origin_id:
        origin_items[index] = api_origin
        break
else:
    origin_items.append(api_origin)

origins['Items'] = origin_items
origins['Quantity'] = len(origin_items)
config['Origins'] = origins

allowed_methods = {
    'Quantity': 7,
    'Items': ['GET', 'HEAD', 'OPTIONS', 'PUT', 'POST', 'PATCH', 'DELETE'],
    'CachedMethods': {
        'Quantity': 2,
        'Items': ['GET', 'HEAD'],
    },
}

forwarded_values = {
    'QueryString': True,
    'QueryStringCacheKeys': {
        'Quantity': 0,
        'Items': [],
    },
    'Cookies': {
        'Forward': 'all',
    },
    'Headers': {
        'Quantity': 3,
        'Items': ['Origin', 'Authorization', 'Content-Type'],
    },
}

api_behavior = {
    'PathPattern': api_path_pattern,
    'TargetOriginId': ec2_origin_id,
    'ViewerProtocolPolicy': 'redirect-to-https',
    'AllowedMethods': allowed_methods,
    'Compress': True,
    'SmoothStreaming': False,
    'LambdaFunctionAssociations': {
        'Quantity': 0,
    },
    'FunctionAssociations': {
        'Quantity': 0,
    },
    'FieldLevelEncryptionId': '',
    'MinTTL': 0,
    'DefaultTTL': 0,
    'MaxTTL': 0,
    'ForwardedValues': forwarded_values,
}

cache_behaviors = config.get('CacheBehaviors')
if cache_behaviors:
    behavior_items = list(cache_behaviors.get('Items', []))
    for index, behavior in enumerate(behavior_items):
        if behavior.get('PathPattern') in ('/api/*', 'api/*'):
            behavior_items[index] = api_behavior
            break
    else:
        behavior_items.append(api_behavior)
    cache_behaviors['Items'] = behavior_items
    cache_behaviors['Quantity'] = len(behavior_items)
    config['CacheBehaviors'] = cache_behaviors
else:
    config['CacheBehaviors'] = {
        'Quantity': 1,
        'Items': [api_behavior],
    }

with open(output_path, 'w', encoding='utf-8') as handle:
    json.dump({'ETag': etag, 'DistributionConfig': config}, handle, indent=2)
    handle.write('\n')
PY

ETAG="$(python3 - "$UPDATED_CONFIG_JSON" <<'PY'
import json
import sys

with open(sys.argv[1], 'r', encoding='utf-8') as handle:
    payload = json.load(handle)

print(payload['ETag'])
PY
)"

DIST_CONFIG_FILE="$WORKDIR/distribution-config.json"
python3 - "$UPDATED_CONFIG_JSON" "$DIST_CONFIG_FILE" <<'PY'
import json
import sys

with open(sys.argv[1], 'r', encoding='utf-8') as handle:
    payload = json.load(handle)

with open(sys.argv[2], 'w', encoding='utf-8') as handle:
    json.dump(payload['DistributionConfig'], handle, indent=2)
    handle.write('\n')
PY

echo "Updating distribution"
aws cloudfront update-distribution \
  --id "$DISTRIBUTION_ID" \
  --if-match "$ETAG" \
  --distribution-config "file://$DIST_CONFIG_FILE" >/dev/null

echo "Waiting for distribution deployment"
aws cloudfront wait distribution-deployed --id "$DISTRIBUTION_ID"

echo "Creating API invalidation"
aws cloudfront create-invalidation \
  --distribution-id "$DISTRIBUTION_ID" \
  --paths "/api/*" >/dev/null

echo "Done. CloudFront distribution ${DISTRIBUTION_ID} now includes the EC2 API origin and an invalidation for /api/*."