# ServiCerca API

API del marketplace local construida con Java 21, Spring Boot 3.5, PostgreSQL/PostGIS, JWT, OAuth2, WebSocket y Ceph RGW. Sigue la arquitectura de Kaza con paquetes `controller`, `service`, `repository`, `model`, `dto`, `mapper`, `security`, `config` y `exception`; las entidades nunca se exponen desde los controllers.

## Inicio rápido con Docker

```powershell
cd infra
Copy-Item .env.example .env
docker compose --env-file .env up --build -d
```

La aplicación queda en `http://localhost:4200`, Swagger en `http://localhost:8080/swagger-ui.html` y Ceph RGW en `http://localhost:8000`.

Usuarios demo (contraseña `Demo1234!`):

- `cliente@demo.com`
- `profesional@demo.com`

La base PostGIS ya fue levantada y validada mediante el Compose. Para detener sin borrar datos: `docker compose stop`. Para eliminar también los volúmenes: `docker compose down -v`.

## Desarrollo

Requisitos: Java 21, Maven 3.9, Node 20 y Docker Desktop.

```powershell
mvn test
mvn spring-boot:run
```

Las migraciones Flyway crean PostGIS, usuarios, oficios, pedidos, postulaciones, calificaciones, reportes, tokens, notificaciones y archivos. El perfil `dev` agrega los datos demo. Las recuperaciones de contraseña se escriben como URL mediante `logger.info` solamente en dicho perfil.

## Configuración externa

- Google: crear credenciales OAuth Web y autorizar `https://DOMINIO/login/oauth2/code/google`; completar `GOOGLE_CLIENT_ID` y `GOOGLE_CLIENT_SECRET`.
- Push: generar un par VAPID y completar las variables correspondientes.
- Ceph: el contenedor `quay.io/ceph/demo` es exclusivamente local. Producción debe usar un clúster externo desplegado con `cephadm`, un usuario RGW limitado al bucket y TLS.
- Secretos: copiar `.env.example` a `.env`; nunca versionar el archivo resultante.

## Producción en VPS Linux

Instalar Docker Engine y Compose, copiar `infra`, completar `.env`, configurar `S3_ENDPOINT` hacia Ceph RGW y ejecutar:

```sh
chmod +x infra/scripts/*.sh
infra/scripts/deploy.sh
```

El frontend escucha solo en `127.0.0.1:4200`; colocar Caddy, Traefik o Nginx delante para TLS y dominio. Configurar en Docker Hub los repositorios `servicerca-backend` y `servicerca-frontend`. Los workflows esperan `DOCKERHUB_USERNAME` y `DOCKERHUB_TOKEN`. El script `publish.ps1` permite la publicación manual.

Backups: `infra/scripts/backup.sh` o `backup.ps1`. Para restaurar: detener el backend y ejecutar `pg_restore --clean --if-exists -U servicerca -d servicerca archivo.dump` dentro del contenedor de DB.
