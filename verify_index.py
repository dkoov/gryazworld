# -*- coding: utf-8 -*-
import paramiko
import sys

# Force UTF-8 output
sys.stdout.reconfigure(encoding='utf-8', errors='replace')

jump_host = '155.212.210.252'
jump_user = 'root'
jump_pass = '2db**gLPoyDx'

target_host = 'REDACTED_HOST'
target_user = 'REDACTED_USER'
target_pass = 'REDACTED_PASSWORD'
target_port = 22

remote_file = '/home/k/katalist/gryazworld.ru/public_html/index.html'

jump_client = paramiko.SSHClient()
jump_client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
jump_client.connect(jump_host, username=jump_user, password=jump_pass, timeout=15)

jump_transport = jump_client.get_transport()
channel = jump_transport.open_channel('direct-tcpip', (target_host, target_port), ('127.0.0.1', 0))

target_client = paramiko.SSHClient()
target_client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
target_client.connect(target_host, username=target_user, password=target_pass, sock=channel, timeout=15)

sftp = target_client.open_sftp()

with sftp.open(remote_file, 'r') as f:
    content = f.read().decode('utf-8')

lines = content.split('\n')
print(f"Total lines: {len(lines)}")
print("\n--- Lines 860-900 ---")
for i, line in enumerate(lines[859:900], start=860):
    print(f"{i}: {line}")

sftp.close()
target_client.close()
jump_client.close()
print("\nVerification done.")
