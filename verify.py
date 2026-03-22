#!/usr/bin/env python3
"""Verify new endpoints work"""
import paramiko
import sys

VPS_HOST = "155.212.210.252"
VPS_USER = "root"
VPS_PASS = "2db**gLPoyDx"

sys.stdout.reconfigure(encoding='utf-8')

def connect_vps():
    client = paramiko.SSHClient()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    client.connect(VPS_HOST, username=VPS_USER, password=VPS_PASS, timeout=30)
    return client

def run_command(client, cmd, timeout=15):
    stdin, stdout, stderr = client.exec_command(cmd, timeout=timeout)
    out = stdout.read().decode('utf-8', errors='replace')
    err = stderr.read().decode('utf-8', errors='replace')
    return out, err

vps = connect_vps()

print("Testing GET /web/server-stats ...")
out, err = run_command(vps, "curl -s http://localhost:8000/web/server-stats")
print("Response:", out)
if err:
    print("ERR:", err)

print("\nTesting GET /web/stats ...")
out2, err2 = run_command(vps, "curl -s http://localhost:8000/web/stats")
print("Response:", out2)
if err2:
    print("ERR:", err2)

# Verify DB column
print("\nVerifying DB column...")
check_script = '''import sqlite3
conn = sqlite3.connect("/root/backend/serverpanel.db")
cur = conn.cursor()
cur.execute("PRAGMA table_info(players)")
cols = [c[1] for c in cur.fetchall()]
print("Players columns:", cols)
cur.execute("SELECT id, nickname, is_online FROM players LIMIT 5")
rows = cur.fetchall()
print("Sample rows:", rows)
conn.close()
'''
sftp = vps.open_sftp()
with sftp.open('/tmp/verify_db.py', 'w') as f:
    f.write(check_script)
sftp.close()

out3, err3 = run_command(vps, 'python3 /tmp/verify_db.py')
print(out3)
if err3:
    print("ERR:", err3)

vps.close()
print("\nVerification complete!")
