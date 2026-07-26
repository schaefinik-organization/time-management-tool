#!/bin/bash
set -e

ACTION=$1       # backup, deploy, rollback
ENVIRONMENT=$2  # dev, prod

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="./backups/$ENVIRONMENT"
mkdir -p "$BACKUP_DIR"

if [ "$ACTION" == "backup" ]; then
    echo "Creating backup for $ENVIRONMENT..."
    docker exec time-mgmt-db pg_dump -U postgres timetracker > "$BACKUP_DIR/db_backup_$TIMESTAMP.sql"
    # Hier könnte man auch das aktuelle JAR sichern
    cp backend/build/libs/*.jar "$BACKUP_DIR/app_backup_$TIMESTAMP.jar"
    echo "Backup saved: $BACKUP_DIR/db_backup_$TIMESTAMP.sql"

elif [ "$ACTION" == "deploy" ]; then
    echo "Starting Deployment for $ENVIRONMENT..."
    # 1. Backup zuerst
    $0 backup $ENVIRONMENT
    # 2. Deployment Logik (z.B. rsync zu Server oder Docker restart)
    echo "Deployment to $ENVIRONMENT finished."

elif [ "$ACTION" == "rollback" ]; then
    echo "Rolling back $ENVIRONMENT..."
    LATEST_SQL=$(ls -t $BACKUP_DIR/*.sql | head -1)
    echo "Restoring $LATEST_SQL..."
    cat "$LATEST_SQL" | docker exec -i time-mgmt-db psql -U postgres timetracker
    echo "Rollback finished."
fi