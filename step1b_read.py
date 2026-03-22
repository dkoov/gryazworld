#!/usr/bin/env python3
"""Step 1b: Save files to disk for inspection"""
import paramiko
import sys

# VPS credentials
VPS_HOST = "155.212.210.252"
VPS_USER = "root"
VPS_PASS = "2db**gLPoyDx"

# Beget credentials
BEGET_HOST = "REDACTED_HOST"
BEGET_USER = "REDACTED_USER"
BEGET_PASS = "REDACTED_PASSWORD"

def connect_vps():
    client = paramiko.SSHClient()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    client.connect(VPS_HOST, username=VPS_USER, password=VPS_PASS, timeout=30)
    return client

def connect_beget_via_jump(vps_client):
    transport = vps_client.get_transport()
    dest_addr = (BEGET_HOST, 22)
    src_addr = (VPS_HOST, 22)
    channel = transport.open_channel("direct-tcpip", dest_addr, src_addr)
    beget_client = paramiko.SSHClient()
    beget_client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    beget_client.connect(BEGET_HOST, username=BEGET_USER, password=BEGET_PASS, sock=channel, timeout=30)
    return beget_client

def download_file(client, remote_path, local_path):
    sftp = client.open_sftp()
    try:
        sftp.get(remote_path, local_path)
    finally:
        sftp.close()

def run_command(client, cmd):
    stdin, stdout, stderr = client.exec_command(cmd)
    out = stdout.read().decode('utf-8', errors='replace')
    err = stderr.read().decode('utf-8', errors='replace')
    return out, err

sys.stdout.reconfigure(encoding='utf-8')

print("Connecting to VPS...")
vps = connect_vps()
print("Connected!")

# Download web.py
download_file(vps, "/root/backend/routers/web.py", "C:/Users/IrMine/Desktop/grworld/web_remote.py")
print("Downloaded web.py -> web_remote.py")

# Get full DB schema
schema_out, schema_err = run_command(vps, 'sqlite3 /root/backend/serverpanel.db ".schema"')
with open("C:/Users/IrMine/Desktop/grworld/schema.txt", "w", encoding="utf-8") as f:
    f.write(schema_out)
    if schema_err:
        f.write("\nSTDERR: " + schema_err)
print("Downloaded DB schema -> schema.txt")

# Check if is_online or session tracking exists
check_out, _ = run_command(vps, 'sqlite3 /root/backend/serverpanel.db "SELECT * FROM players LIMIT 3;" 2>&1')
with open("C:/Users/IrMine/Desktop/grworld/players_sample.txt", "w", encoding="utf-8") as f:
    f.write(check_out)
print("Got players sample -> players_sample.txt")

print("\nConnecting to Beget...")
beget = connect_beget_via_jump(vps)
print("Connected to Beget!")

BEGET_HTML_PATH = "/home/k/katalist/gryazworld.ru/public_html/index.html"
download_file(beget, BEGET_HTML_PATH, "C:/Users/IrMine/Desktop/grworld/index_remote.py.html")
print("Downloaded index.html -> index_remote.py.html")

beget.close()
vps.close()
print("\nAll files downloaded!")
