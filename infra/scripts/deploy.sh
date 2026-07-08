#!/usr/bin/env sh
set -eu

if [ "${DEPLOY_LOCKED:-}" != "1" ]; then
  exec flock /tmp/servicerca-deploy.lock env DEPLOY_LOCKED=1 "$0" "$@"
fi

ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
BACKEND=$(CDPATH= cd -- "$ROOT/.." && pwd)
FRONTEND=$(CDPATH= cd -- "$BACKEND/../serviciosFrontend" && pwd)

git -C "$BACKEND" fetch origin main
git -C "$BACKEND" reset --hard origin/main
git -C "$FRONTEND" fetch origin main
git -C "$FRONTEND" reset --hard origin/main

docker compose --env-file "$ROOT/.env" -f "$ROOT/compose.prod.yml" build "$@"
docker compose --env-file "$ROOT/.env" -f "$ROOT/compose.prod.yml" up -d --remove-orphans

attempt=1
while [ "$attempt" -le 36 ]; do
  if curl -fsS "https://$(sed -n 's/^DOMAIN=//p' "$ROOT/.env")/actuator/health" >/dev/null; then
    docker image prune -f
    exit 0
  fi
  sleep 5
  attempt=$((attempt + 1))
done

docker compose --env-file "$ROOT/.env" -f "$ROOT/compose.prod.yml" ps
docker compose --env-file "$ROOT/.env" -f "$ROOT/compose.prod.yml" logs --tail=100 backend frontend
exit 1
