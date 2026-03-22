import paramiko
import io

# Jump host (VPS)
jump_host = '155.212.210.252'
jump_user = 'root'
jump_pass = '2db**gLPoyDx'

# Target (beget)
target_host = 'REDACTED_HOST'
target_user = 'REDACTED_USER'
target_pass = 'REDACTED_PASSWORD'
target_port = 22

remote_file = '/home/k/katalist/gryazworld.ru/public_html/index.html'

OLD_BLOCK = """        const rows = leaders.map((p, i) => {
          const hours = Math.floor(p.total_seconds / 3600);
          const mins = Math.floor((p.total_seconds % 3600) / 60);
          const timeStr = hours + 'ч ' + mins + 'м';
          const medal = i === 0 ? '🥇' : i === 1 ? '🥈' : i === 2 ? '🥉' : (i + 1) + '.';
          return '<tr style="border-bottom:1px solid var(--border)">' +
            '<td style="padding:10px 12px;color:var(--accent)">' + medal + '</td>' +
            '<td style="padding:10px 12px;color:var(--text);font-weight:500">' + p.nickname + '</td>' +
            '<td style="padding:10px 12px;color:var(--muted)">' + timeStr + '</td>' +
            '<td style="padding:10px 12px;color:var(--accent)">' + p.balance + ' 💎</td>' +
            '<td style="padding:10px 12px;color:' + (p.warns > 0 ? '#e05c5c' : 'var(--muted)') + '">' + p.warns + '</td>' +
          '</tr>';
        }).join('');
        tbl.innerHTML = '<table style="width:100%;border-collapse:collapse;font-size:13px">' +
          '<thead><tr style="border-bottom:1px solid var(--border2)">' +
          '<th style="padding:8px 12px;text-align:left;font-size:10px;letter-spacing:.1em;text-transform:uppercase;color:var(--muted)">Место</th>' +
          '<th style="padding:8px 12px;text-align:left;font-size:10px;letter-spacing:.1em;text-transform:uppercase;color:var(--muted)">Ник</th>' +
          '<th style="padding:8px 12px;text-align:left;font-size:10px;letter-spacing:.1em;text-transform:uppercase;color:var(--muted)">Время (ч)</th>' +
          '<th style="padding:8px 12px;text-align:left;font-size:10px;letter-spacing:.1em;text-transform:uppercase;color:var(--muted)">Баланс (алмазы)</th>' +
          '<th style="padding:8px 12px;text-align:left;font-size:10px;letter-spacing:.1em;text-transform:uppercase;color:var(--muted)">Варны</th>' +
          '</thead><tbody>' + rows + '</tbody></table>';"""

NEW_BLOCK = """        const rows = leaders.map((p, i) => {
          const hours = Math.floor(p.total_seconds / 3600);
          const mins = Math.floor((p.total_seconds % 3600) / 60);
          const timeStr = hours + 'ч ' + mins + 'м';
          const medal = i===0 ? '🥇' : i===1 ? '🥈' : i===2 ? '🥉' : '';
          const rank = i + 1;
          const skinUrl = 'https://mc-heads.net/avatar/' + p.nickname + '/64';
          const isOnline = p.is_online ? '<span style="display:inline-block;width:8px;height:8px;background:#4caf50;border-radius:50%;margin-right:4px"></span>' : '';

          return `<div style="
    display:flex;align-items:center;gap:14px;
    background:var(--card,#1a1a1a);
    border:1px solid var(--border,#2a2a2a);
    border-radius:10px;padding:12px 16px;
    transition:border-color .2s;cursor:default;
  " onmouseover="this.style.borderColor='#c8a84b'" onmouseout="this.style.borderColor='var(--border,#2a2a2a)'">
    <div style="min-width:28px;font-size:18px;text-align:center">${medal || '<span style="color:var(--muted,#666);font-size:13px">' + rank + '</span>'}</div>
    <img src="${skinUrl}" alt="${p.nickname}"
      style="width:48px;height:48px;border-radius:6px;image-rendering:pixelated;flex-shrink:0"
      onerror="this.src='https://mc-heads.net/avatar/Steve/64'">
    <div style="flex:1;min-width:0">
      <div style="font-weight:600;font-size:15px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">
        ${isOnline}${p.nickname}
      </div>
      <div style="color:var(--muted,#888);font-size:12px;margin-top:2px">Наиграл: ${timeStr}</div>
    </div>
    <div style="text-align:right;flex-shrink:0">
      <div style="font-size:13px;color:#c8a84b">${p.balance} 💎</div>
      ${p.warns > 0 ? '<div style="font-size:11px;color:#e05c5c;margin-top:2px">⚠️ ' + p.warns + ' варн</div>' : ''}
    </div>
  </div>`;
        }).join('');
        tbl.innerHTML = '<div style="display:flex;flex-direction:column;gap:8px">' + rows + '</div>';"""

print("Connecting to jump host...")
jump_client = paramiko.SSHClient()
jump_client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
jump_client.connect(jump_host, username=jump_user, password=jump_pass, timeout=15)
print("Connected to jump host.")

# Open direct-tcpip channel to target
jump_transport = jump_client.get_transport()
dest_addr = (target_host, target_port)
local_addr = ('127.0.0.1', 0)
channel = jump_transport.open_channel('direct-tcpip', dest_addr, local_addr)
print("Tunnel channel opened.")

# Connect to target via channel
target_client = paramiko.SSHClient()
target_client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
target_client.connect(target_host, username=target_user, password=target_pass, sock=channel, timeout=15)
print("Connected to target (beget).")

# Open SFTP
sftp = target_client.open_sftp()
print("SFTP session opened.")

# Download file
print(f"Downloading {remote_file} ...")
with sftp.open(remote_file, 'r') as f:
    content = f.read().decode('utf-8')
print(f"File downloaded, {len(content)} bytes, {content.count(chr(10))+1} lines.")

# Check old block exists
if OLD_BLOCK not in content:
    print("ERROR: Old block NOT found in file! Aborting.")
    # Show lines around 855-890 for debugging
    lines = content.split('\n')
    for i, line in enumerate(lines[855:890], start=856):
        print(f"{i}: {repr(line)}")
    sftp.close()
    target_client.close()
    jump_client.close()
    exit(1)

print("Old block found. Replacing...")
new_content = content.replace(OLD_BLOCK, NEW_BLOCK, 1)

if new_content == content:
    print("ERROR: Replacement had no effect!")
    exit(1)

print("Uploading modified file...")
with sftp.open(remote_file, 'w') as f:
    f.write(new_content.encode('utf-8'))
print("File uploaded successfully.")

# Verify: show lines 860-900
lines = new_content.split('\n')
print("\n--- Lines 860-900 of updated file ---")
for i, line in enumerate(lines[859:900], start=860):
    print(f"{i}: {line}")

sftp.close()
target_client.close()
jump_client.close()
print("\nDone. Connections closed.")
