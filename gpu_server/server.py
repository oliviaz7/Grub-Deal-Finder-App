from fastapi import FastAPI, Request, HTTPException
from pydantic import BaseModel
import json
from typing import Optional, Literal
from datetime import datetime
import requests
from transformers import MllamaForConditionalGeneration, AutoProcessor
import torch
from PIL import Image
import re
import os

# https://huggingface.co/meta-llama/Llama-3.2-11B-Vision
MODEL_ID = "meta-llama/Llama-3.2-11B-Vision-Instruct"

# TODO: token instead of use_auth_token

model = MllamaForConditionalGeneration.from_pretrained(
    MODEL_ID,
    torch_dtype=torch.bfloat16,
    device_map="auto",
    use_auth_token=os.getenv("HUGGINGFACE_TOKEN")
)
processor = AutoProcessor.from_pretrained(MODEL_ID, use_auth_token=HUGGINGFACE_TOKEN)

class InputData(BaseModel):
    prompt: str

class JSONSchema(BaseModel):
    item_name: str
    deal_description: Optional[str]
    expiry_date: Optional[datetime]
    price: Optional[float]
    deal_type: Literal['BOGO', 'FREE', 'DISCOUNT', 'OTHER']
    applicable_group: Literal['UNDER_18', 'STUDENT', 'SENIOR', 'LOYALTY_MEMBER', 'NEW_USER', 'BIRTHDAY', 'EVERYONE']

app = FastAPI()

@app.get("/")
async def root():
    return {"message": "Hello World"}

BASE_URL = "https://firebasestorage.googleapis.com/v0/b/meowmeow-6c3ec.firebasestorage.app/o/"
FILE_PATH = "deal_images%2F"
QUERY_PARAMS = "?alt=media"

FOLDERPATH = f"/mnt/slurm_nfs/mllm_446/data"

class ImageRequest(BaseModel):
    image_id: str

messages = [
    {
        "role": "system",
        "content": [
            {
                "type": "text",
                "text": (
                    "You are an expert at parsing restaurant posters. Your task is to analyze the details in a restaurant poster that advertises a deal and produce a JSON object. "
                    "The JSON object must strictly follow this format and contain no additional text:\n\n"
                    "{\n"
                    '  "item_name": "<string>",\n'
                    '  "deal_description": "<string or null>",\n'
                    '  "expiry_date": "<ISO8601 date or null>",\n'
                    '  "price": "<float or null>",\n'
                    '  "deal_type": "<BOGO|FREE|DISCOUNT|OTHER>",\n'
                    '  "applicable_group": "<UNDER_18|STUDENT|SENIOR|LOYALTY_MEMBER|NEW_USER|BIRTHDAY|EVERYONE>"\n'
                    "}\n\n"
                    "Respond with valid JSON only. Do not include any introductions, summaries, or extra text."
                )
            }
        ]
    },

    {   "role": "user",
            "content": [
            {"type": "image"},
            {"type": "text", "text": "Analyze the details in a restaurant poster that advertises a deal and produce a JSON object."}
        ]
    }
]

def extract(text: str) -> str:
    # pattern: <|start_header_id|>assistant<|end_header_id|> ANSWER <|eot_id|> or end of text.
    pattern = r"<\|start_header_id\|>assistant<\|end_header_id\|>(.*?)(<\|eot_id\|>|$)"
    matches = re.findall(pattern, text, re.DOTALL)
    if matches:
        print(f"matches+++: {matches}")
        print(f"matches[-1]+++: {matches[-1]}")
        return matches[-1][0].strip()
    return text.strip()

@app.post("/generate", response_model=JSONSchema)
def generate(
    request: Request,
    image_request: ImageRequest
    # image_id: str
):
    try:
        url = f"{BASE_URL}{FILE_PATH}{image_request.image_id}{QUERY_PARAMS}" # do we need query params
        # url = f"{BASE_URL}{FILE_PATH}{image_id}{QUERY_PARAMS}" # do we need query params
        print(f"URL: {url}")

        filepath = f"{FOLDERPATH}/{image_request.image_id}"
        print(filepath)

        response = requests.get(url)
        # if response.status_code != 200:
        #     print(f"failed response content: {response.content}")

        if response.status_code == 200:
            with open(filepath, "wb") as f:
                f.write(response.content)
            print(f"Image downloaded to: {filepath}")
        else:
            raise Exception(f"Failed to fetch image. Status code: {response.status_code}")
    except Exception as e:
        print(e)

    try:
        if not filepath:
            raise HTTPException(status_code=400, detail="No image was posted")

        image = Image.open(filepath)
        inputs = processor.apply_chat_template(messages, add_generation_prompt=True)

        inputs = processor(
            image,
            inputs,
            add_special_tokens=False,
            return_tensors="pt"
        ).to(model.device)

        output = model.generate(**inputs, max_new_tokens=100)
        print(f"output+++: {processor.decode(output[0])}")
        response = extract(processor.decode(output[0]))
        print(f"response+++: {response}")

        # TODO: will it always be the last one?
        jsponse = json.loads(response)
        print(f"json_response++: {jsponse}")

    except Exception as e:
        print(e)
        raise HTTPException(status_code=500, detail=str(e))

    return JSONSchema(**jsponse)
