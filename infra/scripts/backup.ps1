$ErrorActionPreference = 'Stop'
$Root = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$Stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
New-Item -ItemType Directory -Force "$Root\backups" | Out-Null
docker compose --env-file "$Root\.env" -f "$Root\compose.yml" exec -T db pg_dump -Fc -U servicerca servicerca > "$Root\backups\db-$Stamp.dump"
