// --- file: pose-infer/README.md
# Pose Inference Service (FastAPI + MoveNet)

## Run locally
```bash
cd pose-infer
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
export MOVENET_MODEL=thunder  # or lightning
uvicorn main:app --host 0.0.0.0 --port 8000
```

## Docker
```bash
cd pose-infer
docker build -t pose-infer:latest .
docker run --rm -p 8000:8000 -e MOVENET_MODEL=thunder pose-infer:latest
```

## API
- **POST** `/infer` (multipart/form-data)
    - field `file`: image (jpeg/png)
    - **Response**: `{ "schema": "coco17", "keypoints": [{"name":"LEFT_SHOULDER","x":..,"y":..,"confidence":..}, ...] }`

## Notes
- Coordinates are returned in **pixel space (original image size)**; Java service normalizes/rotates.
- If you need BlazePose(33) via MediaPipe instead of MoveNet, install `mediapipe` and add a mapper to COCO-17.
- Throughput: use `--workers` (uvicorn) or enable TF graph mode; batch inference can be added easily.
