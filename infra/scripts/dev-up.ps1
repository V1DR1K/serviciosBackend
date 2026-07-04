$ErrorActionPreference = 'Stop'
$Root = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
docker compose --env-file "$Root\.env" -f "$Root\compose.yml" up --build -d
docker compose --env-file "$Root\.env" -f "$Root\compose.yml" ps
