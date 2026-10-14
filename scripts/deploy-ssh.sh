#!/usr/bin/env bash
set -Eeuo pipefail

required=(DEPLOY_ENVIRONMENT IMAGE_REF DEPLOY_HOST DEPLOY_USER DEPLOY_DIRECTORY DEPLOY_SSH_PRIVATE_KEY DEPLOY_KNOWN_HOSTS DEPLOY_ENV_FILE)
for name in "${required[@]}"; do
  if [[ -z "${!name:-}" ]]; then
    printf 'Required deployment setting is missing: %s\n' "$name" >&2
    exit 2
  fi
done

case "$DEPLOY_ENVIRONMENT" in
  staging)
    compose_file="compose.staging.yaml"
    health_port="${STAGING_HEALTH_PORT:-8081}"
    ;;
  production)
    compose_file="compose.production.yaml"
    health_port="${PRODUCTION_HEALTH_PORT:-8082}"
    ;;
  *)
    printf 'Unsupported deployment environment: %s\n' "$DEPLOY_ENVIRONMENT" >&2
    exit 2
    ;;
esac

if [[ ! "$IMAGE_REF" =~ ^ghcr\.io/[a-z0-9._-]+/[a-z0-9._-]+@sha256:[a-f0-9]{64}$ ]]; then
  printf 'IMAGE_REF must be an immutable GHCR sha256 reference.\n' >&2
  exit 2
fi

if [[ ! "$DEPLOY_DIRECTORY" =~ ^/[a-zA-Z0-9_./-]+$ ]]; then
  printf 'DEPLOY_DIRECTORY must be an absolute path using only letters, numbers, slash, dot, underscore, and hyphen.\n' >&2
  exit 2
fi

temporary_directory="$(mktemp -d)"
trap 'rm -rf "$temporary_directory"' EXIT
key_file="$temporary_directory/deploy_key"
known_hosts_file="$temporary_directory/known_hosts"
printf '%s\n' "$DEPLOY_SSH_PRIVATE_KEY" > "$key_file"
printf '%s\n' "$DEPLOY_KNOWN_HOSTS" > "$known_hosts_file"
chmod 600 "$key_file" "$known_hosts_file"

ssh_options=(-i "$key_file" -o IdentitiesOnly=yes -o BatchMode=yes -o StrictHostKeyChecking=yes -o "UserKnownHostsFile=$known_hosts_file" -o ConnectTimeout=20)
target="${DEPLOY_USER}@${DEPLOY_HOST}"
remote_directory="$DEPLOY_DIRECTORY"

ssh "${ssh_options[@]}" "$target" "mkdir -p '$remote_directory'"
scp "${ssh_options[@]}" "$compose_file" "$target:$remote_directory/compose.yaml"
printf '%s\n' "$DEPLOY_ENV_FILE" | ssh "${ssh_options[@]}" "$target" "umask 077; cat > '$remote_directory/.env'"

if [[ -n "${GHCR_READ_TOKEN:-}" ]]; then
  if [[ -z "${GHCR_USERNAME:-}" ]]; then
    printf 'GHCR_USERNAME is required when GHCR_READ_TOKEN is set.\n' >&2
    exit 2
  fi
  printf '%s' "$GHCR_READ_TOKEN" | ssh "${ssh_options[@]}" "$target" "docker login ghcr.io --username '$GHCR_USERNAME' --password-stdin"
fi

ssh "${ssh_options[@]}" "$target" sh -s -- "$remote_directory" "$IMAGE_REF" "$health_port" <<'REMOTE_SCRIPT'
set -eu
directory="$1"
image_ref="$2"
health_port="$3"
cd "$directory"
trap 'docker logout ghcr.io >/dev/null 2>&1 || true' EXIT
API_IMAGE="$image_ref" docker compose --env-file .env -f compose.yaml pull api
docker logout ghcr.io >/dev/null 2>&1 || true
trap - EXIT
API_IMAGE="$image_ref" docker compose --env-file .env -f compose.yaml up -d --remove-orphans

for attempt in $(seq 1 30); do
  if curl --connect-timeout 3 --max-time 5 --fail --silent "http://127.0.0.1:${health_port}/actuator/health" >/dev/null; then
    printf 'Deployment is healthy at 127.0.0.1:%s.\n' "$health_port"
    exit 0
  fi
  sleep 5
done

API_IMAGE="$image_ref" docker compose --env-file .env -f compose.yaml logs --tail=100 api mongo || true
exit 1
REMOTE_SCRIPT