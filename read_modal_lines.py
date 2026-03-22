import paramiko, sys

vps_host = "155.212.210.252"; vps_user = "root"; vps_pass = "2db**gLPoyDx"
beget_host = "REDACTED_HOST"; beget_user = "REDACTED_USER"; beget_pass = "REDACTED_PASSWORD"

jump = paramiko.SSHClient(); jump.set_missing_host_key_policy(paramiko.AutoAddPolicy())
jump.connect(vps_host, username=vps_user, password=vps_pass, timeout=15)
channel = jump.get_transport().open_channel("direct-tcpip", (beget_host, 22), ("127.0.0.1", 0))
target = paramiko.SSHClient(); target.set_missing_host_key_policy(paramiko.AutoAddPolicy())
target.connect(beget_host, username=beget_user, password=beget_pass, sock=channel, timeout=15)

_, out, _ = target.exec_command("sed -n '637,655p' /home/k/katalist/gryazworld.ru/public_html/index.html | cat -A")
sys.stdout.buffer.write(out.read())

target.close(); jump.close()
