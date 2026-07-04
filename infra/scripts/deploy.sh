#!/usr/bin/env sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
docker compose --env-file "$ROOT/.env" -f "$ROOT/compose.yml" -f "$ROOT/compose.prod.yml" pull
docker compose --env-file "$ROOT/.env" -f "$ROOT/compose.yml" -f "$ROOT/compose.prod.yml" up -d --remove-orphans
docker image prune -f
