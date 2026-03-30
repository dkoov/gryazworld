#!/bin/bash
# GryazWorld backup script
BACKUP_DIR="/opt/backups/gryazworld"
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p "$BACKUP_DIR"

# Backup SQLite database
cp /opt/gryazworld/data/serverpanel.db "$BACKUP_DIR/serverpanel_$DATE.db"

# Keep only last 28 backups (7 days * 4 per day)
ls -t "$BACKUP_DIR"/serverpanel_*.db | tail -n +29 | xargs -r rm --

echo "[$(date)] Backup created: serverpanel_$DATE.db"
