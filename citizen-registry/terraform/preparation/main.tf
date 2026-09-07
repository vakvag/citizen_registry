# ==============================================================================
# Preparation Phase
# Automated virtual machine image creation (AWS AMIs)
# ==============================================================================

terraform {
  required_version = ">= 1.0.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# 1. Fetch official base Ubuntu 22.04 LTS AMI
data "aws_ami" "ubuntu" {
  most_recent = true
  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }
  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
  owners = ["099720109477"] # Canonical
}

# 2. Temporary Security Group for builder instances
resource "aws_security_group" "prep_builder_sg" {
  name        = "citizen-prep-builder-sg"
  description = "Security group for temporary AMI preparation instances"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# 3. Temporary EC2 Instance for DBMS preparation (DBMS Builder)
resource "aws_instance" "db_builder" {
  ami                         = data.aws_ami.ubuntu.id
  instance_type               = var.instance_type
  key_name                    = var.key_name != "" ? var.key_name : null
  vpc_security_group_ids      = [aws_security_group.prep_builder_sg.id]
  associate_public_ip_address = true

  user_data = file("${path.module}/scripts/db_prep.sh")

  tags = {
    Name = "citizen-db-builder"
    Role = "AMI-Preparation"
  }
}

# 4. Create AMI for DBMS from the configured builder instance
resource "aws_ami_from_instance" "db_ami" {
  name                    = "citizen-db-ami-${formatdate("YYYYMMDDhhmmss", timestamp())}"
  source_instance_id      = aws_instance.db_builder.id
  snapshot_without_reboot = true

  tags = {
    Name        = "citizen-db-ami"
    Environment = "Production-Ready"
    Component   = "DBMS"
  }

  depends_on = [aws_instance.db_builder]
}

# 5. Temporary EC2 Instance for RESTful Service preparation (App Builder)
resource "aws_instance" "app_builder" {
  ami                         = data.aws_ami.ubuntu.id
  instance_type               = var.instance_type
  key_name                    = var.key_name != "" ? var.key_name : null
  vpc_security_group_ids      = [aws_security_group.prep_builder_sg.id]
  associate_public_ip_address = true

  user_data = file("${path.module}/scripts/app_prep.sh")

  tags = {
    Name = "citizen-app-builder"
    Role = "AMI-Preparation"
  }
}

# 6. Create AMI for RESTful Service from the configured builder instance
resource "aws_ami_from_instance" "app_ami" {
  name                    = "citizen-app-ami-${formatdate("YYYYMMDDhhmmss", timestamp())}"
  source_instance_id      = aws_instance.app_builder.id
  snapshot_without_reboot = true

  tags = {
    Name        = "citizen-app-ami"
    Environment = "Production-Ready"
    Component   = "REST-Service"
  }

  depends_on = [aws_instance.app_builder]
}
