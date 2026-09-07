#!/bin/bash
# ==============================================================================
# Preparation Phase: Java 17, Maven & Spring Boot App Preparation Script
# ==============================================================================

set -e

echo "=== [REST App Preparation] Updating System Packages ==="
sudo apt-get update -y
sudo apt-get install -y openjdk-17-jdk maven git wget curl

echo "=== [REST App Preparation] Creating Application Directory ==="
sudo mkdir -p /opt/citizen-registry
sudo chown -R ubuntu:ubuntu /opt/citizen-registry

echo "=== [REST App Preparation] Preparing Systemd Service Template ==="
cat << 'EOF' | sudo tee /etc/systemd/system/citizen-registry.service
[Unit]
Description=Citizen Registry RESTful Spring Boot Service
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/opt/citizen-registry
ExecStart=/usr/bin/java -DSPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL} -DSPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME} -DSPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD} -DSPRING_DATASOURCE_DRIVER=com.mysql.cj.jdbc.Driver -DSPRING_JPA_DIALECT=org.hibernate.dialect.MySQLDialect -DSPRING_JPA_DDL_AUTO=update -jar /opt/citizen-registry/citizen-service-app.jar
Restart=always
RestartSec=10
EnvironmentFile=/etc/default/citizen-registry

[Install]
WantedBy=multi-user.target
EOF

sudo mkdir -p /etc/default
cat << 'EOF' | sudo tee /etc/default/citizen-registry
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/citizendb
SPRING_DATASOURCE_USERNAME=citizenuser
SPRING_DATASOURCE_PASSWORD=CitizenDBPass2026!
EOF

echo "=== [REST App Preparation] REST App Machine Setup Completed Successfully ==="
