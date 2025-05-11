from fastapi import FastAPI, Request, HTTPException
from pydantic import BaseModel
from typing import Optional, Literal
from datetime import datetime
import json
import os
from PIL import Image
import requests

# pip install ollama
from ollama import chat

# https://huggingface.co/meta-llama/Llama-3.2-11B-Vision
MODEL_NAME = "llama3.2-vision:latest"

class JSONSchema(BaseModel):
    item_name: str
    deal_description: Optional[str]
    expiry_date: Optional[datetime]
    price: Optional[float]
    deal_type: Literal['BOGO', 'FREE', 'DISCOUNT', 'OTHER']
    applicable_group: Literal[
        'UNDER_18', 'STUDENT', 'SENIOR',
        'LOYALTY_MEMBER', 'NEW_USER',
        'BIRTHDAY', 'EVERYONE'
    ]

app = FastAPI()

@app.get("/")
async def root():
    return {"message": "Hello World"}

BASE_URL = "https://firebasestorage.googleapis.com/v0/b/meowmeow-6c3ec.firebasestorage.app/o/"
FILE_PATH = "deal_images%2F"
QUERY_PARAMS = "?alt=media"
FOLDERPATH = "/mnt/slurm_nfs/mllm_446/data"

class ImageRequest(BaseModel):
    image_id: str

messages = [
    {
        "role": "system",
        "content": (
            "You are an expert at parsing restaurant posters. "
            "Produce a JSON object **only** in this format:\n"
            "{\n"
            '  "item_name": "<string>",\n'
            '  "deal_description": "<string or null>",\n'
            '  "expiry_date": "<ISO8601 date or null>",\n'
            '  "price": "<float or null>",\n'
            '  "deal_type": "<BOGO|FREE|DISCOUNT|OTHER>",\n'
            '  "applicable_group": '
            '"<UNDER_18|STUDENT|SENIOR|LOYALTY_MEMBER|NEW_USER|BIRTHDAY|EVERYONE>"\n'
            "}\n"
            "No extra text."
        )
    },
    {
        "role": "user",
        "content": "Analyze the details in this restaurant poster and produce the JSON.",
        "images": []
    }
]

@app.post("/generate", response_model=JSONSchema)
def generate(request: Request, image_request: ImageRequest):
    # download locally from firebase
    url = f"{BASE_URL}{FILE_PATH}{image_request.image_id}{QUERY_PARAMS}"
    local_path = os.path.join(FOLDERPATH, image_request.image_id)
    resp = requests.get(url)
    if resp.status_code != 200:
        raise HTTPException(
            status_code=400,
            detail=f"Failed to fetch image: {resp.status_code}"
        )
    with open(local_path, "wb") as f:
        f.write(resp.content)

    user_msg = messages[1].copy()
    user_msg["images"] = [local_path]

    convo = [messages[0], user_msg]

    try:
        response = chat(
            model=MODEL_NAME,
            messages=convo
        )
        # response.message.content = model’s reply string :contentReference[oaicite:1]{index=1}
        json_str = response.message.content.strip()
        parsed = json.loads(json_str)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

    return JSONSchema(**parsed)
