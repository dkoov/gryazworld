import paramiko

ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
ssh.connect('155.212.210.252', username='root', password='2db**gLPoyDx', timeout=15)
transport = ssh.get_transport()

# 1. Create community_invites table
db_script = (
    "import sqlite3\n"
    "conn = sqlite3.connect('/root/backend/serverpanel.db')\n"
    "try:\n"
    "    conn.execute('''CREATE TABLE IF NOT EXISTS community_invites (\n"
    "        id INTEGER PRIMARY KEY,\n"
    "        community_id INTEGER NOT NULL,\n"
    "        invited_nickname VARCHAR NOT NULL,\n"
    "        invited_by_discord_id VARCHAR NOT NULL,\n"
    "        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,\n"
    "        FOREIGN KEY (community_id) REFERENCES communities(id)\n"
    "    )''')\n"
    "    conn.commit()\n"
    "    print('DB: OK')\n"
    "except Exception as e:\n"
    "    print('DB:', e)\n"
    "conn.close()\n"
)
ch = transport.open_session()
ch.exec_command('python3 -')
ch.sendall(db_script.encode())
ch.shutdown_write()
out = b''
while True:
    chunk = ch.recv(4096)
    if not chunk: break
    out += chunk
ch.close()
print(out.decode())

# 2. Read files
def read_file(path):
    stdin, stdout, stderr = ssh.exec_command('cat ' + path)
    return stdout.read().decode('utf-8')

def write_file(path, content):
    ch = transport.open_session()
    ch.exec_command('cat > ' + path)
    ch.sendall(content.encode('utf-8'))
    ch.shutdown_write()
    ch.recv_exit_status()
    ch.close()
    print(path + ': written')

web_content = read_file('/root/backend/routers/web.py')
player_content = read_file('/root/backend/routers/player.py')
db_content = read_file('/root/backend/database.py')

# --- Patch database.py: add CommunityInvite model ---
new_model = "\nclass CommunityInvite(Base):\n    __tablename__ = 'community_invites'\n    id                    = Column(Integer, primary_key=True)\n    community_id          = Column(Integer, nullable=False)\n    invited_nickname      = Column(String, nullable=False)\n    invited_by_discord_id = Column(String, nullable=False)\n    created_at            = Column(DateTime, default=datetime.utcnow)\n"
if 'CommunityInvite' not in db_content:
    db_content = db_content.rstrip() + new_model + '\n'

# --- Patch web.py: import CommunityInvite ---
if 'CommunityInvite' not in web_content:
    web_content = web_content.replace(
        'from database import get_db, Player, BankAccount, Fine',
        'from database import get_db, Player, BankAccount, Fine, CommunityInvite'
    )

# --- Patch web.py: add new endpoints ---
invite_endpoints = '''

class CommunityInviteBody(BaseModel):
    discord_id: str
    nickname: str

class AcceptInviteBody(BaseModel):
    nickname: str

@router.post("/communities/{community_id}/invite")
async def invite_to_community(community_id: int, data: CommunityInviteBody, db: AsyncSession = Depends(get_db)):
    comm_result = await db.execute(select(Community).where(Community.id == community_id))
    comm = comm_result.scalar_one_or_none()
    if comm is None:
        raise HTTPException(status_code=404, detail="Община не найдена")
    if comm.owner_discord_id != data.discord_id:
        raise HTTPException(status_code=403, detail="Только владелец может приглашать")
    player_result = await db.execute(select(Player).where(func.lower(Player.nickname) == data.nickname.lower()))
    player = player_result.scalar_one_or_none()
    if player is None:
        raise HTTPException(status_code=404, detail="Игрок не найден")
    invite = CommunityInvite(
        community_id=community_id,
        invited_nickname=data.nickname.lower(),
        invited_by_discord_id=data.discord_id
    )
    db.add(invite)
    await db.commit()
    return {"detail": "Приглашение отправлено"}

@router.post("/communities/{community_id}/accept-invite")
async def accept_invite(community_id: int, data: AcceptInviteBody, db: AsyncSession = Depends(get_db)):
    invite_result = await db.execute(
        select(CommunityInvite).where(
            CommunityInvite.community_id == community_id,
            func.lower(CommunityInvite.invited_nickname) == data.nickname.lower()
        )
    )
    invite = invite_result.scalar_one_or_none()
    if invite is None:
        raise HTTPException(status_code=404, detail="Приглашение не найдено")
    player_result = await db.execute(select(Player).where(func.lower(Player.nickname) == data.nickname.lower()))
    player = player_result.scalar_one_or_none()
    if player is None:
        raise HTTPException(status_code=404, detail="Игрок не найден")
    existing = await db.execute(
        select(CommunityMember).where(CommunityMember.community_id == community_id, CommunityMember.discord_id == player.discord_id)
    )
    if existing.scalar_one_or_none() is None:
        member = CommunityMember(community_id=community_id, discord_id=player.discord_id)
        db.add(member)
        comm_result = await db.execute(select(Community).where(Community.id == community_id))
        comm = comm_result.scalar_one_or_none()
        if comm:
            comm.member_count += 1
    await db.delete(invite)
    await db.commit()
    return {"detail": "Вступление успешно"}

@router.get("/communities/{community_id}/invites")
async def list_invites(community_id: int, discord_id: str, db: AsyncSession = Depends(get_db)):
    comm_result = await db.execute(select(Community).where(Community.id == community_id))
    comm = comm_result.scalar_one_or_none()
    if comm is None:
        raise HTTPException(status_code=404, detail="Община не найдена")
    if comm.owner_discord_id != discord_id:
        raise HTTPException(status_code=403, detail="Доступ запрещён")
    invites_result = await db.execute(select(CommunityInvite).where(CommunityInvite.community_id == community_id))
    invites = invites_result.scalars().all()
    return [{"nickname": i.invited_nickname, "created_at": i.created_at.isoformat()} for i in invites]

@router.get("/communities/owned")
async def get_owned_community(discord_id: str, db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(Community).where(Community.owner_discord_id == discord_id))
    comm = result.scalars().first()
    if comm is None:
        raise HTTPException(status_code=404, detail="Своей общины нет")
    return {"id": comm.id, "name": comm.name}

'''

web_content = web_content.replace('@router.get("/banner-proxy")', invite_endpoints + '@router.get("/banner-proxy")')

# --- Patch player.py: add discord-id endpoint ---
discord_id_endpoint = (
    "\n\n@router.get('/discord-id')\n"
    "async def get_discord_id(nickname: str, db: AsyncSession = Depends(get_db)):\n"
    "    result = await db.execute(select(Player).where(func.lower(Player.nickname) == nickname.lower()))\n"
    "    player = result.scalar_one_or_none()\n"
    "    if player is None:\n"
    "        raise HTTPException(status_code=404, detail='Игрок не найден')\n"
    "    if not player.discord_id:\n"
    "        raise HTTPException(status_code=404, detail='Discord не привязан')\n"
    "    return {'discord_id': player.discord_id}\n"
)
if '/discord-id' not in player_content:
    player_content = player_content.rstrip() + discord_id_endpoint + '\n'

write_file('/root/backend/database.py', db_content)
write_file('/root/backend/routers/web.py', web_content)
write_file('/root/backend/routers/player.py', player_content)

# Restart
stdin, stdout, stderr = ssh.exec_command('systemctl restart grworld-backend.service && sleep 2 && systemctl is-active grworld-backend.service')
print('Restart:', stdout.read().decode().strip())
ssh.close()
