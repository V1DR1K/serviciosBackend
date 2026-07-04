param([Parameter(Mandatory=$true)][string]$Namespace,[string]$Tag='latest')
$ErrorActionPreference='Stop'; $Here=Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
docker build -t "$Namespace/servicerca-backend:$Tag" $Here
docker build -t "$Namespace/servicerca-frontend:$Tag" (Join-Path (Split-Path -Parent $Here) 'serviciosFrontend')
docker push "$Namespace/servicerca-backend:$Tag"; docker push "$Namespace/servicerca-frontend:$Tag"
