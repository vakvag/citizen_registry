#!/bin/bash
# ==============================================================================
# Execution Phase: REST App Instance Bootstrapping & DBMS Interconnection Script
# ==============================================================================

set -e

# Environment variables dynamically populated from Terraform user_data templatefile
DB_PRIVATE_IP="${db_private_ip}"
DB_NAME="${db_name}"
DB_USER="${db_user}"
DB_PASS="${db_pass}"

echo "=== [REST App Execution] Configuring Database Connection String ==="
sudo mkdir -p /etc/default
cat << EOF | sudo tee /etc/default/citizen-registry
SPRING_DATASOURCE_URL=jdbc:mysql://${DB_PRIVATE_IP}:3306/${DB_NAME}?useSSL=false&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=${DB_USER}
SPRING_DATASOURCE_PASSWORD=${DB_PASS}
EOF

echo "=== [REST App Execution] Starting Citizen Registry Service ==="
sudo systemctl daemon-reload
sudo systemctl enable citizen-registry
sudo systemctl restart citizen-registry || true

echo "=== [REST App Execution] Service Bootstrapping Complete ==="
