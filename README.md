# Citizen Registry RESTful Service & Terraform AWS Infrastructure

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Terraform](https://img.shields.io/badge/Terraform->=1.0.0-blue.svg)](https://www.terraform.io/)
[![AWS](https://img.shields.io/badge/AWS-EC2%20%7C%20ALB%20%7C%20VPC-orange.svg)](https://aws.amazon.com/)

A comprehensive Spring Boot RESTful application (**Citizen Registry / Μητρώο Πολιτών**) integrated with automated **Infrastructure as Code (IaC)** using **Terraform** and **Amazon Web Services (AWS)**. Developed for the course **l3689 - Cloud Computing and Cross-Cloud Resource Management**.

---

## 🏗️ Architecture Overview

The system architecture automates image preparation and full lifecycle management of the cloud application:

```
                          [ Internet Traffic ]
                                   │
                                   ▼
                    ┌──────────────────────────────┐
                    │ Application Load Balancer    │ (Port 80)
                    │ (AWS ALB - Public Ingress)   │
                    └──────────────┬───────────────┘
                                   │
         ┌─────────────────────────┼─────────────────────────┐
         │                         │                         │
         ▼                         ▼                         ▼
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│ REST Service 1  │       │ REST Service 2  │       │ REST Service 3  │ (Port 8080)
│ (Spring Boot)   │       │ (Spring Boot)   │       │ (Spring Boot)   │
└────────┬────────┘       └────────┬────────┘       └────────┬────────┘
         │                         │                         │
         └─────────────────────────┼─────────────────────────┘
                                   │
                                   ▼
                        ┌─────────────────────┐
                        │ DBMS Instance       │ (MySQL 8 - Port 3306)
                        │ (Standalone Server) │
                        └─────────────────────┘
```

---

## 📁 Repository Structure

- **[`citizen-registry/`](./citizen-registry/)**: Main Maven multi-module Spring Boot application (`citizen-domain`, `citizen-service`).
- **[`citizen-registry/terraform/`](./citizen-registry/terraform/)**: Terraform infrastructure models:
  - **`preparation/`** (50/100): Automated creation of custom AMIs for DBMS and REST Service.
  - **`execution/`** (50/100): Full infrastructure deployment (1x DB, 3x REST Instances, ALB, Security Groups, VPC).
- **[`citizen-registry/terraform/README.md`](./citizen-registry/terraform/README.md)**: Detailed step-by-step CLI usage guide.

---

## 🚀 Quick Start & Deployment Guide

### 1. Build Spring Boot Application
```bash
cd citizen-registry
mvn clean compile
```

### 2. Terraform Preparation Phase (AMI Building)
```bash
cd citizen-registry/terraform/preparation
terraform init
terraform apply
```

### 3. Terraform Execution Phase (Deployment)
```bash
cd ../execution
terraform init
terraform apply
```

### 4. Destruction Phase
```bash
cd citizen-registry/terraform/execution
terraform destroy
```

---

## 🔒 Security Group Firewall Rules

1. **ALB Security Group**: Public HTTP (`Port 80`) access from `0.0.0.0/0`.
2. **App Security Group**: Ingress on `Port 8080` restricted **ONLY** to traffic coming from the ALB Security Group. SSH (`Port 22`) for admin.
3. **DBMS Security Group**: Ingress on MySQL (`Port 3306`) restricted **ONLY** to traffic coming from the REST App Security Group. SSH (`Port 22`) for admin.