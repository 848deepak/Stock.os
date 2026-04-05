# 🌐 Custom Domain Setup Quick Reference

## Transform Your URL

### From:
```
https://d15os9kcan23ap.cloudfront.net/api/products
```

### To:
```
https://stock.os/api/products
```

---

## ⚡ Quick Setup (15-30 minutes)

### Step 1: Get a Domain
```bash
# Option A: AWS Route 53
aws route53 register-domain --domain-name stock.os --duration-in-years 1

# Option B: External registrar (GoDaddy, Namecheap, etc.)
# Buy domain at registrar website
```

### Step 2: Create SSL Certificate
```bash
# In AWS Certificate Manager
aws acm request-certificate \
  --domain-name stock.os \
  --subject-alternative-names "www.stock.os" "api.stock.os" \
  --validation-method DNS \
  --region us-east-1

# Note the Certificate ARN: arn:aws:acm:...
```

### Step 3: Verify Certificate (AWS provides CNAME)
1. Go to AWS Console → Certificate Manager
2. Click your certificate
3. Copy the CNAME record value
4. Add to your domain's DNS settings at registrar
5. Wait for validation (usually 5-15 minutes)

### Step 4: Update CloudFront
1. Go to AWS Console → CloudFront
2. Edit distribution: E1YKXYS6CAYRVP
3. Add Alternate Domain Names:
   - stock.os
   - www.stock.os
   - api.stock.os (optional)
4. Select your SSL certificate
5. Save

### Step 5: Update DNS Records

**If using Route 53:**
```bash
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

**If using GoDaddy / Namecheap / Other registrar:**
1. Log into registrar dashboard
2. Go to DNS settings
3. Add CNAME records:
   - Name: `@` (or `stock.os`)
   - Value: `d15os9kcan23ap.cloudfront.net`
4. Add for `www` and `api` subdomains too
5. Save (allow 15-30 minutes for DNS propagation)

### Step 6: Test Domain
```bash
# Wait 15-30 minutes for DNS propagation, then test

# Check if domain resolves
nslookup stock.os

# Test HTTPS
curl -I https://stock.os
curl -I https://api.stock.os/health

# Should show your SSL certificate, not CloudFront's
```

---

## 📋 Registrar-Specific Instructions

### AWS Route 53 (Easiest if using AWS)
1. AWS Console → Route 53 → Register Domain
2. Follow wizard (select your domain)
3. Create hosted zone (Route 53 handles this)
4. Add alias records pointing to CloudFront
5. Done! (propagation: 5-15 min)

### GoDaddy
1. Login to GoDaddy Dashboard
2. Settings → DNS Management
3. Add Records → CNAME
4. Name: `@` (for root) or `www` and `api`
5. Value: `d15os9kcan23ap.cloudfront.net`
6. Save (propagation: 15-30 min)

### Namecheap
1. Login to Namecheap Dashboard
2. Domain List → Manage → Advanced DNS
3. Host Records → Add New Record
4. Type: CNAME
5. Name: `@` or subdomain
6. Value: `d15os9kcan23ap.cloudfront.net`
7. Save (propagation: 15-30 min)

### Cloudflare (Free DNS)
1. Add your domain to Cloudflare
2. Update registrar's nameservers to Cloudflare's
3. In Cloudflare Dashboard → DNS
4. Add Record: CNAME → `stock.os` → `d15os9kcan23ap.cloudfront.net`
5. Ensure "Proxied" is enabled
6. Save (propagation: 5-10 min with Cloudflare)

---

## ✅ Verification Checklist

- [ ] Domain purchased and accessible
- [ ] SSL certificate created in AWS ACM
- [ ] Certificate validation CNAME added to DNS
- [ ] Certificate status shows "Issued" (wait 5-15 min)
- [ ] CloudFront distribution updated with domain names
- [ ] CloudFront distribution updated with SSL certificate
- [ ] DNS records added at registrar
- [ ] `nslookup stock.os` shows CloudFront domain
- [ ] `curl -I https://stock.os` returns 200/301
- [ ] Browser shows your SSL certificate (not CloudFront's)
- [ ] API works at `https://api.stock.os/health`
- [ ] Frontend loads at `https://stock.os`

---

## 🐛 Troubleshooting

### Domain resolves but HTTPS errors
```
✗ Problem: SSL certificate mismatch
✓ Solution: 
  1. Ensure CloudFront has your ACM certificate
  2. Ensure domain is in Certificate's SAN list
  3. Wait 15+ minutes for propagation
  4. Clear browser cache
  5. Test from new incognito window
```

### "Certificate pending" in ACM
```
✗ Problem: DNS validation not completed
✓ Solution:
  1. Check you added correct CNAME to DNS
  2. Verify with: nslookup _XXXX.stock.os
  3. AWS shows validation up to 72 hours
  4. Try validating again in ACM console
```

### DNS not resolving
```
✗ Problem: DNS records not updated yet
✓ Solution:
  1. Check registrar shows your CNAME records
  2. Use: nslookup -type=CNAME stock.os
  3. DNS propagates in 5-30 minutes globally
  4. Use online DNS checker: whatsmydns.net
```

### CloudFront still shows old domain
```
✗ Problem: CloudFront distribution not updated
✓ Solution:
  1. Refresh CloudFront console
  2. Ensure "Status" shows "Deployed" (might take 10-15 min)
  3. Create cache invalidation: /* 
  4. Clear browser cache
```

---

## 📊 URL Mapping After Setup

| Purpose | URL |
|---------|-----|
| Website | `https://stock.os` |
| API | `https://api.stock.os` or `https://stock.os/api` |
| Admin login | `https://stock.os/#/login` |
| Dashboard | `https://stock.os/#/dashboard` |
| Health check | `https://api.stock.os/health` or `https://stock.os/api/auth/health` |

---

## 💰 Cost Impact

| Item | Cost | Notes |
|------|------|-------|
| Domain | $12-15/year | Varies by registrar and TLD |
| SSL Certificate | FREE | AWS ACM provides free certificates |
| CloudFront | Variable | No extra charge for custom domain |
| Route 53 | $0.50/zone/month | Optional - use any registrar |

---

## 🚀 Advanced: Multiple Domains

If you want `inventory.stock.os` or other subdomains:

```bash
# Add more Subject Alternative Names to certificate
aws acm request-certificate \
  --domain-name stock.os \
  --subject-alternative-names "*.stock.os" \
  --validation-method DNS

# *.stock.os wildcard covers all subdomains
# inventory.stock.os, api.stock.os, www.stock.os, etc.

# Then in CloudFront, add:
# - stock.os
# - *.stock.os
```

---

## 📞 Support

- **Stuck?** Check [AWS_DEPLOYMENT_GUIDE.md - Phase 6](../AWS_DEPLOYMENT_GUIDE.md#phase-6-custom-domain--verification)
- **General AWS Help**: [AWS Documentation](https://docs.aws.amazon.com/route53/)
- **SSL Issues**: [ACM Troubleshooting](https://docs.aws.amazon.com/acm/latest/userguide/troubleshooting.html)

---

**Time: ~15-30 minutes | Difficulty: ⭐⭐ (Intermediate) | Cost: $12-15/year domain**

Next: Follow deployment guide for production setup!
