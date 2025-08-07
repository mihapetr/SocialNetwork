#!/bin/bash

SERVER_URL="${1}"
JWT="${2}"
REPORT_PATH="./target/site/jacoco-mhipster/index.html"

RESPONSE=$(
	curl -X POST "$SERVER_URL" \
	-H "Authorization: Bearer $JWT" \
	-H "Content-Type: text/html" \
	--data-binary "@$REPORT_PATH"
)

echo "$RESPONSE"
