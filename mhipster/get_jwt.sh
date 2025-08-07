#!/bin/bash

SERVER_URL="${1}"
LOGIN="${2}"
PASSWORD="${3}"

read -r -d '' PAYLOAD <<EOF
{
	"username": "$LOGIN",
	"password": "$PASSWORD",
	"rememberMe": false
}
EOF

RESPONSE=$(
	curl -X POST "$SERVER_URL" \
	-H "Content-Type: application/json" \
	-d "$PAYLOAD"
)

TOKEN=$(echo "$RESPONSE" | jq -r '.id_token')

echo "$TOKEN"
