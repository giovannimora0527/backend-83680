# Instalación y sustentación

## Ejecución con Docker Compose (presentación)

Se levantan los dos servicios de aplicación (backend y frontend) y MySQL, requerido por el backend.
El archivo Compose está en la raíz de `backend-83680/` para que quede versionado junto al backend.

1. Instalar Docker Desktop.
2. Desde la raíz del workspace, entrar a `backend-83680/` y crear el archivo local de variables:

   ```bash
   cd backend-83680
   cp .env.example .env
   ```

3. Ajustar las contraseñas de `.env` si se compartirá el equipo o la red.
4. Construir y arrancar la solución:

   ```bash
   docker compose up --build -d
   ```

La interfaz queda en `http://localhost:8088` y la API en `http://localhost:8080/clinica/v1`.
Nginx sirve Angular, conserva las rutas de la aplicación y reenvía `/clinica/v1` al backend.
MySQL utiliza el volumen `mysql_data`, por lo que los datos sobreviven al reinicio de los contenedores.
En el primer inicio, el backend carga propietarios, mascotas, médico, citas, medicamentos y lotes ficticios de demostración desde `src/main/resources/data.sql`. La carga es idempotente: reiniciar no duplica esos registros ni borra datos existentes.

Comandos útiles:

```bash
docker compose ps
docker compose logs -f backend
docker compose down
```

`docker compose down` conserva la base de datos. El archivo `.env` es local y no se debe publicar.

## Requisitos

- Java 21
- Maven Wrapper incluido
- Node.js y npm
- Angular CLI compatible con Angular 19
- MySQL 8 con una base de datos llamada `Veterinaria`

## Backend

```bash
cd backend-83680
./mvnw test
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080/clinica/v1`.

## Frontend

```bash
cd frontend26-83680
npm install
npm start
```

La interfaz queda disponible en `http://localhost:4200`.

## Arquitectura

- Backend: API REST, controladores, servicios, implementación de negocio, repositorios JPA, entidades y modelos.
- Frontend: componentes standalone Angular, servicios HTTP, modelos TypeScript y navegación protegida por perfil.
- Integración: el frontend utiliza `http://localhost:8080/clinica/v1`.

## Permisos

Los perfiles admitidos son `ADMINISTRADOR`, `SECRETARIA`, `VETERINARIO` y `DESPACHO_MEDICO`.
El frontend toma el perfil desde `localStorage.rol` y el backend admite la cabecera `X-User-Rol`.

## Usuarios de prueba

No se deben publicar contraseñas reales. Crear usuarios de prueba en el entorno local con los siguientes perfiles:

| Perfil | Acceso |
|---|---|
| ADMINISTRADOR | Administración, citas, historias, facturación y reportes |
| SECRETARIA | Mascotas, médicos, citas y facturación |
| VETERINARIO | Citas, historias, fórmulas y medicamentos |
| DESPACHO_MEDICO | Inventario y despacho de fórmulas |

## Funciones principales

- Inventario, cargas masivas, lotes, movimientos y alertas.
- Citas en calendario, bloqueos y seguimiento.
- Atención médica, fórmulas, dispensación y facturación.
- Reportes financiero, inventario y gestión médica.
- Exportación PDF y Excel.
- Cola de despacho, preparación, despachos parciales y completos.

## Verificación de ejecución

```bash
cd backend-83680 && ./mvnw test
cd frontend26-83680 && npm run build
```
