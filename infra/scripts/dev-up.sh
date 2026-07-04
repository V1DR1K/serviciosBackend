#!/usr/bin/env sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
docker compose --env-file "$ROOT/.env" -f "$ROOT/compose.yml" up --build -d
docker compose --env-file "$ROOT/.env" -f "$ROOT/compose.yml" ps
