#!/bin/sh

echo 'Substituting environment variables'

# Set default values for environment variables
export ENV_NAME="${ENV_NAME:-local}"
export EMS_API="${EMS_API:-http://localhost:8083}"
export UECDI_API="${UECDI_API:-http://localhost:5000}"
export UECDI_VALIDATE_API="${UECDI_VALIDATE_API:-http://localhost:7000/tkw-client}"
export TERM_API="${TERM_API:-https://ontoserver.dataproducts.nhs.uk/fhir}"
export USER_GUIDE="${USER_GUIDE:-https://uec-connect-conformance-guide.netlify.app/}"

# shellcheck disable=SC2016
SUBST_VARS='$ENV_NAME,$EMS_API,$UECDI_API,$UECDI_VALIDATE_API,$TERM_API,$USER_GUIDE'

TMP_FILE=$(mktemp)
SUBST_FILE=$(ls main.*.js)
chmod go+w "$SUBST_FILE"

# Substitute specified environment variables
envsubst "$SUBST_VARS" <"$SUBST_FILE" >"$TMP_FILE"
mv "$TMP_FILE" "$SUBST_FILE"

# Start nginx server
echo "Starting Angular app in nginx on $HOSTNAME"
nginx -g "daemon off;"