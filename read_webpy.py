import paramiko, sys

vps_host = "155.212.210.252"; vps_user = "root"; vps_pass = "2db**gLPoyDx"

c = paramiko.SSHClient(); c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect(vps_host, username=vps_user, password=vps_pass, timeout=15)

_, out, _ = c.exec_command("cat /root/backend/routers/web.py")
content = out.read().decode("utf-8")
c.close()

sys.stdout.buffer.write(content.encode("utf-8"))
