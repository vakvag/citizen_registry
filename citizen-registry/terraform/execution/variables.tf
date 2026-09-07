# ==============================================================================
# Execution Phase: Input Variables
# ==============================================================================

variable "aws_region" {
  description = "AWS Region"
  type        = string
  default     = "eu-central-1"
}

variable "vpc_cidr" {
  description = "CIDR block for the application VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "db_ami_id" {
  description = "AMI ID for the DBMS (from preparation phase). If empty, the latest matching AMI will be fetched automatically."
  type        = string
  default     = ""
}

variable "app_ami_id" {
  description = "AMI ID for the RESTful service (from preparation phase). If empty, the latest matching AMI will be fetched automatically."
  type        = string
  default     = ""
}

variable "instance_type" {
  description = "Virtual machine instance type"
  type        = string
  default     = "t2.micro"
}

variable "key_name" {
  description = "SSH Key Pair name for instance access"
  type        = string
  default     = ""
}

variable "app_instance_count" {
  description = "Number of instances for the RESTful service (3 instances required per specification)"
  type        = number
  default     = 3
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "citizendb"
}

variable "db_user" {
  description = "Database username"
  type        = string
  default     = "citizenuser"
}

variable "db_password" {
  description = "Database password"
  type        = string
  default     = "CitizenDBPass2026!"
}
