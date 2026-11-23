# Guia de ejecucion - Examen Final

## Requisitos

- Docker Desktop
- Docker Compose v2

## Ejecucion

```powershell
cd C:\Users\JuanR\Desktop\Amigos\Alma\ENTREGA\EntregaParcialAlmaHernandez
docker compose up --build
```

- Frontend: http://localhost:4200
- Backend: http://localhost:8000/clinica/v1
- Swagger: http://localhost:8000/clinica/v1/swagger-ui/index.html

## Modelo de datos

Importe primero `backup-session.sql` y luego `examen-final-migration.sql`. Docker Compose ejecuta ambos automaticamente al crear el volumen por primera vez.

Documentación completa: [MODELO-ENTIDAD-RELACION.md](MODELO-ENTIDAD-RELACION.md)

## Credenciales de prueba

No se deben usar contrasenas reales en este repositorio. Cree usuarios desde el API con contrasenas temporales y roles:

- Administrador
- Recepcion
- Medico
- DESPACHO_MEDICO

Las contrasenas de usuarios nuevos se almacenan con BCrypt.

Usuario temporal precargado:

- Usuario: `despacho`
- Contrasena temporal: `DemoTemporal2026!`
- Rol: `DESPACHO_MEDICO`

## Estado de implementacion

El modelo actualizado incluye inventario, lotes, movimientos, citas, formulas, despacho, facturacion, pagos y tarifas. Las interfaces existentes deben completarse con CRUD masivo, calendario, PDF/Excel y reporte segun el alcance de sustentacion.
