# ==============================================================================
# Preparation Phase: Input Variables
# ==============================================================================

variable "aws_region" {
  description = "The AWS Region where the AMIs will be created."
  type        = string
  default     = "eu-central-1"
}

variable "instance_type" {
  description = "Virtual machine instance type for the preparation phase."
  type        = string
  default     = "t2.micro"
}

variable "key_name" {
  description = "SSH Key Pair name for accessing preparation instances."
  type        = string
  default     = ""
}

variable "vpc_id" {
  description = "VPC ID where temporary builders will run (optional, uses default VPC if empty)."
  type        = string
  default     = ""
}
