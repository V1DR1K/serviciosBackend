#!/usr/bin/env sh
set -eu

case "${1:-}" in
  backend|api)
    component=api
    ;;
  frontend|web)
    component=web
    ;;
  *)
    echo "Usage: $0 {backend|frontend}" >&2
    exit 64
    ;;
esac

exec /opt/infra/bin/deploy-service servicerca "$component"
