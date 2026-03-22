import paramiko, re

ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
ssh.connect('155.212.210.252', username='root', password='2db**gLPoyDx', timeout=15)

stdin, stdout, stderr = ssh.exec_command('cat /root/discord-bot/bot.py')
content = stdout.read().decode('utf-8')

original = content

# 1. Remove standalone: embed.timestamp = datetime.utcnow() / datetime.now()
content = re.sub(r'[ \t]*embed\.timestamp\s*=\s*datetime\.(utcnow|now)\(\)[ \t]*\n', '', content)

# 2. Remove inline in Embed(): timestamp=datetime.utcnow() or timestamp=datetime.now()
content = re.sub(r',?\s*timestamp=datetime\.(utcnow|now)\(\)', '', content)

changed = content != original
print("Changed:", changed)
if changed:
    transport = ssh.get_transport()
    ch = transport.open_session()
    ch.exec_command('cat > /root/discord-bot/bot.py')
    ch.sendall(content.encode('utf-8'))
    ch.shutdown_write()
    ch.recv_exit_status()
    ch.close()
    print("Written")

# Verify nothing left
remaining = [l for l in content.splitlines() if 'timestamp' in l]
print("Remaining timestamp lines:", remaining)

stdin, stdout, stderr = ssh.exec_command(
    'systemctl restart grworld-bot.service; sleep 2; systemctl is-active grworld-bot.service'
)
print("Bot:", stdout.read().decode().strip())
ssh.close()
