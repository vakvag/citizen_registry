# ==============================================================================
# Preparation Phase: Output Values
# ==============================================================================

output "db_ami_id" {
  description = "ID of the created AMI for DBMS"
  value       = aws_ami_from_instance.db_ami.id
}

output "app_ami_id" {
  description = "ID of the created AMI for RESTful service"
  value       = aws_ami_from_instance.app_ami.id
}
