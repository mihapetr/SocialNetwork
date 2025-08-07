#!/bin/bash

############################ SETUP ###################################

input_file="test_features_selection.txt"
mkdir -p src/test/resources/selected_features
rm -f src/test/resources/selected_features/*

while IFS= read -r line; do
	[[ -z "$line" ]] && continue	# skip empty lines
  if ! cp "src/test/resources/features/$line" "src/test/resources/selected_features/$line"; then
  	echo -e "Please check the formatting of the feature selection file.\nEvery line has to be a valid feature name from the /src/test/resources/features.\nErorr in line $line"
  	exit 1
	fi
done < "$input_file"

# config file created for the user
source	./mhipster/test_features.conf

# overriding the config file for testing
if [ "$#" -gt 0 ]; then
		BASE_URL="http://localhost:${1}"
		PROJECT_ID="${2}"
		login="${3}"
		password="${4}"
fi

echo "marking executables"
chmod u+x ./mhipster/get_jwt.sh ./mhipster/m_generate.sh ./mhipster/post.sh

# jq is required to run this script
if ! command -v jq &>/dev/null; then
  	echo "jq is not installed. Please install with 'sudo apt-get install -y jq'"
  	exit
fi

# ask user for MHipster credentials (login, password) required for JWT fetching
if [ "$#" -eq 0 ]; then
		read -p "MHipster username: " login
		read -s -p "MHipster password: " password
		echo
fi

echo "authenticating at endpoint $BASE_URL/api/authenticate with username $login"
JWT=$(
		./mhipster/get_jwt.sh "$BASE_URL/api/authenticate" "$login" "$password"
)

# read the package name user has chosen from .yo-rc.json
packageName=$(jq -r '.["generator-jhipster"].packageName' .yo-rc.json)

############################# SOURCE RUN ####################################

echo "changing the retention policy of @NotGenerated at $NOT_GENERATED_DIR to SOURCE with packageName $packageName"
./mhipster/m_generate.sh "$NOT_GENERATED_DIR" "$packageName" SOURCE

echo "performing integration testing"
./mvnw clean verify -Pmhipster-it

########################### SUCCESS CHECK #############################

# check if build was successful
if [ $? -ne 0 ]; then
		echo "exiting because test failed"
		exit
fi

echo "posting report to $BASE_URL/api/test-reports/of-project/$PROJECT_ID with token $JWT"
RESPONSE=$(
		./mhipster/post.sh  "$BASE_URL/api/test-reports/of-project/$PROJECT_ID" "$JWT"
)
FEATURE_TEST_ID=$(echo "$RESPONSE" | jq -r '.featureTst.id')

############################# RUNTIME RUN ###############################

echo "changing the retention policy of @NotGenerated at $NOT_GENERATED_DIR to RUNTIME with packageName $packageName"
./mhipster/m_generate.sh "$NOT_GENERATED_DIR" "$packageName" RUNTIME

echo "performing integration testing"
./mvnw verify -Pmhipster-it

echo "posting report to $BASE_URL/api/test-reports/of-feature-test/$FEATURE_TEST_ID with token $JWT"

# overriding for testing
if [ "$#" -gt 0 ]; then
		./mhipster/post_dev.sh  "$BASE_URL/api/test-reports/of-feature-test/$FEATURE_TEST_ID?features=${5}" "$JWT"
else
    ./mhipster/post.sh  "$BASE_URL/api/test-reports/of-feature-test/$FEATURE_TEST_ID" "$JWT"
fi
