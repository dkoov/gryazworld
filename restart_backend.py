import paramiko, sys

def vps_run(cmd):
    c = paramiko.SSHClient()
    c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    c.connect('155.212.210.252', username='root', password='2db**gLPoyDx', timeout=15)
    stdin, stdout, stderr = c.exec_command(cmd)
    out = stdout.read()
    err = stderr.read()
    c.close()
    return out, err

# Restart systemd service
out, err = vps_run(b'systemctl restart grworld-backend.service && echo "systemd restart OK"')
sys.stdout.buffer.write(b"=== systemctl restart ===\n" + out + err)

# Check status
out, err = vps_run(b'systemctl is-active grworld-backend.service && systemctl status grworld-backend.service | head -15')
sys.stdout.buffer.write(b"\n=== status ===\n" + out + err)
