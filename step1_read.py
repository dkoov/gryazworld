#!/usr/bin/env python3
"""Step 1: Read existing files from both servers"""
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

def read_remote_file(client, path):
    sftp = client.open_sftp()
    try:
        with sftp.open(path, 'r') as f:
            return f.read().decode('utf-8')
    finally:
        sftp.close()

def run_command(client, cmd):
    stdin, stdout, stderr = client.exec_command(cmd)
    out = stdout.read().decode('utf-8')
    err = stderr.read().decode('utf-8')
    return out, err

print("=" * 60)
print("Connecting to VPS...")
vps = connect_vps()
print("Connected to VPS!")

# Read web.py
print("\nReading /root/backend/routers/web.py ...")
web_py = read_remote_file(vps, "/root/backend/routers/web.py")
print("--- web.py START ---")
print(web_py)
print("--- web.py END ---")

# Read DB schema
print("\nReading DB schema...")
schema_out, schema_err = run_command(vps, 'sqlite3 /root/backend/serverpanel.db ".schema"')
print("--- DB Schema START ---")
print(schema_out)
if schema_err:
    print("STDERR:", schema_err)
print("--- DB Schema END ---")

# Also look for model files
print("\nLooking for model files...")
models_list_out, _ = run_command(vps, 'find /root/backend -name "*.py" | head -30')
print("Python files:", models_list_out)

# Try to read models.py or similar
for candidate in ["/root/backend/models.py", "/root/backend/database.py", "/root/backend/db.py"]:
    try:
        content = read_remote_file(vps, candidate)
        print(f"\n--- {candidate} START ---")
        print(content)
        print(f"--- {candidate} END ---")
    except Exception as e:
        print(f"Not found: {candidate} ({e})")

print("\n" + "=" * 60)
print("Connecting to Beget via VPS jump host...")
beget = connect_beget_via_jump(vps)
print("Connected to Beget!")

# Read index.html
BEGET_HTML_PATH = "/home/k/katalist/gryazworld.ru/public_html/index.html"
print(f"\nReading {BEGET_HTML_PATH} ...")
index_html = read_remote_file(beget, BEGET_HTML_PATH)
print("--- index.html START ---")
print(index_html)
print("--- index.html END ---")

beget.close()
vps.close()
print("\nDone!")
