#!/bin/bash
# ==============================================================================
# Execution Phase: DBMS Instance Initialization Script
# ==============================================================================

set -e

echo "=== [DBMS Execution] Ensuring MySQL Service Status ==="
sudo systemctl enable mysql
sudo systemctl restart mysql

echo "=== [DBMS Execution] DBMS Instance Ready to Accept Connections ==="
