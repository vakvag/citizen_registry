# Terraform Infrastructure Models (AWS) - Assignment l3689

This folder contains the infrastructure configuration models written in **HCL (Terraform)** for automated cloud management of the **Spring Boot RESTful service** (Citizen Registry) and its underlying **Database Management System (DBMS - MySQL 8)** on AWS.

---

## Directory Structure

```
terraform/
├── preparation/                   # (50/100) Preparation Phase - Image Creation (AMIs)
│   ├── main.tf                    # HCL definitions for AMI creation
│   ├── variables.tf               # Input variables
│   ├── outputs.tf                 # Generated AMI IDs
│   └── scripts/
│       ├── db_prep.sh             # MySQL 8 installation & setup script
│       └── app_prep.sh            # Java 17 & Spring Boot Service setup script
│
└── execution/                     # (50/100) Execution & Destruction Phase - Deployment
    ├── main.tf                    # HCL definitions (1x DB, 3x REST Instances, ALB, SGs, VPC)
    ├── variables.tf               # Input variables
    ├── outputs.tf                 # Load Balancer DNS name & instance IPs
    ├── terraform.tfvars.example   # Example variables file
    └── scripts/
        ├── db_init.sh             # DBMS instance startup script
        └── app_init.sh            # REST instances startup & DB interconnection script
```

---

## Usage Instructions

### 1. Prerequisites
- **Terraform CLI** installed (v1.0.0+)
- **AWS CLI** installed and configured (`aws configure`) or environment variables set (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`).
- An active AWS SSH Key Pair (e.g. `my-key.pem`).

---

### 2. Preparation Phase
Automates the creation of Virtual Machine Images (AMIs) for the DBMS and the RESTful service.

```bash
cd terraform/preparation

# Initialize Terraform AWS Provider
terraform init

# Validate & Plan
terraform plan

# Apply and create the AMIs
terraform apply -auto-approve
```
*After completion, the generated AMI IDs will be displayed in the outputs (`db_ami_id` & `app_ami_id`).*

---

### 3. Execution Phase
Deploys the production infrastructure:
- 1x DBMS Instance (MySQL)
- 3x RESTful Service Instances
- 1x Application Load Balancer (ALB)
- Security Groups & Interconnection

```bash
cd ../execution

# Initialize
terraform init

# Create variable file (optional)
cp terraform.tfvars.example terraform.tfvars

# Apply and deploy infrastructure
terraform apply -auto-approve
```

*After deployment, the Load Balancer DNS Name will be shown in the outputs:*
```
Outputs:
load_balancer_dns_name = "citizen-alb-123456789.eu-central-1.elb.amazonaws.com"
```

---

### 4. Destruction Phase
To destroy and tear down all deployed cloud infrastructure resources:

```bash
cd terraform/execution
terraform destroy -auto-approve
```

To destroy the preparation AMIs as well:
```bash
cd ../preparation
terraform destroy -auto-approve
```
