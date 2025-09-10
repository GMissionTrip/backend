from fastapi import FastAPI, File, UploadFile
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List
from io import BytesIO
import numpy as np
from PIL import Image, ImageOps
import os

# --- Load MoveNet (TF Hub: COCO-17, single-person)
import tensorflow as tf
import tensorflow_hub as hub

MODEL_VARIANT = os.getenv("MOVENET_MODEL", "thunder").lower()  # "lightning" or "thunder"
MODEL_URL = {
    "lightning": "https://tfhub.dev/google/movenet/singlepose/lightning/4",
    "thunder":   "https://tfhub.dev/google/movenet/singlepose/thunder/4",
}.get(MODEL_VARIANT, "https://tfhub.dev/google/movenet/singlepose/thunder/4")

app = FastAPI(title="Pose Inference Service", version="1.0")
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# COCO-17 keypoint names in MoveNet order
COCO17 = [
    "NOSE",
    "LEFT_EYE", "RIGHT_EYE",
    "LEFT_EAR", "RIGHT_EAR",
    "LEFT_SHOULDER", "RIGHT_SHOULDER",
    "LEFT_ELBOW", "RIGHT_ELBOW",
    "LEFT_WRIST", "RIGHT_WRIST",
    "LEFT_HIP", "RIGHT_HIP",
    "LEFT_KNEE", "RIGHT_KNEE",
    "LEFT_ANKLE", "RIGHT_ANKLE",
]

class KP(BaseModel):
    name: str
    x: float
    y: float
    confidence: float

class InferResponse(BaseModel):
    schema: str = "coco17"
    keypoints: List[KP]

# Lazy load to speed up container start
_movenet = None


def load_model():
    global _movenet
    if _movenet is None:
        model = hub.load(MODEL_URL)
        _movenet = model.signatures['serving_default']
    return _movenet


def exif_correct(rgb: Image.Image) -> Image.Image:
    # Fix orientation using EXIF if present
    return ImageOps.exif_transpose(rgb)


def preprocess(img: np.ndarray, size: int = 256) -> tf.Tensor:
    # img: HxWx3 uint8
    t = tf.convert_to_tensor(img)
    t = tf.image.resize_with_pad(t, size, size)
    t = tf.expand_dims(t, axis=0)  # 1xHxWx3
    t = tf.cast(t, tf.int32)       # MoveNet expects int32 0..255
    return t


def run_movenet(img_np: np.ndarray) -> np.ndarray:
    # Returns (17,3) with (y, x, score) normalized [0,1]
    model = load_model()
    inp = preprocess(img_np)
    outputs = model(inp)
    kps = outputs['output_0'].numpy()[0, 0, :, :]  # (17,3)
    return kps


@app.post("/infer", response_model=InferResponse)
async def infer(file: UploadFile = File(...)):
    # 1) Read & decode
    content = await file.read()
    image = Image.open(BytesIO(content)).convert("RGB")
    image = exif_correct(image)
    w, h = image.size
    img_np = np.array(image)

    # 2) Run MoveNet
    kps = run_movenet(img_np)  # (17,3): y,x,score in [0,1]

    # 3) Convert to pixel coords
    out = []
    for i, name in enumerate(COCO17):
        y_norm, x_norm, conf = kps[i]
        x = float(x_norm * w)
        y = float(y_norm * h)
        out.append(KP(name=name, x=x, y=y, confidence=float(conf)))

    return InferResponse(schema="coco17", keypoints=out)


# # --- file: pose-infer/Dockerfile
# # Multi-stage for smaller image (optional). Simple version below:
# FROM python:3.10-slim
# ENV PYTHONDONTWRITEBYTECODE=1 \
#     PYTHONUNBUFFERED=1 \
#     TF_CPP_MIN_LOG_LEVEL=2
#
# WORKDIR /app
# COPY requirements.txt ./
# RUN pip install --no-cache-dir -r requirements.txt
# COPY main.py ./
#
# EXPOSE 8000
# CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]

