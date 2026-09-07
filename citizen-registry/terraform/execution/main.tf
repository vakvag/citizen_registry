# ==============================================================================
# Execution & Destruction Phase (50/100)
# Infrastructure Deployment: 1x DBMS Instance, 3x REST App Instances, ALB & Security Rules
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

# ------------------------------------------------------------------------------
# 1. NETWORKING (VPC, Subnets, Internet Gateway & Route Tables)
# ------------------------------------------------------------------------------

data "aws_availability_zones" "available" {
  state = "available"
}

resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = "citizen-vpc"
  }
}

resource "aws_internet_gateway" "gw" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "citizen-igw"
  }
}

# Subnet 1 (AZ a)
resource "aws_subnet" "public_a" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = data.aws_availability_zones.available.names[0]
  map_public_ip_on_launch = true

  tags = {
    Name = "citizen-subnet-public-a"
  }
}

# Subnet 2 (AZ b - Required for AWS Application Load Balancer)
resource "aws_subnet" "public_b" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = data.aws_availability_zones.available.names[1]
  map_public_ip_on_launch = true

  tags = {
    Name = "citizen-subnet-public-b"
  }
}

resource "aws_route_table" "public_rt" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.gw.id
  }

  tags = {
    Name = "citizen-public-rt"
  }
}

resource "aws_route_table_association" "a" {
  subnet_id      = aws_subnet.public_a.id
  route_table_id = aws_route_table.public_rt.id
}

resource "aws_route_table_association" "b" {
  subnet_id      = aws_subnet.public_b.id
  route_table_id = aws_route_table.public_rt.id
}

# ------------------------------------------------------------------------------
# 2. SECURITY GROUPS & FIREWALL RULES
# ------------------------------------------------------------------------------

# (a) Security Group for Load Balancer (ALB): Allows public HTTP (Port 80) access
resource "aws_security_group" "alb_sg" {
  name        = "citizen-alb-sg"
  description = "Allow public inbound HTTP access to Load Balancer"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "Public HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "citizen-alb-sg"
  }
}

# (b) Security Group for the 3 RESTful Service Instances:
# Allows Port 8080 ONLY from the ALB Security Group, and SSH Port 22 for management
resource "aws_security_group" "app_sg" {
  name        = "citizen-app-sg"
  description = "Allow inbound traffic from ALB only"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "REST Service Port from ALB"
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.alb_sg.id]
  }

  ingress {
    description = "SSH Access for Administration"
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

  tags = {
    Name = "citizen-app-sg"
  }
}

# (c) Security Group for the DBMS Instance:
# Allows MySQL (Port 3306) ONLY from the REST Service Security Group
resource "aws_security_group" "db_sg" {
  name        = "citizen-db-sg"
  description = "Allow MySQL traffic from REST app instances only"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "MySQL Access from REST Service instances"
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.app_sg.id]
  }

  ingress {
    description = "SSH Access for Administration"
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

  tags = {
    Name = "citizen-db-sg"
  }
}

# ------------------------------------------------------------------------------
# 3. AMI LOOKUPS (If not specified explicitly in variables)
# ------------------------------------------------------------------------------

data "aws_ami" "db_ami_latest" {
  most_recent = true
  owners      = ["self"]

  filter {
    name   = "name"
    values = ["citizen-db-ami*"]
  }
}

data "aws_ami" "app_ami_latest" {
  most_recent = true
  owners      = ["self"]

  filter {
    name   = "name"
    values = ["citizen-app-ami*"]
  }
}

locals {
  selected_db_ami  = var.db_ami_id != "" ? var.db_ami_id : data.aws_ami.db_ami_latest.id
  selected_app_ami = var.app_ami_id != "" ? var.app_ami_id : data.aws_ami.app_ami_latest.id
}

# ------------------------------------------------------------------------------
# 4. DBMS INSTANCE (1x DBMS INSTANCE)
# ------------------------------------------------------------------------------

resource "aws_instance" "db_instance" {
  ami                         = local.selected_db_ami
  instance_type               = var.instance_type
  subnet_id                   = aws_subnet.public_a.id
  vpc_security_group_ids      = [aws_security_group.db_sg.id]
  key_name                    = var.key_name != "" ? var.key_name : null
  associate_public_ip_address = true

  user_data = file("${path.module}/scripts/db_init.sh")

  tags = {
    Name = "citizen-db-instance-1"
    Role = "Database-Server"
  }
}

# ------------------------------------------------------------------------------
# 5. RESTful SERVICE INSTANCES (3x REST APP INSTANCES)
# ------------------------------------------------------------------------------

resource "aws_instance" "app_instances" {
  count                       = var.app_instance_count
  ami                         = local.selected_app_ami
  instance_type               = var.instance_type
  subnet_id                   = count.index % 2 == 0 ? aws_subnet.public_a.id : aws_subnet.public_b.id
  vpc_security_group_ids      = [aws_security_group.app_sg.id]
  key_name                    = var.key_name != "" ? var.key_name : null
  associate_public_ip_address = true

  # Dynamic interconnection: Pass the DBMS instance private IP to the app instances
  user_data = templatefile("${path.module}/scripts/app_init.sh", {
    db_private_ip = aws_instance.db_instance.private_ip
    db_name       = var.db_name
    db_user       = var.db_user
    db_pass       = var.db_password
  })

  tags = {
    Name = "citizen-app-instance-${count.index + 1}"
    Role = "REST-API-Server"
  }

  depends_on = [aws_instance.db_instance]
}

# ------------------------------------------------------------------------------
# 6. APPLICATION LOAD BALANCER (AWS ALB)
# ------------------------------------------------------------------------------

resource "aws_lb" "citizen_alb" {
  name               = "citizen-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb_sg.id]
  subnets            = [aws_subnet.public_a.id, aws_subnet.public_b.id]

  tags = {
    Name = "citizen-alb"
  }
}

resource "aws_lb_target_group" "citizen_tg" {
  name     = "citizen-tg"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = aws_vpc.main.id

  health_check {
    enabled             = true
    path                = "/h2-console"
    port                = "8080"
    protocol            = "HTTP"
    healthy_threshold   = 3
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    matcher             = "200,302,404"
  }
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.citizen_alb.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.citizen_tg.arn
  }
}

# Attach all 3 EC2 Instances to the Target Group
resource "aws_lb_target_group_attachment" "app_attachment" {
  count            = var.app_instance_count
  target_group_arn = aws_lb_target_group.citizen_tg.arn
  target_id        = aws_instance.app_instances[count.index].id
  port             = 8080
}
