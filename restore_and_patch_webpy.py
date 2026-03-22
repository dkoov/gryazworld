"""Restore web.py from bak2 + apply all Part 2 changes"""
import paramiko, sys

vps_host = "155.212.210.252"; vps_user = "root"; vps_pass = "2db**gLPoyDx"

c = paramiko.SSHClient(); c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect(vps_host, username=vps_user, password=vps_pass, timeout=15)
sftp = c.open_sftp()

# Read bak2 as base
with sftp.open("/root/backend/routers/web.py.bak2", "rb") as f:
    content = f.read().decode("utf-8")

log = []

HOUSE_ICON = chr(0x1F3D8) + chr(0xFE0F)  # 🏘️

# --- Check what's already in bak2 ---
log.append(f"INFO: bak2 has {len(content.splitlines())} lines")
log.append(f"INFO: has communities: {'communities' in content}")
log.append(f"INFO: has CommunityCreate: {'CommunityCreate' in content}")

# 1. Add typing Optional import at top if missing
if "from typing import Optional" not in content and "from typing import" not in content:
    # Insert after the last import block (find first non-import line)
    lines = content.split("\n")
    insert_at = 0
    for i, line in enumerate(lines):
        if line.startswith("from ") or line.startswith("import "):
            insert_at = i + 1
    lines.insert(insert_at, "from typing import Optional")
    content = "\n".join(lines)
    log.append("OK : Optional import added")
elif "Optional" in content:
    log.append("SKP: Optional already imported")
else:
    # add to existing typing import
    old = content.split("from typing import")[0] + "from typing import"
    line = content[len(old):].split("\n")[0]
    content = content.replace("from typing import" + line, "from typing import" + line.rstrip() + ", Optional", 1)
    log.append("OK : Optional added to typing import")

# 2. If CommunityCreate already exists in bak2, simplify it
if "class CommunityCreate(BaseModel):" in content:
    # Find and replace the full class definition
    import re
    # Try exact match first
    OLD_CC = (
        "class CommunityCreate(BaseModel):\n"
        "    name: str\n"
        "    description: str = \"\"\n"
        "    tag: str = \"\u041e\u0431\u0449\u0430\u044f\"\n"
        "    icon: str = \"\U0001f3e0\"\n"
        "    discord_id: str\n"
    )
    NEW_CC = (
        "class CommunityCreate(BaseModel):\n"
        "    name: str\n"
        "    discord_id: str\n"
    )
    if OLD_CC in content:
        content = content.replace(OLD_CC, NEW_CC, 1)
        log.append("OK : CommunityCreate simplified")
    else:
        log.append("WARN: CommunityCreate found but exact anchor not matched - check manually")
else:
    log.append("INFO: CommunityCreate not in bak2, will be added with communities block")

# 3. Add CommunityUpdate if missing
if "CommunityUpdate" not in content:
    # Insert after CommunityDelete class
    OLD_DELETE_CLASS = (
        "class CommunityDelete(BaseModel):\n"
        "    discord_id: str\n"
    )
    NEW_DELETE_CLASS = (
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
    )
    if OLD_DELETE_CLASS in content:
        content = content.replace(OLD_DELETE_CLASS, NEW_DELETE_CLASS, 1)
        log.append("OK : CommunityUpdate added after CommunityDelete")
    else:
        log.append("WARN: CommunityDelete class not found for CommunityUpdate insertion")

# 4. Update GET /communities response to include new fields
OLD_GET_RESP = (
    "            \"member_count\": cm.member_count,\n"
    "            \"created_at\": cm.created_at.isoformat() if cm.created_at else None,\n"
    "        }\n"
    "        for cm in comms\n"
    "    ]\n"
)
NEW_GET_RESP = (
    "            \"member_count\": cm.member_count,\n"
    "            \"banner_url\": cm.banner_url,\n"
    "            \"discord_url\": cm.discord_url,\n"
    "            \"members_can_invite\": cm.members_can_invite,\n"
    "            \"created_at\": cm.created_at.isoformat() if cm.created_at else None,\n"
    "        }\n"
    "        for cm in comms\n"
    "    ]\n"
)
if OLD_GET_RESP in content:
    content = content.replace(OLD_GET_RESP, NEW_GET_RESP, 1)
    log.append("OK : GET /communities response updated with new fields")
elif "\"banner_url\": cm.banner_url" in content:
    log.append("SKP: GET response already has banner_url")
else:
    log.append("ERR: GET response anchor not found")

# 5. Update create_community to set defaults
OLD_COMM_OBJ = (
    "    comm = Community(\n"
    "        name=data.name.strip(),\n"
    "        description=data.description.strip(),\n"
    "        tag=data.tag.strip() or \"\u041e\u0431\u0449\u0430\u044f\",\n"
    "        icon=data.icon.strip() or \"\U0001f3e0\",\n"
    "        owner_discord_id=data.discord_id,\n"
    "        member_count=1,\n"
    "    )\n"
)
NEW_COMM_OBJ = (
    "    comm = Community(\n"
    "        name=data.name.strip(),\n"
    "        description=\"\",\n"
    "        tag=\"\",\n"
    "        icon=\"" + HOUSE_ICON + "\",\n"
    "        owner_discord_id=data.discord_id,\n"
    "        member_count=1,\n"
    "    )\n"
)
if OLD_COMM_OBJ in content:
    content = content.replace(OLD_COMM_OBJ, NEW_COMM_OBJ, 1)
    log.append("OK : create_community Community() defaults updated")
else:
    log.append("SKP: create_community Community() anchor not found (may need manual check)")

# 6. Add PATCH endpoint before DELETE endpoint
PATCH_ENDPOINT = (
    "\n\n"
    "@router.patch(\"/communities/{community_id}\")\n"
    "async def update_community(community_id: int, data: CommunityUpdate, db: AsyncSession = Depends(get_db)):\n"
    "    \"\"\"Update community. Only the owner can edit.\"\"\"\n"
    "    comm_result = await db.execute(\n"
    "        select(Community).where(Community.id == community_id)\n"
    "    )\n"
    "    comm = comm_result.scalar_one_or_none()\n"
    "    if comm is None:\n"
    "        raise HTTPException(status_code=404, detail=\"\u041e\u0431\u0449\u0438\u043d\u0430 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430\")\n"
    "    if comm.owner_discord_id != data.discord_id:\n"
    "        raise HTTPException(status_code=403, detail=\"\u0422\u043e\u043b\u044c\u043a\u043e \u0441\u043e\u0437\u0434\u0430\u0442\u0435\u043b\u044c \u043c\u043e\u0436\u0435\u0442 \u0440\u0435\u0434\u0430\u043a\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043e\u0431\u0449\u0438\u043d\u0443\")\n"
    "\n"
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
    "\n"
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
)

if "@router.patch(\"/communities/" in content:
    log.append("SKP: PATCH /communities endpoint already exists")
else:
    DELETE_ANCHOR = "@router.delete(\"/communities/{community_id}\")"
    if DELETE_ANCHOR in content:
        content = content.replace(DELETE_ANCHOR, PATCH_ENDPOINT + DELETE_ANCHOR, 1)
        log.append("OK : PATCH /communities/{id} endpoint added before DELETE")
    else:
        content = content.rstrip() + "\n" + PATCH_ENDPOINT + "\n"
        log.append("OK : PATCH endpoint appended to end")

# Write back
with sftp.open("/root/backend/routers/web.py", "wb") as f:
    f.write(content.encode("utf-8"))

sftp.close(); c.close()

for entry in log:
    sys.stdout.buffer.write((entry + "\n").encode("utf-8"))
