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

# Check how backend is running
out, err = vps_run(b'systemctl list-units --type=service --state=running | grep -iE "back|gryaz|uvicorn|fastapi"')
sys.stdout.buffer.write(b"=== systemctl ===\n" + out + err)

out, err = vps_run(b'pm2 list 2>/dev/null || echo "no pm2"')
sys.stdout.buffer.write(b"\n=== pm2 ===\n" + out)

out, err = vps_run(b'ls /root/backend/ && cat /root/backend/start.sh 2>/dev/null || echo "no start.sh"')
sys.stdout.buffer.write(b"\n=== backend files ===\n" + out)

out, err = vps_run(b'systemctl list-unit-files | grep -iE "back|gryaz|uvicorn"')
sys.stdout.buffer.write(b"\n=== unit files ===\n" + out)
