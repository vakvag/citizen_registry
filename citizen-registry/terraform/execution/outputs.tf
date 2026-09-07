# ==============================================================================
# Execution Phase: Output Values
# ==============================================================================

output "load_balancer_dns_name" {
  description = "Public DNS name of the Application Load Balancer (ALB) to access the RESTful service"
  value       = aws_lb.citizen_alb.dns_name
}

output "db_instance_private_ip" {
  description = "Private IP address of the DBMS Instance"
  value       = aws_instance.db_instance.private_ip
}

output "app_instances_private_ips" {
  description = "Private IP addresses of the 3 RESTful service instances"
  value       = aws_instance.app_instances[*].private_ip
}

output "app_instances_public_ips" {
  description = "Public IP addresses of the 3 RESTful service instances (for SSH/debugging)"
  value       = aws_instance.app_instances[*].public_ip
}
