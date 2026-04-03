#!/bin/bash
# Бэкап SQLite базы данных GryazWorld
# Использование: ./scripts/backup.sh
# Или через cron: 0 */6 * * * /opt/gryazworld/scripts/backup.sh

BACKUP_DIR="/opt/gryazworld/backups"
DB_PATH="/opt/gryazworld/data/serverpanel.db"
MAX_BACKUPS=30  # хранить последние 30 бэкапов

mkdir -p "$BACKUP_DIR"

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/serverpanel_$TIMESTAMP.db"

# sqlite3 .backup делает консистентный бэкап даже если база открыта
sqlite3 "$DB_PATH" ".backup '$BACKUP_FILE'"

if [ $? -eq 0 ]; then
    gzip "$BACKUP_FILE"
    echo "[OK] Бэкап создан: ${BACKUP_FILE}.gz ($(du -h "${BACKUP_FILE}.gz" | cut -f1))"
else
    echo "[ERROR] Не удалось создать бэкап"
    exit 1
fi

# Удалить старые бэкапы
cd "$BACKUP_DIR" && ls -t *.gz 2>/dev/null | tail -n +$((MAX_BACKUPS + 1)) | xargs -r rm --
echo "[OK] Бэкапов сейчас: $(ls "$BACKUP_DIR"/*.gz 2>/dev/null | wc -l)"
