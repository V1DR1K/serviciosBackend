#!/usr/bin/env sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd); mkdir -p "$ROOT/backups"
docker compose --env-file "$ROOT/.env" -f "$ROOT/compose.yml" exec -T db pg_dump -Fc -U "${POSTGRES_USER:-servicerca}" "${POSTGRES_DB:-servicerca}" > "$ROOT/backups/db-$(date +%Y%m%d-%H%M%S).dump"
