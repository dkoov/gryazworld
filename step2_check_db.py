#!/usr/bin/env python3
"""Check DB structure via VPS"""
import paramiko
import sys

VPS_HOST = "155.212.210.252"
VPS_USER = "root"
VPS_PASS = "2db**gLPoyDx"

def connect_vps():
    client = paramiko.SSHClient()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    client.connect(VPS_HOST, username=VPS_USER, password=VPS_PASS, timeout=30)
    return client

def run_command(client, cmd):
    stdin, stdout, stderr = client.exec_command(cmd)
    out = stdout.read().decode('utf-8', errors='replace')
    err = stderr.read().decode('utf-8', errors='replace')
    return out, err

sys.stdout.reconfigure(encoding='utf-8')

vps = connect_vps()

# Check schema using Python directly on VPS
schema_cmd = """cd /root/backend && python3 -c "
import sqlite3
conn = sqlite3.connect('serverpanel.db')
cur = conn.cursor()
cur.execute(\"SELECT name FROM sqlite_master WHERE type='table'\")
tables = cur.fetchall()
print('Tables:', tables)
for t in tables:
    tname = t[0]
    cur.execute(f'PRAGMA table_info({tname})')
    cols = cur.fetchall()
    print(f'\\nTable {tname}:')
    for c in cols:
        print(f'  {c}')
    cur.execute(f'SELECT * FROM {tname} LIMIT 2')
    rows = cur.fetchall()
    print(f'  Sample rows: {rows}')
conn.close()
"
"""
out, err = run_command(vps, schema_cmd)
print("DB Info:")
print(out)
if err:
    print("STDERR:", err)

# Also check if there's a sessions table or is_online field
check_cmd = """cd /root/backend && python3 -c "
import sqlite3
conn = sqlite3.connect('serverpanel.db')
cur = conn.cursor()
cur.execute(\"SELECT name FROM sqlite_master WHERE type='table'\")
tables = [t[0] for t in cur.fetchall()]
print('All tables:', tables)
# Check players table specifically
cur.execute('PRAGMA table_info(players)')
cols = cur.fetchall()
print('Players columns:', [c[1] for c in cols])
conn.close()
"
"""
out2, err2 = run_command(vps, check_cmd)
print("\nDetail check:")
print(out2)
if err2:
    print("STDERR:", err2)

# Check routers directory for any session/online tracking
out3, _ = run_command(vps, "ls /root/backend/routers/ && grep -r 'is_online\\|session\\|online' /root/backend --include='*.py' -l 2>/dev/null")
print("\nFiles with online/session refs:", out3)

vps.close()
