# AWS Deployment Guide - Inventory Management System

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Phase 1: AWS Account Setup](#phase-1-aws-account-setup)
3. [Phase 2: Infrastructure Creation](#phase-2-infrastructure-creation)
4. [Phase 3: Application Configuration](#phase-3-application-configuration)
5. [Phase 4: Backend Deployment](#phase-4-backend-deployment)
6. [Phase 5: Frontend Deployment](#phase-5-frontend-deployment)
7. [Phase 6: Custom Domain & Verification](#phase-6-custom-domain--verification)
8. [Phase 7: Final Verification & Testing](#phase-7-final-verification--testing)
9. [Phase 8: Monitoring & Maintenance](#phase-8-monitoring--maintenance)

---

## Prerequisites

### Required Tools
- AWS CLI (version 2.13+) - [Installation Guide](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)
- Git
- Maven 3.8+
- Node.js 18+ (for frontend)
- SSH client
- Text editor (nano, vim, VS Code)

### AWS Account Setup
1. Create AWS account at https://aws.amazon.com
2. Set up billing alerts to avoid unexpected charges
3. Create IAM user with programmatic access
4. Store Access Key ID and Secret Access Key securely
5. Enable MFA for root account

### Local Environment
```bash
# Verify AWS CLI installation
aws --version

# Configure AWS credentials
aws configure
# Provide: Access Key ID, Secret Access Key, Region (us-east-1), Output format (json)

# Verify credentials
aws iam get-user
```

---

## Phase 1: AWS Account Setup

### Step 1.1: Create EC2 Key Pair
```bash
# Generate key pair for SSH access
aws ec2 create-key-pair --key-name inventory-system-key --region us-east-1 \
  --query 'KeyMaterial' --output text > inventory-system-key.pem

# Set permissions
chmod 400 inventory-system-key.pem

# Save securely (NOT in git repo)
mv inventory-system-key.pem ~/.ssh/
```

### Step 1.2: Configure Environment Variables
```bash
# Copy template
cp .env.template .env

# Edit with your values
nano .env

# Required values:
# - AWS_REGION=us-east-1
# - DB_PASSWORD=YOUR_SECURE_PASSWORD
# - JWT_SECRET=YOUR_32_CHARACTER_SECRET
# - S3_BUCKET_NAME=inventory-system-YOUR_ID
```

### Step 1.3: Verify IAM Permissions
Ensure your IAM user has:
- EC2 full access
- RDS full access
- S3 full access
- VPC full access
- IAM limited access (create roles, instance profiles)
- CloudWatch full access

---

## Phase 2: Infrastructure Creation

### Step 2.1: Run Infrastructure Setup Script
```bash
# Make script executable
chmod +x aws-setup.sh

# Run setup (this creates VPC, Subnets, RDS, S3, Security Groups)
./aws-setup.sh us-east-1 t2.micro

# Output will show:
# - VPC ID
# - Subnet IDs
# - Security Group IDs
# - RDS Instance Name
# - S3 Bucket Name
```

> **Note**: RDS creation takes 5-10 minutes. You can proceed while waiting.

### Step 2.2: Verify Infrastructure Creation
```bash
# Check VPC
aws ec2 describe-vpcs --filters "Name=tag:Name,Values=inventory-management-system-vpc" \
  --query 'Vpcs[0].VpcId' --output text

# Check RDS status
aws rds describe-db-instances --db-instance-identifier inventory-management-system-db \
  --query 'DBInstances[0].DBInstanceStatus' --output text
# Wait for: available

# Get RDS endpoint (use this in .env)
RDS_ENDPOINT=$(aws rds describe-db-instances \
  --db-instance-identifier inventory-management-system-db \
  --query 'DBInstances[0].Endpoint.Address' --output text)
echo "DB_HOST=$RDS_ENDPOINT" >> .env
```

---

## Phase 3: Application Configuration

### Step 3.1: Update Spring Boot Configuration
```bash
# Backend is already configured for AWS in src/main/resources/application-aws.yml
# Verify database migration script exists:
cat inventory-management-system/src/main/resources/db/migration/V1__Initial_Schema.sql
```

### Step 3.2: Build Backend JAR
```bash
cd inventory-management-system

# Build with AWS profile, skip tests initially
mvn clean package -P aws -DskipTests

# Size should be ~50-80MB
ls -lh target/inventory-management-system.jar
```

### Step 3.3: Build Frontend
```bash
cd ../frontend

# Install dependencies
npm install

# Build production bundle
npm run build

# Verify output
ls -lh dist/
```

---

## Phase 4: Backend Deployment

### Step 4.1: Create EC2 Instance
```bash
# Get IDs from aws-setup.sh output
SUBNET_ID="subnet-xxxxx"
EC2_SG_ID="sg-xxxxx"
INSTANCE_PROFILE="inventory-management-system-instance-profile"

# Launch EC2 instance
aws ec2 run-instances \
  --image-id ami-0c55b159cbfafe1f0 \
  --instance-type t2.micro \
  --key-name inventory-system-key \
  --security-group-ids $EC2_SG_ID \
  --subnet-id $SUBNET_ID \
  --iam-instance-profile Name=$INSTANCE_PROFILE \
  --user-data file://ec2-init.sh \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=inventory-app-server}]'

# Get Instance ID from response
INSTANCE_ID="i-xxxxx"

# Get Public IP
EC2_PUBLIC_IP=$(aws ec2 describe-instances --instance-ids $INSTANCE_ID \
  --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)

echo "EC2 Instance IP: $EC2_PUBLIC_IP"
```

### Step 4.2: Wait for EC2 to be Ready
```bash
# Check instance status
aws ec2 describe-instance-status --instance-ids $INSTANCE_ID \
  --query 'InstanceStatuses[0].InstanceStatus.Status' --output text

# Wait for: ok

# SSH to instance (may take 2-3 minutes)
ssh -i ~/.ssh/inventory-system-key.pem ec2-user@$EC2_PUBLIC_IP

# Verify Java installed
java -version
```

### Step 4.3: Transfer JAR to EC2
```bash
# From local machine
scp -i ~/.ssh/inventory-system-key.pem \
  inventory-management-system/target/inventory-management-system.jar \
  ec2-user@$EC2_PUBLIC_IP:/home/ec2-user/app.jar

# SSH to EC2 and verify
ssh -i ~/.ssh/inventory-system-key.pem ec2-user@$EC2_PUBLIC_IP
ls -lh /home/ec2-user/app.jar
```

### Step 4.4: Create Systemd Service
```bash
# SSH to EC2
ssh -i ~/.ssh/inventory-system-key.pem ec2-user@$EC2_PUBLIC_IP

# Create service file
sudo tee /etc/systemd/system/inventory-app.service > /dev/null <<EOF
[Unit]
Description=Inventory Management System
After=network.target

[Service]
User=ec2-user
WorkingDirectory=/home/ec2-user
ExecStart=/usr/bin/java -jar /home/ec2-user/app.jar \\
  --spring.profiles.active=aws \\
  --spring.datasource.url=jdbc:postgresql://${DB_HOST}:5432/inventory_db \\
  --spring.datasource.username=pgadmin \\
  --spring.datasource.password=${DB_PASSWORD} \\
  --app.jwt.secret-key=${JWT_SECRET} \\
  --aws.region=${AWS_REGION} \\
  --s3.bucket-name=${S3_BUCKET_NAME}

Restart=always
RestartSec=10

StandardOutput=append:/var/log/inventory/app.log
StandardError=append:/var/log/inventory/app.log

[Install]
WantedBy=multi-user.target
EOF

# Create log directory
sudo mkdir -p /var/log/inventory
sudo chown ec2-user:ec2-user /var/log/inventory

# Enable and start service
sudo systemctl daemon-reload
sudo systemctl enable inventory-app
sudo systemctl start inventory-app

# Check status
sudo systemctl status inventory-app

# View logs
tail -f /var/log/inventory/app.log
```

### Step 4.5: Configure Nginx Reverse Proxy
```bash
# SSH to EC2
ssh -i ~/.ssh/inventory-system-key.pem ec2-user@$EC2_PUBLIC_IP

# Create Nginx config
sudo tee /etc/nginx/conf.d/inventory.conf > /dev/null <<EOF
upstream inventory_backend {
    server 127.0.0.1:8080;
}

server {
    listen 80;
    server_name ${EC2_PUBLIC_IP};

    client_max_body_size 50M;

    location / {
        proxy_pass http://inventory_backend;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    location /static/ {
        alias /usr/share/nginx/html/;
        expires 30d;
    }
}
EOF

# Restart Nginx
sudo systemctl restart nginx
sudo systemctl status nginx
```

---

## Phase 5: Frontend Deployment

### Step 5.1: Upload Frontend to S3
```bash
# Get S3 bucket name from .env or previous output
S3_BUCKET=$(grep S3_BUCKET_NAME .env | cut -d= -f2)

# Upload frontend files
aws s3 sync frontend/dist/ s3://$S3_BUCKET/frontend/ \
  --cache-control "public, max-age=3600" \
  --delete

# Set index.html to no-cache
aws s3 cp frontend/dist/index.html s3://$S3_BUCKET/frontend/index.html \
  --cache-control "public, max-age=0, must-revalidate" \
  --content-type "text/html"

# Verify upload
aws s3 ls s3://$S3_BUCKET/frontend/
```

### Step 5.2: Create CloudFront Distribution
```bash
# Create OAI (Origin Access Identity)
OAI_ID=$(aws cloudfront create-cloud-front-origin-access-identity \
  --cloud-front-origin-access-identity-config \
  'CallerReference=inventory-system-oai,Comment=OAI for inventory system' \
  --query 'CloudFrontOriationAccessIdentity.Id' --output text)

# Create distribution (save to file for easier management)
cat > cf-distribution.json <<'EOF'
{
  "CallerReference": "inventory-system-$(date +%s)",
  "Comment": "Frontend for Inventory Management System",
  "DefaultRootObject": "index.html",
  "Origins": {
    "Quantity": 1,
    "Items": [
      {
        "Id": "S3Origin",
        "DomainName": "BUCKET_NAME.s3.amazonaws.com",
        "S3OriginConfig": {
          "OriginAccessIdentity": "origin-access-identity/cloudfront/OAI_ID"
        }
      }
    ]
  },
  "DefaultCacheBehavior": {
    "AllowedMethods": {
      "Quantity": 2,
      "Items": ["GET", "HEAD"]
    },
    "CachePolicyId": "658327ea-f89d-4fab-a63d-7e88639e58f6",
    "Compress": true,
    "TargetOriginId": "S3Origin",
    "ViewerProtocolPolicy": "redirect-to-https"
  },
  "CacheBehaviors": [
    {
      "PathPattern": "index.html",
      "AllowedMethods": {
        "Quantity": 2,
        "Items": ["GET", "HEAD"]
      },
      "CachePolicyId": "4135ea3d-c35d-46eb-81d7-reefstructure",
      "TargetOriginId": "S3Origin",
      "ViewerProtocolPolicy": "redirect-to-https"
    }
  ],
  "Enabled": true
}
EOF

# Update placeholders
sed -i "s/BUCKET_NAME/$S3_BUCKET/" cf-distribution.json
sed -i "s/OAI_ID/$OAI_ID/" cf-distribution.json

# Create distribution
aws cloudfront create-distribution --distribution-config file://cf-distribution.json

# Get CloudFront domain
CF_DOMAIN=$(aws cloudfront list-distributions \
  --query 'DistributionList.Items[0].DomainName' --output text)

echo "CloudFront URL: https://$CF_DOMAIN"
```

---

## Phase 6: Custom Domain & Verification

### Step 6.1: Configure Custom Domain (Recommended)

To use a professional domain like `stock.os` instead of the CloudFront distribution domain:

#### Prerequisites
- Domain name (e.g., stock.os purchased from Route 53, GoDaddy, Namecheap, etc.)
- You must have access to the domain's DNS settings

#### Configuration Steps

**1. Request SSL/TLS Certificate**
```bash
# Create certificate in AWS Certificate Manager
aws acm request-certificate \
  --domain-name stock.os \
  --subject-alternative-names "www.stock.os" "api.stock.os" \
  --validation-method DNS \
  --region us-east-1

# Save the Certificate ARN
CERT_ARN="arn:aws:acm:us-east-1:ACCOUNT_ID:certificate/XXXX-XXXX"
```

**2. Verify Certificate (AWS will provide DNS CNAME)**
- Go to: AWS Console → Certificate Manager
- Click your certificate → Add CNAME records to your domain registrar
- Wait for validation (usually 5-15 minutes)

**3. Update CloudFront Distribution**
```bash
# Backup current config
aws cloudfront get-distribution-config \
  --id E1YKXYS6CAYRVP > cf-config-backup.json

# Create updated config with custom domain
cat > cf-config-update.json << 'EOF'
{
  "ViewerCertificate": {
    "ACMCertificateArn": "arn:aws:acm:us-east-1:ACCOUNT_ID:certificate/XXXX",
    "SSLSupportMethod": "sni-only",
    "MinimumProtocolVersion": "TLSv1.2_2021"
  },
  "Aliases": {
    "Quantity": 3,
    "Items": ["stock.os", "www.stock.os", "api.stock.os"]
  },
  "DefaultRootObject": "index.html",
  "Comment": "Stock.os Inventory Management System"
}
EOF

# Update distribution (note: need ETag from get-distribution-config)
ETAG=$(cat cf-config-backup.json | jq -r '.ETag')
# Merge the configs and update (manual step recommended for production)
echo "⚠️  Update CloudFront manually via AWS Console:"
echo "   1. CloudFront → Distributions → E1YKXYS6CAYRVP"
echo "   2. Edit → Alternate Domain Names → Add: stock.os, www.stock.os"
echo "   3. Custom SSL Certificate → Select your certificate"
echo "   4. Save"
```

**4. Update Route 53 DNS (or your registrar)**

If using **Route 53**:
```bash
# Create alias record
aws route53 change-resource-record-sets \
  --hosted-zone-id ZONE_ID \
  --change-batch '{
    "Changes": [{
      "Action": "CREATE",
      "ResourceRecordSet": {
        "Name": "stock.os",
        "Type": "A",
        "AliasTarget": {
          "HostedZoneId": "Z2FDTNDATAQYW2",
          "DNSName": "d15os9kcan23ap.cloudfront.net",
          "EvaluateTargetHealth": false
        }
      }
    }]
  }'
```

If using **external registrar (GoDaddy, Namecheap, etc.)**:
- Log into your registrar's DNS management
- Add CNAME records:
  - `stock.os` or `@` → `d15os9kcan23ap.cloudfront.net`
  - `www` → `d15os9kcan23ap.cloudfront.net`
  - `api` → `d15os9kcan23ap.cloudfront.net` (optional, if routing API separately)

**5. Verify Domain Propagation**
```bash
# Wait 15-30 minutes for DNS propagation, then test:
nslookup stock.os

# If propagated, should see:
# stock.os canonical name = d15os9kcan23ap.cloudfront.net

# Test HTTPS
curl -I https://stock.os   # Should show your certificate
curl -I https://api.stock.os/health

# Both should return SSL certificate from your domain
```

### Step 6.2: CloudFront Cache Management
```bash
# After deploying frontend updates, invalidate cache
aws cloudfront create-invalidation \
  --distribution-id E1YKXYS6CAYRVP \
  --paths "/*"

# For specific files only
aws cloudfront create-invalidation \
  --distribution-id E1YKXYS6CAYRVP \
  --paths "/index.html" "/js/*" "/css/*" "/assets/*"

# Check invalidation status
aws cloudfront list-invalidations --distribution-id E1YKXYS6CAYRVP
```

---

## Phase 7: Final Verification & Testing

### Step 7.1: Test Backend API
```bash
# Health check
curl -X GET http://$EC2_PUBLIC_IP:80/auth/health

# Expected response:
# {"status":"ok","message":"Inventory Management System API is running"}

# Login test
curl -X POST http://$EC2_PUBLIC_IP:80/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Test@123"}'
```

### Step 7.2: Test Database Connection
```bash
# SSH to EC2
ssh -i ~/.ssh/inventory-system-key.pem ec2-user@$EC2_PUBLIC_IP

# Test PostgreSQL connection
sudo yum install -y postgresql

# From EC2, test connection (replace DB_HOST)
psql -h $DB_HOST -U pgadmin -d inventory_db -c "SELECT version();"
```

### Step 7.3: Test Frontend
```bash
# Open browser
https://$CF_DOMAIN/

# Should see login page
# Test login with database credentials
```

### Step 7.4: Run Backend Tests
```bash
cd inventory-management-system

# Run all tests
mvn test -P aws

# Run specific test suite
mvn test -Dtest=ProductControllerTest -P aws

# Generate coverage report
mvn test jacoco:report -P aws
# Report at: target/site/jacoco/index.html
```

### Step 6.5: Run Frontend Tests
```bash
cd frontend

# Run tests
npm test

# With coverage
npm test -- --coverage
```

---

## Phase 8: Monitoring & Maintenance

### Step 7.1: Enable CloudWatch Logs
```bash
# Create log group
aws logs create-log-group --log-group-name /aws/ec2/inventory-app

# View logs
aws logs tail /aws/ec2/inventory-app --follow
```

### Step 7.2: Set Up Alarms
```bash
# High CPU alarm
aws cloudwatch put-metric-alarm \
  --alarm-name inventory-app-high-cpu \
  --alarm-description "Alert when CPU exceeds 80%" \
  --metric-name CPUUtilization \
  --namespace AWS/EC2 \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold \
  --evaluation-periods 2

# High disk usage alarm
aws cloudwatch put-metric-alarm \
  --alarm-name inventory-app-high-disk \
  --alarm-description "Alert when disk usage exceeds 85%" \
  --metric-name DiskSpaceUtilization \
  --namespace AWS/EC2 \
  --statistic Average \
  --period 300 \
  --threshold 85 \
  --comparison-operator GreaterThanThreshold
```

### Step 7.3: Database Backup
```bash
# Enable automatic backups (if not already enabled)
aws rds modify-db-instance \
  --db-instance-identifier inventory-management-system-db \
  --backup-retention-period 7 \
  --preferred-backup-window "03:00-04:00" \
  --apply-immediately

# Create manual snapshot
aws rds create-db-snapshot \
  --db-instance-identifier inventory-management-system-db \
  --db-snapshot-identifier inventory-db-backup-$(date +%Y%m%d)
```

### Step 7.4: SSH Maintenance
```bash
# Update security
ssh -i ~/.ssh/inventory-system-key.pem ec2-user@$EC2_PUBLIC_IP
sudo yum update -y
sudo yum install -y security-tool

# Check application status
sudo systemctl status inventory-app

# View recent logs
sudo tail -100 /var/log/inventory/app.log
```

---

## Troubleshooting

### Backend not connecting to database
```bash
# Check security group rules allow RDS port
aws ec2 describe-security-groups --group-ids $RDS_SG_ID

# Test connection from EC2
ssh -i ~/.ssh/inventory-system-key.pem ec2-user@$EC2_PUBLIC_IP
nc -zv $DB_HOST 5432
```

### Frontend not loading
```bash
# Check S3 bucket permissions
aws s3api get-bucket-acl --bucket $S3_BUCKET

# Clear CloudFront cache
aws cloudfront create-invalidation --distribution-id $CF_DISTRIBUTION_ID \
  --paths "/*"
```

### Application won't start
```bash
# Check logs
ssh -i ~/.ssh/inventory-system-key.pem ec2-user@$EC2_PUBLIC_IP
sudo systemctl status inventory-app
sudo journalctl -u inventory-app -n 50

# Restart service
sudo systemctl restart inventory-app
```

---

## Cleanup (to avoid costs)

```bash
# Terminate EC2 instance
aws ec2 terminate-instances --instance-ids $INSTANCE_ID

# Delete RDS instance
aws rds delete-db-instance --db-instance-identifier inventory-management-system-db \
  --skip-final-snapshot

# Delete S3 bucket (must be empty first)
aws s3 rm s3://$S3_BUCKET --recursive
aws s3api delete-bucket --bucket $S3_BUCKET

# Delete CloudFront distribution
aws cloudfront delete-distribution --id $CF_DISTRIBUTION_ID --if-match $ETAG
```

---

**Last Updated**: April 4, 2026
**Version**: 1.0.0
