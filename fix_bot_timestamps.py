import paramiko

ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
ssh.connect('155.212.210.252', username='root', password='2db**gLPoyDx', timeout=15)

stdin, stdout, stderr = ssh.exec_command('cat /root/discord-bot/bot.py')
content = stdout.read().decode('utf-8')

original = content

# Remove standalone timestamp lines
import re
content = re.sub(r'\n\s*embed\.timestamp\s*=\s*datetime\.utcnow\(\)\s*', '\n', content)

# Remove timestamp= from discord.Embed(...) calls (may span multiple lines)
# Handle: timestamp=datetime.utcnow() with optional trailing comma
content = re.sub(r',?\s*timestamp=datetime\.utcnow\(\)', '', content)

print("Changed:", content != original)
# Show diff count
orig_lines = original.splitlines()
new_lines = content.splitlines()
print(f"Lines: {len(orig_lines)} -> {len(new_lines)}")

# Write back
transport = ssh.get_transport()
ch = transport.open_session()
ch.exec_command('cat > /root/discord-bot/bot.py')
ch.sendall(content.encode('utf-8'))
ch.shutdown_write()
ch.recv_exit_status()
ch.close()
print("bot.py written")

# Restart
stdin, stdout, stderr = ssh.exec_command(
    'systemctl restart grworld-bot.service; sleep 2; systemctl is-active grworld-bot.service'
)
print("Bot:", stdout.read().decode().strip())
ssh.close()
