#!/bin/bash

echo "[ENTRYPOINT] Fetching secrets from AWS Secrets Manager..."

SECRET_NAME="dev/siungongsi/backend"
REGION="${AWS_REGION:-ap-northeast-2}"

# AWS CLI 및 JQ 버전 확인 (디버깅용)
echo "[DEBUG] aws version: $(aws --version)"
echo "[DEBUG] jq version: $(jq --version)"

# SecretsManager에서 시크릿을 JSON 형태로 가져와서 .env 파일로 변환
SECRET_STRING=$(aws secretsmanager get-secret-value \
    --region "$REGION" \
    --secret-id "$SECRET_NAME" \
    --query SecretString \
    --output text)

if [ -z "$SECRET_STRING" ]; then
    echo "[ENTRYPOINT ERROR] Failed to fetch secret from AWS Secrets Manager. Exiting."
    exit 1
fi

echo "$SECRET_STRING" | jq -r 'to_entries | map("\(.key)=\(.value)") | .[]' > /app/.env

if [ -f /app/.env ]; then
    echo "[ENTRYPOINT] Loaded .env:"
    cat /app/.env

    # export 환경변수로 설정
    export $(cat /app/.env | xargs)
else
    echo "[ENTRYPOINT ERROR] .env 파일이 생성되지 않았습니다. Exiting."
    exit 1
fi

# Spring Boot 실행
echo "[ENTRYPOINT] Starting Spring Boot application..."
exec java -jar app.jar --spring.profiles.active=dev
