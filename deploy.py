import paramiko
import os
import stat

HOST = "155.212.210.252"
USER = "root"
PASS = "2db**gLPoyDx"
LOCAL_DIR = r"C:\Users\IrMine\Desktop\grworld\backend"
REMOTE_DIR = "/root/backend"

SYSTEMD_SERVICE = """\
[Unit]
Description=GryazWorld FastAPI Backend
After=network.target

[Service]
User=root
WorkingDirectory=/root/backend
ExecStart=/usr/local/bin/uvicorn main:app --host 0.0.0.0 --port 8000
Restart=always
RestartSec=5
Environment=PYTHONUNBUFFERED=1

[Install]
WantedBy=multi-user.target
"""

def run(client, cmd, desc=""):
    print(f"  >> {desc or cmd}")
    stdin, stdout, stderr = client.exec_command(cmd, get_pty=True)
    out = stdout.read().decode()
    err = stderr.read().decode()
    if out.strip():
        print(out.strip().encode("cp1251", errors="replace").decode("cp1251"))
    if err.strip():
        print("[err]", err.strip().encode("cp1251", errors="replace").decode("cp1251"))
    return out

def upload_dir(sftp, local_dir, remote_dir):
    try:
        sftp.mkdir(remote_dir)
    except:
        pass

    for item in os.listdir(local_dir):
        local_path = os.path.join(local_dir, item)
        remote_path = remote_dir + "/" + item
        if os.path.isdir(local_path):
            upload_dir(sftp, local_path, remote_path)
        else:
            print(f"  Uploading: {item}")
            sftp.put(local_path, remote_path)

print(f"Connecting to {HOST}...")
client = paramiko.SSHClient()
client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
client.connect(HOST, username=USER, password=PASS, timeout=30)
print("Connected!\n")

print("[1/5] Uploading backend files...")
sftp = client.open_sftp()
upload_dir(sftp, LOCAL_DIR, REMOTE_DIR)
sftp.close()
print("Done.\n")

print("[2/5] Installing Python3 & pip...")
run(client, "apt-get update -qq", "apt update")
run(client, "apt-get install -y python3 python3-pip python3-venv", "install python3 pip venv")
print("Done.\n")

print("[3/5] Creating venv and installing requirements...")
run(client, f"python3 -m venv {REMOTE_DIR}/venv", "create venv")
run(client, f"{REMOTE_DIR}/venv/bin/pip install -q -r {REMOTE_DIR}/requirements.txt", "pip install requirements")
print("Done.\n")

print("[4/5] Creating systemd service...")
service_content = SYSTEMD_SERVICE.replace("/usr/local/bin/uvicorn", f"{REMOTE_DIR}/venv/bin/uvicorn")

sftp = client.open_sftp()
with sftp.open("/etc/systemd/system/grworld-backend.service", "w") as f:
    f.write(service_content)
sftp.close()

run(client, "systemctl daemon-reload", "daemon-reload")
run(client, "systemctl enable grworld-backend", "enable service")
run(client, "systemctl restart grworld-backend", "start service")
print("Done.\n")

print("[5/5] Configuring ufw firewall...")
run(client, "ufw allow 8000/tcp", "allow port 8000")
run(client, "ufw allow OpenSSH", "allow SSH")
run(client, "ufw --force enable", "enable ufw")
print("Done.\n")

print("Checking service status...")
run(client, "systemctl status grworld-backend --no-pager -l", "service status")

client.close()
print("\nDeployment complete! Backend is running at http://148.253.209.198:8000")
