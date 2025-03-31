#!/bin/bash

echo "[ENTRYPOINT] Fetching secrets from AWS Secrets Manager..."

SECRET_NAME="dev/siungongsi/backend"
REGION="${AWS_REGION:-ap-northeast-2}"  # 기본 리전을 설정해도 좋아요

# Secrets Manager에서 .env로 저장
aws secretsmanager get-secret-value \
    --region "$REGION" \
    --secret-id "$SECRET_NAME" \
    --query SecretString \
    --output text > /app/.env

    if [ -f /app/.env ]; then
    echo "[ENTRYPOINT] .env loaded:"
    cat /app/.env

    # 환경 변수로 export
    export $(cat /app/.env | xargs)
    else
    echo "[ENTRYPOINT ERROR] Failed to load secrets. Exiting."
    exit 1
fi

# 애플리케이션 실행
exec java -jar app.jar --spring.profiles.active=dev
