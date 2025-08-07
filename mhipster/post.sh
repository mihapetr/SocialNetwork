#!/bin/bash

SERVER_URL="${1}"
JWT="${2}"
REPORT_PATH="./target/site/jacoco-mhipster/index.html"

# processing directory is project root
filter=$(awk -F'__' '{print $2}' test_features_selection.txt | paste -sd, -)

RESPONSE=$(
	curl -X POST "$SERVER_URL?features=$filter" \
	-H "Authorization: Bearer $JWT" \
	-H "Content-Type: text/html" \
	--data-binary "@$REPORT_PATH"
)

echo "$RESPONSE"
