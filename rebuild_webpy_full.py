"""Full rebuild of web.py: restore from bak2 + add all communities code with new features"""
import paramiko, sys

vps_host = "155.212.210.252"; vps_user = "root"; vps_pass = "2db**gLPoyDx"

c = paramiko.SSHClient(); c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect(vps_host, username=vps_user, password=vps_pass, timeout=15)
sftp = c.open_sftp()

# Read bak2 as the clean base
with sftp.open("/root/backend/routers/web.py.bak2", "rb") as f:
    content = f.read().decode("utf-8")

log = []
log.append("INFO: bak2 lines=" + str(len(content.splitlines())))

# 1. Update import from database to include Community + CommunityMember
OLD_IMPORT = "from database import get_db, Player, BankAccount, Fine"
NEW_IMPORT = "from database import get_db, Player, BankAccount, Fine, Community, CommunityMember"
if OLD_IMPORT in content:
    content = content.replace(OLD_IMPORT, NEW_IMPORT, 1)
    log.append("OK : database import updated")
elif "Community" in content:
    log.append("SKP: Community already in import")
else:
    log.append("ERR: database import not found")

# 2. Append full communities block
BT = chr(96)
HOUSE = chr(0x1F3D8) + chr(0xFE0F)

COMMUNITIES_BLOCK = (
    "\n\n"
    "# ─── Pydantic schemas ────────────────────────────────────────────────────────\n"
    "\n"
    "class CommunityCreate(BaseModel):\n"
    "    name: str\n"
    "    discord_id: str\n"
    "\n"
    "\n"
    "class CommunityJoin(BaseModel):\n"
    "    discord_id: str\n"
    "\n"
    "\n"
    "class CommunityDelete(BaseModel):\n"
    "    discord_id: str\n"
    "\n"
    "\n"
    "class CommunityUpdate(BaseModel):\n"
    "    discord_id: str\n"
    "    name: Optional[str] = None\n"
    "    description: Optional[str] = None\n"
    "    tag: Optional[str] = None\n"
    "    icon: Optional[str] = None\n"
    "    banner_url: Optional[str] = None\n"
    "    discord_url: Optional[str] = None\n"
    "    members_can_invite: Optional[int] = None\n"
    "\n"
    "\n"
    "# ─── Communities endpoints ───────────────────────────────────────────────────\n"
    "\n"
    "@router.get(\"/communities\")\n"
    "async def list_communities(db: AsyncSession = Depends(get_db)):\n"
    "    \"\"\"Return all communities sorted by member_count desc.\"\"\"\n"
    "    result = await db.execute(\n"
    "        select(Community).order_by(Community.member_count.desc())\n"
    "    )\n"
    "    comms = result.scalars().all()\n"
    "    return [\n"
    "        {\n"
    "            \"id\": cm.id,\n"
    "            \"name\": cm.name,\n"
    "            \"description\": cm.description or \"\",\n"
    "            \"tag\": cm.tag or \"\",\n"
    "            \"icon\": cm.icon or \"" + HOUSE + "\",\n"
    "            \"owner_discord_id\": cm.owner_discord_id,\n"
    "            \"member_count\": cm.member_count,\n"
    "            \"banner_url\": cm.banner_url,\n"
    "            \"discord_url\": cm.discord_url,\n"
    "            \"members_can_invite\": cm.members_can_invite,\n"
    "            \"created_at\": cm.created_at.isoformat() if cm.created_at else None,\n"
    "        }\n"
    "        for cm in comms\n"
    "    ]\n"
    "\n"
    "\n"
    "@router.post(\"/communities\")\n"
    "async def create_community(data: CommunityCreate, db: AsyncSession = Depends(get_db)):\n"
    "    \"\"\"Create a new community. Limit: 3 per discord_id.\"\"\"\n"
    "    result = await db.execute(\n"
    "        select(func.count()).select_from(Community)\n"
    "        .where(Community.owner_discord_id == data.discord_id)\n"
    "    )\n"
    "    count = result.scalar()\n"
    "    if count >= 3:\n"
    "        raise HTTPException(status_code=400, detail=\"\u041c\u0430\u043a\u0441\u0438\u043c\u0443\u043c 3 \u043e\u0431\u0449\u0438\u043d\u044b \u043d\u0430 \u0438\u0433\u0440\u043e\u043a\u0430\")\n"
    "    if not data.name.strip():\n"
    "        raise HTTPException(status_code=400, detail=\"\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u043f\u0443\u0441\u0442\u044b\u043c\")\n"
    "    comm = Community(\n"
    "        name=data.name.strip(),\n"
    "        description=\"\",\n"
    "        tag=\"\",\n"
    "        icon=\"" + HOUSE + "\",\n"
    "        owner_discord_id=data.discord_id,\n"
    "        member_count=1,\n"
    "    )\n"
    "    db.add(comm)\n"
    "    await db.flush()\n"
    "    member = CommunityMember(community_id=comm.id, discord_id=data.discord_id)\n"
    "    db.add(member)\n"
    "    await db.commit()\n"
    "    await db.refresh(comm)\n"
    "    return {\n"
    "        \"id\": comm.id,\n"
    "        \"name\": comm.name,\n"
    "        \"description\": comm.description,\n"
    "        \"tag\": comm.tag,\n"
    "        \"icon\": comm.icon,\n"
    "        \"owner_discord_id\": comm.owner_discord_id,\n"
    "        \"member_count\": comm.member_count,\n"
    "        \"banner_url\": comm.banner_url,\n"
    "        \"discord_url\": comm.discord_url,\n"
    "        \"members_can_invite\": comm.members_can_invite,\n"
    "    }\n"
    "\n"
    "\n"
    "@router.patch(\"/communities/{community_id}\")\n"
    "async def update_community(community_id: int, data: CommunityUpdate, db: AsyncSession = Depends(get_db)):\n"
    "    \"\"\"Update community. Only the owner can edit.\"\"\"\n"
    "    comm_result = await db.execute(select(Community).where(Community.id == community_id))\n"
    "    comm = comm_result.scalar_one_or_none()\n"
    "    if comm is None:\n"
    "        raise HTTPException(status_code=404, detail=\"\u041e\u0431\u0449\u0438\u043d\u0430 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430\")\n"
    "    if comm.owner_discord_id != data.discord_id:\n"
    "        raise HTTPException(status_code=403, detail=\"\u0422\u043e\u043b\u044c\u043a\u043e \u0441\u043e\u0437\u0434\u0430\u0442\u0435\u043b\u044c \u043c\u043e\u0436\u0435\u0442 \u0440\u0435\u0434\u0430\u043a\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043e\u0431\u0449\u0438\u043d\u0443\")\n"
    "    if data.name is not None:\n"
    "        comm.name = data.name.strip() or comm.name\n"
    "    if data.description is not None:\n"
    "        comm.description = data.description\n"
    "    if data.tag is not None:\n"
    "        comm.tag = data.tag\n"
    "    if data.icon is not None:\n"
    "        comm.icon = data.icon\n"
    "    if data.banner_url is not None:\n"
    "        comm.banner_url = data.banner_url or None\n"
    "    if data.discord_url is not None:\n"
    "        comm.discord_url = data.discord_url or None\n"
    "    if data.members_can_invite is not None:\n"
    "        comm.members_can_invite = data.members_can_invite\n"
    "    await db.commit()\n"
    "    await db.refresh(comm)\n"
    "    return {\n"
    "        \"id\": comm.id, \"name\": comm.name, \"description\": comm.description,\n"
    "        \"tag\": comm.tag, \"icon\": comm.icon, \"owner_discord_id\": comm.owner_discord_id,\n"
    "        \"member_count\": comm.member_count, \"banner_url\": comm.banner_url,\n"
    "        \"discord_url\": comm.discord_url, \"members_can_invite\": comm.members_can_invite,\n"
    "    }\n"
    "\n"
    "\n"
    "@router.post(\"/communities/{community_id}/join\")\n"
    "async def join_community(community_id: int, data: CommunityJoin, db: AsyncSession = Depends(get_db)):\n"
    "    \"\"\"Join a community. Cannot join twice.\"\"\"\n"
    "    comm_result = await db.execute(select(Community).where(Community.id == community_id))\n"
    "    comm = comm_result.scalar_one_or_none()\n"
    "    if comm is None:\n"
    "        raise HTTPException(status_code=404, detail=\"\u041e\u0431\u0449\u0438\u043d\u0430 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430\")\n"
    "    existing = await db.execute(\n"
    "        select(CommunityMember)\n"
    "        .where(CommunityMember.community_id == community_id)\n"
    "        .where(CommunityMember.discord_id == data.discord_id)\n"
    "    )\n"
    "    if existing.scalar_one_or_none() is not None:\n"
    "        raise HTTPException(status_code=400, detail=\"\u0412\u044b \u0443\u0436\u0435 \u0441\u043e\u0441\u0442\u043e\u0438\u0442\u0435 \u0432 \u044d\u0442\u043e\u0439 \u043e\u0431\u0449\u0438\u043d\u0435\")\n"
    "    member = CommunityMember(community_id=community_id, discord_id=data.discord_id)\n"
    "    db.add(member)\n"
    "    comm.member_count += 1\n"
    "    await db.commit()\n"
    "    return {\"status\": \"ok\", \"community_id\": community_id, \"member_count\": comm.member_count}\n"
    "\n"
    "\n"
    "@router.delete(\"/communities/{community_id}\")\n"
    "async def delete_community(community_id: int, data: CommunityDelete, db: AsyncSession = Depends(get_db)):\n"
    "    \"\"\"Delete community. Only the owner can delete.\"\"\"\n"
    "    comm_result = await db.execute(select(Community).where(Community.id == community_id))\n"
    "    comm = comm_result.scalar_one_or_none()\n"
    "    if comm is None:\n"
    "        raise HTTPException(status_code=404, detail=\"\u041e\u0431\u0449\u0438\u043d\u0430 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430\")\n"
    "    if comm.owner_discord_id != data.discord_id:\n"
    "        raise HTTPException(status_code=403, detail=\"\u0422\u043e\u043b\u044c\u043a\u043e \u0441\u043e\u0437\u0434\u0430\u0442\u0435\u043b\u044c \u043c\u043e\u0436\u0435\u0442 \u0443\u0434\u0430\u043b\u0438\u0442\u044c \u043e\u0431\u0449\u0438\u043d\u0443\")\n"
    "    await db.delete(comm)\n"
    "    await db.commit()\n"
    "    return {\"status\": \"deleted\", \"community_id\": community_id}\n"
)

# Check if Optional is already in bak2
if "from typing import Optional" not in content:
    # Add after last import line
    lines = content.split("\n")
    last_import = 0
    for i, line in enumerate(lines):
        if line.startswith("from ") or line.startswith("import "):
            last_import = i
    lines.insert(last_import + 1, "from typing import Optional")
    content = "\n".join(lines)
    log.append("OK : Optional import added")
else:
    log.append("SKP: Optional already imported")

content = content.rstrip() + "\n" + COMMUNITIES_BLOCK + "\n"
log.append("OK : full communities block appended")
log.append("INFO: final lines=" + str(len(content.splitlines())))

with sftp.open("/root/backend/routers/web.py", "wb") as f:
    f.write(content.encode("utf-8"))

sftp.close(); c.close()

for entry in log:
    sys.stdout.buffer.write((entry + "\n").encode("utf-8"))
