#!/bin/bash
# ==============================================================================
# Preparation Phase: DBMS (MySQL 8) Installation & Configuration Script
# ==============================================================================

set -e

echo "=== [DBMS Preparation] Updating System Packages ==="
sudo apt-get update -y
sudo apt-get upgrade -y

echo "=== [DBMS Preparation] Installing MySQL Server ==="
sudo apt-get install -y mysql-server

echo "=== [DBMS Preparation] Configuring MySQL for Remote Connections ==="
# Configure MySQL to listen on all interfaces (0.0.0.0)
sudo sed -i 's/bind-address\s*=\s*127.0.0.1/bind-address = 0.0.0.0/' /etc/mysql/mysql.conf.d/mysqld.cnf

echo "=== [DBMS Preparation] Starting MySQL Service ==="
sudo systemctl enable mysql
sudo systemctl restart mysql

echo "=== [DBMS Preparation] Initializing Database & User ==="
sudo mysql -e "CREATE DATABASE IF NOT EXISTS citizendb;"
sudo mysql -e "CREATE USER IF NOT EXISTS 'citizenuser'@'%' IDENTIFIED BY 'CitizenDBPass2026!';"
sudo mysql -e "GRANT ALL PRIVILEGES ON citizendb.* TO 'citizenuser'@'%';"
sudo mysql -e "FLUSH PRIVILEGES;"

echo "=== [DBMS Preparation] DBMS Machine Setup Completed Successfully ==="
