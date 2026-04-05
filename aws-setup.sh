#!/usr/bin/env bash

set -euo pipefail

# Free-tier-first, one-by-one AWS setup for Inventory app.
# Uses existing default VPC/subnets to avoid extra paid networking resources.
#
# Usage:
#   ./aws-setup.sh <db-password> [region] [stack]
# Example:
#   ./aws-setup.sh 'Deepak1124A1' ap-south-1 inventory-prod

DB_PASSWORD="${1:-}"
REGION="${2:-ap-south-1}"
STACK="${3:-inventory-prod}"

if [[ -z "$DB_PASSWORD" ]]; then
    echo "Usage: ./aws-setup.sh <db-password> [region] [stack]"
    exit 1
fi

# RDS password restriction: must not include / @ " or space.
if [[ "$DB_PASSWORD" =~ [/@\"[:space:]] ]]; then
    echo "Error: DB password cannot contain /, @, double-quote, or spaces."
    exit 1
fi

AWS="python3 -m awscli"

echo "[1/9] Verifying AWS auth"
$AWS sts get-caller-identity --output table --region "$REGION" >/dev/null

echo "[2/9] Resolving default network"
VPC_ID="$($AWS ec2 describe-vpcs --filters Name=isDefault,Values=true --query 'Vpcs[0].VpcId' --output text --region "$REGION")"
SUBNET_1="$($AWS ec2 describe-subnets --filters Name=vpc-id,Values="$VPC_ID" --query 'Subnets[0].SubnetId' --output text --region "$REGION")"
SUBNET_2="$($AWS ec2 describe-subnets --filters Name=vpc-id,Values="$VPC_ID" --query 'Subnets[1].SubnetId' --output text --region "$REGION")"

echo "[3/9] Ensuring SSH key pair"
KEY_NAME="${STACK}-key"
if $AWS ec2 describe-key-pairs --key-names "$KEY_NAME" --region "$REGION" >/dev/null 2>&1; then
    echo "  Key exists: $KEY_NAME"
else
    $AWS ec2 create-key-pair --key-name "$KEY_NAME" --query 'KeyMaterial' --output text --region "$REGION" > "${KEY_NAME}.pem"
    chmod 400 "${KEY_NAME}.pem"
    echo "  Key created: ${KEY_NAME}.pem"
fi

echo "[4/9] Ensuring EC2 and RDS security groups"
EC2_SG_NAME="${STACK}-ec2-sg"
RDS_SG_NAME="${STACK}-rds-sg"

EC2_SG_ID="$($AWS ec2 describe-security-groups --filters Name=group-name,Values="$EC2_SG_NAME" Name=vpc-id,Values="$VPC_ID" --query 'SecurityGroups[0].GroupId' --output text --region "$REGION")"
if [[ "$EC2_SG_ID" == "None" ]]; then
    EC2_SG_ID="$($AWS ec2 create-security-group --group-name "$EC2_SG_NAME" --description 'Inventory EC2 SG' --vpc-id "$VPC_ID" --query 'GroupId' --output text --region "$REGION")"
fi
$AWS ec2 authorize-security-group-ingress --group-id "$EC2_SG_ID" --protocol tcp --port 22 --cidr 0.0.0.0/0 --region "$REGION" >/dev/null 2>&1 || true
$AWS ec2 authorize-security-group-ingress --group-id "$EC2_SG_ID" --protocol tcp --port 80 --cidr 0.0.0.0/0 --region "$REGION" >/dev/null 2>&1 || true
$AWS ec2 authorize-security-group-ingress --group-id "$EC2_SG_ID" --protocol tcp --port 8080 --cidr 0.0.0.0/0 --region "$REGION" >/dev/null 2>&1 || true

RDS_SG_ID="$($AWS ec2 describe-security-groups --filters Name=group-name,Values="$RDS_SG_NAME" Name=vpc-id,Values="$VPC_ID" --query 'SecurityGroups[0].GroupId' --output text --region "$REGION")"
if [[ "$RDS_SG_ID" == "None" ]]; then
    RDS_SG_ID="$($AWS ec2 create-security-group --group-name "$RDS_SG_NAME" --description 'Inventory RDS SG' --vpc-id "$VPC_ID" --query 'GroupId' --output text --region "$REGION")"
fi
$AWS ec2 authorize-security-group-ingress --group-id "$RDS_SG_ID" --protocol tcp --port 5432 --source-group "$EC2_SG_ID" --region "$REGION" >/dev/null 2>&1 || true

echo "[5/9] Ensuring S3 bucket"
ACCOUNT_ID="$($AWS sts get-caller-identity --query Account --output text --region "$REGION")"
S3_BUCKET="${STACK}-${ACCOUNT_ID}-${REGION}"
if $AWS s3api head-bucket --bucket "$S3_BUCKET" 2>/dev/null; then
    echo "  Bucket exists: $S3_BUCKET"
else
    $AWS s3api create-bucket --bucket "$S3_BUCKET" --region "$REGION" --create-bucket-configuration LocationConstraint="$REGION" >/dev/null
    echo "  Bucket created: $S3_BUCKET"
fi
$AWS s3api put-bucket-versioning --bucket "$S3_BUCKET" --versioning-configuration Status=Enabled >/dev/null
$AWS s3api put-public-access-block --bucket "$S3_BUCKET" --public-access-block-configuration BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true >/dev/null

echo "[6/9] Ensuring DB subnet group"
DB_SUBNET_GROUP="${STACK}-db-subnet-group"
if $AWS rds describe-db-subnet-groups --db-subnet-group-name "$DB_SUBNET_GROUP" --region "$REGION" >/dev/null 2>&1; then
    echo "  DB subnet group exists: $DB_SUBNET_GROUP"
else
    $AWS rds create-db-subnet-group --db-subnet-group-name "$DB_SUBNET_GROUP" --db-subnet-group-description 'Inventory DB subnet group' --subnet-ids "$SUBNET_1" "$SUBNET_2" --region "$REGION" >/dev/null
fi

echo "[7/9] Ensuring RDS PostgreSQL (free-tier profile)"
DB_ID="${STACK}-db"
if $AWS rds describe-db-instances --db-instance-identifier "$DB_ID" --region "$REGION" >/dev/null 2>&1; then
    echo "  DB exists: $DB_ID"
else
    $AWS rds create-db-instance \
        --db-instance-identifier "$DB_ID" \
        --db-instance-class db.t3.micro \
        --engine postgres \
        --master-username deepak \
        --master-user-password "$DB_PASSWORD" \
        --allocated-storage 20 \
        --storage-type gp3 \
        --db-name inventory_db \
        --vpc-security-group-ids "$RDS_SG_ID" \
        --db-subnet-group-name "$DB_SUBNET_GROUP" \
        --backup-retention-period 1 \
        --no-publicly-accessible \
        --region "$REGION" >/dev/null
fi

echo "[8/9] Ensuring EC2 backend instance"
AMI_ID="$($AWS ssm get-parameter --name /aws/service/ami-amazon-linux-latest/al2023-ami-kernel-6.1-x86_64 --query 'Parameter.Value' --output text --region "$REGION")"
INSTANCE_ID="$($AWS ec2 describe-instances --filters Name=tag:Name,Values=${STACK}-backend Name=instance-state-name,Values=pending,running,stopping,stopped --query 'Reservations[].Instances[0].InstanceId' --output text --region "$REGION")"
if [[ "$INSTANCE_ID" == "None" || -z "$INSTANCE_ID" ]]; then
    INSTANCE_ID="$($AWS ec2 run-instances --image-id "$AMI_ID" --instance-type t3.micro --key-name "$KEY_NAME" --security-group-ids "$EC2_SG_ID" --subnet-id "$SUBNET_1" --tag-specifications "ResourceType=instance,Tags=[{Key=Name,Value=${STACK}-backend}]" --query 'Instances[0].InstanceId' --output text --region "$REGION")"
fi

echo "[9/9] Summary"
EC2_PUBLIC_IP="$($AWS ec2 describe-instances --instance-ids "$INSTANCE_ID" --query 'Reservations[0].Instances[0].PublicIpAddress' --output text --region "$REGION")"
DB_STATUS="$($AWS rds describe-db-instances --db-instance-identifier "$DB_ID" --query 'DBInstances[0].DBInstanceStatus' --output text --region "$REGION")"

echo "----------------------------------------"
echo "Stack:           $STACK"
echo "Region:          $REGION"
echo "EC2 Instance:    $INSTANCE_ID"
echo "EC2 Public IP:   $EC2_PUBLIC_IP"
echo "EC2 SG:          $EC2_SG_ID"
echo "RDS Instance:    $DB_ID"
echo "RDS Status:      $DB_STATUS"
echo "RDS SG:          $RDS_SG_ID"
echo "S3 Bucket:       $S3_BUCKET"
echo "Key Pair:        $KEY_NAME"
echo "----------------------------------------"
echo "Next: wait for RDS to be 'available' before app deployment."
