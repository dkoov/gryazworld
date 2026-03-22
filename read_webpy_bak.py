import paramiko, sys

vps_host = "155.212.210.252"; vps_user = "root"; vps_pass = "2db**gLPoyDx"

c = paramiko.SSHClient(); c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect(vps_host, username=vps_user, password=vps_pass, timeout=15)

_, out, _ = c.exec_command("wc -l /root/backend/routers/web.py.bak /root/backend/routers/web.py.bak2")
sys.stdout.buffer.write(out.read())

_, out, _ = c.exec_command("tail -30 /root/backend/routers/web.py.bak2")
sys.stdout.buffer.write(b"\n--- bak2 tail ---\n")
sys.stdout.buffer.write(out.read())

_, out, _ = c.exec_command("tail -30 /root/backend/routers/web.py.bak")
sys.stdout.buffer.write(b"\n--- bak tail ---\n")
sys.stdout.buffer.write(out.read())

c.close()
