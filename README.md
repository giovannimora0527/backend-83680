# Clínica veterinaria

Solución formada por una API Spring Boot 4 / Java 21 y una aplicación Angular 19. El stack de presentación incluye MySQL y se inicia con Docker Compose.

## Inicio rápido con Docker Compose

Requisitos: Docker Desktop con Compose habilitado.

1. Desde este directorio, crea tu archivo local de variables:

   ```sh
   cp .env.example .env
   ```

2. Edita `.env`: reemplaza todos los valores `replace_with_...` por claves propias. `JWT_SECRET` debe tener al menos 32 caracteres y `ADMIN_PASSWORD` al menos 12. No compartas ni subas `.env`.
3. Inicia los servicios:

   ```sh
   docker compose up --build -d
   ```

4. Abre:
   - Aplicación: <http://localhost:8088>
   - API: <http://localhost:8080/clinica/v1>
   - Swagger UI: <http://localhost:8080/clinica/v1/swagger-ui/index.html>
   - OpenAPI JSON: <http://localhost:8080/clinica/v1/v3/api-docs>

Al primer inicio, Compose crea la base vacía y el backend agrega datos ficticios de demostración (propietarios, mascotas, médico, citas, medicamentos y lotes). La carga está en `src/main/resources/data.sql`, se ejecuta después de que Hibernate prepara las tablas y evita insertar de nuevo los registros demo en cada reinicio. No borra ni reinicia datos existentes.

El usuario inicial se crea desde `ADMIN_USERNAME` y `ADMIN_PASSWORD` solo si aún no existe. El bootstrap no reemplaza contraseñas de usuarios existentes. Desde el usuario administrador se crean propietarios, veterinarios y cuentas con sus perfiles correspondientes. No hay credenciales de demostración compartidas en el repositorio.

Para detener los servicios conserva los datos de MySQL:

```sh
docker compose down
```

El volumen `mysql_data` se conserva. La base se elimina únicamente si se borra explícitamente ese volumen.

## Ejecución local sin Docker

Requisitos: Java 21, Maven Wrapper y MySQL 8. Antes de iniciar la API, configura `JWT_SECRET` (mínimo 32 caracteres) y, opcionalmente, `ADMIN_USERNAME` y `ADMIN_PASSWORD` en el entorno. Revisa las variables de conexión de `application-dev.properties`, luego ejecuta:

```sh
./mvnw spring-boot:run
```

Desde `frontend26-83680/`, instala dependencias con `npm ci` y ejecuta `npm start`. La aplicación Angular usa por defecto `http://localhost:8080/clinica/v1`.

## Autenticación y perfiles

`POST /auth/login` recibe `{"username":"...","password":"..."}` y devuelve un JWT, su vencimiento y el perfil asociado. Las rutas de negocio requieren `Authorization: Bearer <token>`; el rol de autorización se toma del token, nunca de `X-User-Rol` ni de parámetros del cliente. El frontend redirige al inicio de sesión si el token vence o la API responde 401.

Perfiles disponibles: `ADMINISTRADOR`, `SECRETARIA`, `VETERINARIO`, `DESPACHO_MEDICO` y `CLIENTE`. Las cuentas de veterinario deben asociarse a un médico; las cuentas de cliente, a un propietario. Las cuentas CLIENTE solo pueden consultar mascotas del propietario vinculado a su cuenta.

## Inventario, fórmulas y FEFO

- Cada producto conserva sus datos generales y puede tener varios registros de lote con cantidades, costos y fechas independientes.
- El despacho bloquea lotes en transacción, excluye lotes vencidos y consume primero el lote vigente con vencimiento más próximo (FEFO); para empates usa fecha de ingreso e identificador.
- Cada lote consumido produce su propio movimiento de salida con código de lote, cantidad, usuario autenticado y referencia a la fórmula. El inventario agregado se descuenta en la misma transacción.
- Para una fórmula, el despacho sigue los estados formulada → en preparación → despacho parcial/completo. Las entregas parciales se permiten hasta completar el saldo. El comprobante PDF toma la trazabilidad de movimientos.
- La carga CSV/XLSX permite previsualizar registros válidos y errores antes de confirmar. El lector CSV actual separa por comas y no interpreta comillas con comas incrustadas; preparar el archivo con campos sin comas internas.

## Arquitectura

Backend: contratos HTTP en `api`, controladores en `apicontroller`, reglas de negocio en `service`/`serviceimpl`, acceso de datos en `repository`, persistencia en `entity` y DTOs en `models`. La autorización JWT se aplica en el interceptor de configuración. Hibernate crea/actualiza el esquema según la configuración del perfil.

Frontend: pantallas bajo `src/app/demo/pages`, llamadas HTTP en `src/app/services`, modelos en `src/app/models`, guardias de navegación y sesión en `guards`/`services`.

## Verificación

```sh
./mvnw -Dtest=JwtTokenServiceTest,MedicamentoServiceImplTest,FormulaMedicaServiceImplTest,ReporteServiceImplTest test
```

```sh
npm run build
```

La prueba integral `ClinicaApplicationTests` necesita una base MySQL accesible con las credenciales de prueba configuradas; las pruebas unitarias anteriores no requieren esa base.

La configuración de Compose se puede revisar sin iniciar contenedores con `docker compose --env-file .env.example config --quiet`. El arranque integrado con MySQL y las evidencias visuales deben verificarse en el entorno de presentación.

## Sustentación y evidencias

Flujo sugerido: iniciar Compose, ingresar con el administrador configurado localmente, crear los perfiles y asociaciones, registrar propietarios/mascotas y médicos, crear lotes con vencimientos distintos, formular, marcar preparación, despachar y revisar el PDF/Kardex. También se pueden mostrar calendario, facturación, importación masiva y exportaciones PDF/Excel.

Las evidencias visuales se deben capturar durante una ejecución con datos de presentación; no se incluyen capturas fabricadas en el repositorio.
