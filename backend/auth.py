import os
from dotenv import load_dotenv
from fastapi import Header, HTTPException, status

load_dotenv()

PLUGIN_SECRET = os.getenv("PLUGIN_SECRET", "")


async def verify_plugin_secret(x_plugin_secret: str = Header(...)):
    if x_plugin_secret != PLUGIN_SECRET:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Invalid plugin secret"
        )
