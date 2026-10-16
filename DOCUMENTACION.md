# Segundo parcial - Módulo de citas, historias clínicas y fórmulas médicas

Backend: Spring Boot (api → apicontroller → service → serviceimpl → repository → entity / models).
Frontend: Angular 19 + Bootstrap (componentes, services, models y utils).

## 1. Modelo de datos

```
CLIENTE 1:N MASCOTA
MASCOTA 1:N CITA              CITA N:1 VETERINARIO (medico)
MASCOTA 1:N HISTORIA_MEDICA   HISTORIA_MEDICA N:1 CITA (una historia por cita atendida)
HISTORIA_MEDICA 1:N ANOTACION_HISTORIA
CITA 1:N FORMULA_MEDICA       FORMULA_MEDICA N:1 MEDICAMENTO / VETERINARIO / MASCOTA
```

Cambios sobre la base entregada (archivo `database/script_parcial.sql`):

| Tabla | Columnas agregadas |
|---|---|
| `cita` | Estados normalizados en mayúscula. Índices por veterinario/fecha y mascota/fecha. |
| `historia_medica` | `cliente_id`, `cita_id` (único), `medico_id`, `fecha_hora`, `motivo`, `diagnostico`, `observaciones`, `estado` (ABIERTA/CERRADA). `paciente_id` es la mascota. |
| `anotacion_historia` | `observacion`, `diagnostico` (diagnóstico o evolución). |
| `formula_medica` | `mascota_id`, `medico_id`, `fecha_emision`, `frecuencia`, `duracion_dias`, `activo`. Las fórmulas existentes toman la mascota y el veterinario de su cita. |

## 2. Endpoints (base `http://localhost:8080/clinica/v1`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/cliente/listar` | Clientes activos |
| GET | `/cliente/consultar?clienteId=` | Un cliente |
| GET | `/mascota/listar-by-cliente?clienteId=` | Mascotas de un cliente |
| GET | `/mascota/consultar?mascotaId=` | Una mascota |
| GET | `/medico/listar` | Veterinarios |
| GET | `/medicamento/listar` | Medicamentos |
| GET | `/cita/listar` | Citas (fecha y hora descendente). Acepta `fechaInicial` y `fechaFinal` opcionales |
| GET | `/cita/consultar?citaId=` | Una cita |
| GET | `/cita/listar-by-mascota?mascotaId=` | Citas de una mascota |
| POST | `/cita/guardar` | Crear cita |
| PUT | `/cita/actualizar` | Editar cita según su estado |
| PATCH | `/cita/reactivar?citaId=` | Reactivar una cita cancelada |
| GET | `/historia-medica/listar` | Historias (fecha de consulta descendente) |
| GET | `/historia-medica/consultar?historiaMedicaId=` | Una historia |
| POST | `/historia-medica/guardar` | Crear historia desde una cita atendida |
| PUT | `/historia-medica/actualizar` | Editar diagnóstico, observaciones y estado |
| GET | `/anotacion-historia/listar-by-historia?historiaMedicaId=` | Anotaciones de una historia (más reciente primero) |
| POST | `/anotacion-historia/guardar` | Agregar anotación |
| GET | `/formula-medica/listar` | Fórmulas (fecha de creación descendente) |
| GET | `/formula-medica/consultar?formulaId=` | Detalle de una fórmula |
| POST | `/formula-medica/guardar` | Crear fórmula |
| PUT | `/formula-medica/actualizar` | Editar fórmula activa |
| PATCH | `/formula-medica/inactivar?formulaId=` | Inactivar fórmula (no se borra) |

Respuestas de éxito: `{ "status": 201|200, "message": "..." }`.
Respuestas de error: `{ "status": ..., "error": "...", "message": "..." }`.

| Código | Cuándo |
|---|---|
| 200 / 201 | Consulta o actualización correcta / registro creado |
| 400 | Campos obligatorios, datos inválidos, relaciones incorrectas (mascota de otro cliente, cita de otra mascota, fechas) |
| 404 | Cliente, mascota, cita, veterinario, historia, fórmula o medicamento inexistente |
| 405 | Método HTTP no permitido |
| 409 | Duplicados, veterinario o mascota ocupados, registro que ya no se puede modificar |
| 500 | Error no controlado (se registra en el log y al usuario se le muestra un mensaje general) |

## 3. Reglas de negocio principales

**Citas**
- El cliente, la mascota y el veterinario deben existir; la mascota debe pertenecer al cliente.
- No se registran citas en fechas anteriores a la actual.
- Un veterinario no puede tener otra cita 15 minutos antes o después (rango inclusivo). Una mascota tampoco. Las citas canceladas no ocupan horario.
- Una cita nueva solo puede quedar Pendiente, Programada o Confirmada.
- Edición: se puede cambiar veterinario, fecha, hora, motivo y estado. El cliente y la mascota no cambian (si son otros es una cita nueva).
- Una cita **atendida** no se modifica (es información histórica). Una cita **cancelada** no se modifica; solo se recupera con la operación explícita "reactivar", que la deja Pendiente y vuelve a validar el horario.

**Historias clínicas y anotaciones**
- Se crean a partir de una cita **atendida** de la mascota; una cita solo genera una historia.
- Se valida cliente → mascota → cita, y que la fecha de la consulta no sea anterior a la de la cita.
- En la edición solo cambian diagnóstico, observaciones y estado. Una historia **cerrada** no se modifica ni recibe anotaciones.
- La fecha y hora de la anotación la asigna el servidor al guardar.

**Fórmulas médicas**
1. La mascota debe existir y 2. pertenecer a un cliente registrado y activo.
3. El veterinario debe existir.
4. La fecha de emisión no puede ser anterior a la fecha de la consulta.
5. La dosis debe indicar una cantidad (ej. "500 mg") y la duración debe estar entre 1 y 365 días.
6. Los campos obligatorios se validan en Angular y en el backend.
7. La consulta (cita) debe ser de la misma mascota y no puede estar cancelada.
8. No hay eliminación física: se inactiva (`activo = 0`) y una fórmula inactiva ya no se edita.
- No se repite el mismo medicamento en la misma consulta. En la edición la mascota y la consulta no cambian.

## 4. Decisiones técnicas

- La base entregada no tiene llaves foráneas y los tipos no coinciden (`int` contra `bigint unsigned`), por eso la integridad de las relaciones se valida en la capa de servicio antes de guardar.
- Se agregaron `NotFoundException` (404) y `ConflictException` (409) junto a la `BadRequestException` existente, todas atendidas en el `GlobalExceptionHandler`. Los mensajes no muestran detalles técnicos.
- Los estados y límites se centralizan en `utils/Constantes.java`.
- Las listas se cargan completas y la paginación, los filtros y el ordenamiento se hacen en Angular, igual que en los módulos que ya existían (el volumen de datos es pequeño).
- En Angular se crearon `utils/tabla-utils.ts` (búsqueda, orden, filtros de fecha) y `utils/mensajes-utils.ts` (alertas SweetAlert) para no repetir código entre módulos.
- Combos dependientes: Cliente → Mascotas del cliente (consultadas al backend) → Citas de la mascota.
- Se subió el `z-index` de SweetAlert porque el tema pone los modales por encima y las alertas quedaban ocultas.

## 5. Cómo ejecutar

1. Cargar el respaldo base de la base de datos y luego ejecutar `database/script_parcial.sql`.
2. Backend: `mvnw spring-boot:run` (perfil `dev`, puerto 8080).
3. Frontend: `npm install` y `ng serve` (puerto 4200).
