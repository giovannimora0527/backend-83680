# backend-83680
Backend para una clínica veterinaria (Spring Boot 4, Java 21, MySQL).

## Arquitectura
`api` (rutas) → `apicontroller` (controlador) → `service` / `serviceimpl` (lógica de negocio)
→ `repository` (acceso a datos) → `entity`. Los datos de entrada y salida viajan en `models` (DTO `Rq` / `Rs`).
Documentación:
- [docs/GUIA_PARA_EXPONER.md](docs/GUIA_PARA_EXPONER.md): guía paso a paso para entender el código y sustentarlo (nivel junior).
- [docs/DOCUMENTACION_TECNICA.md](docs/DOCUMENTACION_TECNICA.md): referencia técnica (versiones, tablas, SQL).

## Ejecución
Requiere MySQL con el esquema `clinica` (usuario y clave en `application-dev.properties`).

```
./mvnw spring-boot:run
```

Base de la API: `http://localhost:8080/clinica/v1`

## Servicios del parcial
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/formula-medica/listar` | Fórmulas médicas, de la más reciente a la más antigua |
| GET | `/cita/listar?fechaInicial=yyyy-MM-dd&fechaFinal=yyyy-MM-dd` | Citas del rango, de la más reciente a la más antigua |
| POST | `/cita/guardar` | Crea una cita |
| POST | `/cita/actualizar` | Actualiza una cita |
| GET | `/anotacion-historia/listar?fechaInicial=&fechaFinal=` | Anotaciones del rango, de la más reciente a la más antigua |
| POST | `/anotacion-historia/guardar` | Crea una anotación en una historia médica |
| POST | `/anotacion-historia/actualizar` | Actualiza una anotación |

Los errores se devuelven como `{"status": 400, "error": "BAD REQUEST", "message": "..."}`.
