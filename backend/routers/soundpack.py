import hashlib
import os
import subprocess
import zipfile

from fastapi import APIRouter, Depends, HTTPException, Request
from fastapi.responses import FileResponse

from auth import verify_plugin_secret

router = APIRouter(prefix="/mc/soundpack", tags=["soundpack"])

DATA_DIR = "/app/data/soundpack"
RAW_PATH = os.path.join(DATA_DIR, "input.raw")
OGG_PATH = os.path.join(DATA_DIR, "custom.ogg")
PACK_PATH = os.path.join(DATA_DIR, "pack.zip")
SHA1_PATH = os.path.join(DATA_DIR, "pack.sha1")

SOUND_NAMESPACE = "ichorix"
SOUND_EVENT = "custom.alert"

PACK_MCMETA = (
    '{"pack":{"pack_format":48,'
    '"supported_formats":{"min_inclusive":15,"max_inclusive":100},'
    '"description":"Ichorix custom sounds"}}'
)


def _sounds_json() -> str:
    return (
        '{"' + SOUND_EVENT + '":{"sounds":["' + SOUND_NAMESPACE + ':custom"],'
        '"category":"master"}}'
    )


@router.post("/upload", dependencies=[Depends(verify_plugin_secret)])
async def upload_sound(request: Request):
    """Принимает сырые байты аудио (любой формат, который понимает ffmpeg -- mp3/ogg/wav/...),
    конвертирует в ogg vorbis и пересобирает ресурс-пак целиком."""
    os.makedirs(DATA_DIR, exist_ok=True)
    body = await request.body()
    if not body:
        raise HTTPException(status_code=400, detail="empty body")

    with open(RAW_PATH, "wb") as f:
        f.write(body)

    result = subprocess.run(
        ["ffmpeg", "-y", "-i", RAW_PATH, "-c:a", "libvorbis", "-q:a", "5", OGG_PATH],
        capture_output=True, text=True, timeout=60,
    )
    if result.returncode != 0 or not os.path.exists(OGG_PATH):
        raise HTTPException(status_code=400, detail=f"ffmpeg failed: {result.stderr[-800:]}")

    with zipfile.ZipFile(PACK_PATH, "w", zipfile.ZIP_DEFLATED) as z:
        z.writestr("pack.mcmeta", PACK_MCMETA)
        z.writestr(f"assets/{SOUND_NAMESPACE}/sounds.json", _sounds_json())
        z.write(OGG_PATH, f"assets/{SOUND_NAMESPACE}/sounds/custom.ogg")

    with open(PACK_PATH, "rb") as f:
        sha1 = hashlib.sha1(f.read()).hexdigest()
    with open(SHA1_PATH, "w") as f:
        f.write(sha1)

    return {
        "ok": True,
        "sha1": sha1,
        "url": "https://ichorix.cc/api/mc/soundpack/pack.zip",
        "soundEvent": f"{SOUND_NAMESPACE}:{SOUND_EVENT}",
    }


@router.get("/info", dependencies=[Depends(verify_plugin_secret)])
async def sound_info():
    if not os.path.exists(SHA1_PATH):
        raise HTTPException(status_code=404, detail="no pack uploaded yet")
    with open(SHA1_PATH) as f:
        sha1 = f.read().strip()
    return {
        "sha1": sha1,
        "url": "https://ichorix.cc/api/mc/soundpack/pack.zip",
        "soundEvent": f"{SOUND_NAMESPACE}:{SOUND_EVENT}",
    }


@router.get("/pack.zip")
async def get_pack():
    # Публичный, БЕЗ verify_plugin_secret -- этот файл качают сами клиенты Minecraft
    # напрямую по ссылке, а не игровой сервер.
    if not os.path.exists(PACK_PATH):
        raise HTTPException(status_code=404)
    return FileResponse(PACK_PATH, media_type="application/zip", filename="pack.zip")
