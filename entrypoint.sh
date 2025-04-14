#!/bin/bash

ENV="${SPRING_PROFILES_ACTIVE:-dev}"
BUCKET="siungongsi-env"
OBJECT_KEY="$ENV/.env"
REGION="${AWS_REGION:-ap-northeast-2}"

echo "[ENTRYPOINT] Fetching .env from S3 → s3://$BUCKET/$OBJECT_KEY"

aws s3 cp s3://$BUCKET/$OBJECT_KEY /app/.env --region $REGION

if [ -f /app/.env ]; then
    echo "[ENTRYPOINT] Loading environment variables..."
    set -a                      
    source /app/.env            
    set +a                     
else
    echo "[ERROR] Failed to load .env"
    exit 1
fi

exec java -jar app.jar --server.port=8080 --server.address=0.0.0.0 --spring.profiles.active=$ENV
