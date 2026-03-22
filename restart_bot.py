import paramiko

ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
ssh.connect('155.212.210.252', username='root', password='2db**gLPoyDx', timeout=15)

stdin, stdout, stderr = ssh.exec_command(
    'sudo systemctl restart grworld-bot.service 2>/dev/null || pm2 restart bot 2>/dev/null; '
    'sleep 2; '
    'systemctl is-active grworld-bot.service 2>/dev/null || pm2 list 2>/dev/null | grep bot'
)
print(stdout.read().decode().strip())
print(stderr.read().decode().strip())
ssh.close()
