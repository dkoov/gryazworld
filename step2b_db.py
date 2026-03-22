#!/usr/bin/env python3
"""Check DB structure and player.py via VPS"""
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

def read_remote_file(client, path):
    sftp = client.open_sftp()
    try:
        with sftp.open(path, 'r') as f:
            return f.read().decode('utf-8', errors='replace')
    finally:
        sftp.close()

sys.stdout.reconfigure(encoding='utf-8')

vps = connect_vps()

# Write a small Python script to server, run it
script = '''import sqlite3
conn = sqlite3.connect("/root/backend/serverpanel.db")
cur = conn.cursor()
cur.execute("SELECT name FROM sqlite_master WHERE type='table'")
tables = [t[0] for t in cur.fetchall()]
print("Tables:", tables)
for t in tables:
    cur.execute(f"PRAGMA table_info({t})")
    cols = cur.fetchall()
    print(f"Table {t}: cols =", [c[1] for c in cols])
    cur.execute(f"SELECT * FROM {t} LIMIT 1")
    rows = cur.fetchall()
    print(f"  sample: {rows}")
conn.close()
'''

sftp = vps.open_sftp()
with sftp.open('/tmp/check_db.py', 'w') as f:
    f.write(script)
sftp.close()

out, err = run_command(vps, 'python3 /tmp/check_db.py')
print("DB structure:")
print(out)
if err:
    print("ERR:", err)

# Read player.py router
print("\n--- player.py ---")
player_content = read_remote_file(vps, "/root/backend/routers/player.py")
print(player_content)

# Read web.py.bak to understand history
print("\n--- web.py.bak ---")
try:
    bak = read_remote_file(vps, "/root/backend/routers/web.py.bak")
    print(bak[:2000])
except:
    print("no bak")

vps.close()
