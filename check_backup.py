import paramiko, sys

vps_host = "155.212.210.252"; vps_user = "root"; vps_pass = "2db**gLPoyDx"

c = paramiko.SSHClient(); c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect(vps_host, username=vps_user, password=vps_pass, timeout=15)

cmds = [
    "cd /root/backend && git log --oneline -5 2>/dev/null || echo NO_GIT",
    "ls /root/backend/routers/",
    "ls /root/backend/*.bak 2>/dev/null || echo NO_BAK",
    "wc -l /root/backend/routers/web.py",
]

for cmd in cmds:
    _, out, _ = c.exec_command(cmd)
    result = out.read().decode("utf-8").strip()
    sys.stdout.buffer.write((f"$ {cmd}\n{result}\n\n").encode("utf-8"))

c.close()
