# Requisitos faltantes del examen final

Este documento resume el estado de implementación de los requerimientos del examen. Las casillas indican funcionalidad implementada; no sustituyen una validación de extremo a extremo en el entorno de presentación.

## Estado general

- Backend: implementado en sus módulos principales
- Frontend: implementado en sus módulos principales
- Exportación PDF/Excel, despacho y permisos por perfil incorporados
- Pendiente de validar con MySQL y Compose: arranque integrado, persistencia/esquema actualizado y recorrido completo de los perfiles
- Pendiente de capturar durante una ejecución real: evidencias visuales para la sustentación
- Limitación conocida: el lector CSV no maneja comas dentro de campos entrecomillados; usar valores sin comas internas

## Requisito 1. Módulo de inventario y gestión de medicamentos

### Estado: Implementado en la capa de negocio principal

#### 1.1 Administración de medicamentos y productos
- [x] Listar todos los medicamentos y productos registrados
- [x] Consultar medicamentos disponibles en inventario
- [x] Visualizar cantidades disponibles
- [x] Visualizar precios de compra y venta al público
- [x] Registrar nuevos medicamentos y productos médicos
- [x] Editar información existente
- [x] Consultar estado del inventario
- [x] Consultar fechas de vencimiento
- [x] Identificar productos agotados
- [x] Identificar existencias bajas
- [x] Identificar productos próximos a vencer
- [x] Almacenar campo de registro Invima
- [x] Diferenciar medicamentos con registro Invima vs utensilios sin Invima

#### 1.2 Gestión de precios y existencias
- [x] Almacenar precio de compra
- [x] Almacenar precio de venta al público
- [x] Almacenar cantidad actual disponible
- [x] Almacenar stock mínimo permitido
- [x] Almacenar fecha de ingreso al inventario
- [x] Almacenar fecha del próximo pedido
- [x] Registrar información de lotes y fechas de vencimiento
- [x] Soportar múltiples lotes por medicamento
- [x] Soportar múltiples precios de adquisición por lote
- [x] Código de 8 caracteres alfanuméricos por medicamento
- [x] Validar unicidad de medicamento según regla de negocio

#### 1.3 Integración con las fórmulas médicas
- [x] Consultar disponibilidad del medicamento al formular
- [x] Validar existencia suficiente
- [x] Calcular cantidad total requerida
- [x] Descontar al confirmar dispensación
- [x] Evitar cantidades negativas en inventario
- [x] Registrar movimiento de salida
- [x] Registrar fecha, medicamento, cantidad, usuario y referencia de la fórmula
- [x] Diferenciar formulación de dispensación
- [x] Consultar historial de entradas y salidas por medicamento

#### 1.4 Carga masiva de inventario
- [x] Cargar archivos Excel (.xlsx)
- [x] Cargar archivos CSV (.csv)
- [x] Procesar más de 50 registros
- [x] Validar estructura del archivo
- [x] Validar encabezados
- [x] Validar campos obligatorios
- [x] Validar duplicados
- [x] Validar precios
- [x] Validar cantidades
- [x] Validar fechas de compra
- [x] Validar fecha de vencimiento
- [x] Vista previa antes de confirmar importación
- [x] Separar registros correctos y con errores
- [x] Generar informe de errores con fila y motivo
- [x] Confirmar importación solo de registros válidos
- [x] Registrar fecha y usuario que realizó la carga
- [x] Registrar log de cargue
- [x] Asegurar que un error en un registro no corrompa otros registros

#### Entregable mínimo recomendado
- Backend: entidad de inventario, lote, movimiento y carga masiva
- Frontend: listado, filtros, carga Excel/CSV, detalle de stock y vencimiento

---

## Requisito 2. Módulo de gestión de citas médicas mediante calendario

### Estado: Implementado

#### 2.1 Calendario médico
- [x] Vista de calendario para citas del veterinario autenticado
- [x] Consultar citas por día
- [x] Consultar citas por semana
- [x] Consultar citas por mes
- [x] Identificar visualmente citas por estado
- [x] Consultar información de cada cita
- [x] Identificar propietario de la mascota
- [x] Consultar datos básicos del paciente
- [x] Consultar motivo de la consulta
- [x] Visualizar fecha y hora de inicio y fin
- [x] Cambiar fecha y hora cuando el sistema lo permita

#### 2.2 Gestión de citas
- [x] Registrar nueva cita médica
- [x] Editar cita existente
- [x] Marcar cita como atendida
- [x] Identificar citas pendientes
- [x] Identificar citas confirmadas
- [x] Identificar citas canceladas
- [x] Identificar citas finalizadas
- [x] Generar reporte Excel con citas del médico asociado
- [x] Filtrar por fecha inicial y final
- [x] Consultar historial de citas de una mascota

#### 2.3 Reglas de negocio
- [x] Validar disponibilidad del médico
- [x] Respetar bloqueos de horario
- [x] Evitar conflictos de agenda
- [x] Validar estado de la cita
- [x] Permitir seguimiento de cambios de cita

#### Relevante al proyecto actual
- Se adaptó la pantalla de citas a una vista de calendario (Día, Semana, Mes y Lista) con separación por perfil (`VETERINARIO`, `SECRETARIA`, `ADMINISTRADOR`), gestión de bloqueos de horario, seguimiento de cambios, historial por mascota y exportación a Excel.

---

## Requisito 3. Atención médica, fórmulas y facturación

### Estado: Implementado

#### 3.1 Atención médica y formulación
- [x] Registrar atención médica
- [x] Generar fórmula médica
- [x] Prescribir medicamentos con dosis
- [x] Prescribir medicamentos con frecuencia
- [x] Prescribir medicamentos con duración del tratamiento
- [x] Guardar indicaciones médicas
- [x] Relacionar fórmula con cita y mascota
- [x] Diferenciar fórmula de dispensación

#### 3.2 Facturación
- [x] Generar factura por atención atendida
- [x] Incluir detalles de servicios y medicamentos
- [x] Calcular descuentos si aplica
- [x] Calcular total a pagar
- [x] Registrar fecha de generación de factura
- [x] Registrar estado de pago
- [x] Soportar estados: pendiente, parcial, pagada, anulada
- [x] Registrar pagos asociados a factura
- [x] Consultar historial de facturación por propietario y mascota

#### 3.3 Reglas de negocio de facturación
- [x] Una cita atendida podrá generar factura
- [x] No cobrar dos veces el mismo servicio
- [x] Conservar precios históricos en factura
- [x] Calcular impuestos según configuración aplicada
- [x] Facturar solo medicamentos efectivamente dispensados
- [x] Mantener trazabilidad de operaciones
- [x] Controlar anulación de factura sin eliminar historial

#### Relevante al proyecto actual
- Se implementaron las entidades `Factura`, `DetalleFactura` y `PagoFactura`, endpoints de preliquidación, emisión, abonos parciales/totales, anulación controlada sin eliminación de historial y pantalla de facturación con historial por propietario y mascota.

---

## Requisito 4. Módulo general de reportes administrativos y contables

### Estado: Completado

#### 4.1 Reportes financieros
- [x] Ingresos por período
- [x] Ingresos por consultas médicas
- [x] Ingresos por especialidad
- [x] Ingresos por veterinario
- [x] Ingresos por venta de medicamentos
- [x] Total de facturas generadas
- [x] Facturas pagadas
- [x] Facturas pendientes
- [x] Pagos parciales
- [x] Impuestos recaudados
- [x] Descuentos aplicados
- [x] Resumen por día, mes y año

#### 4.2 Reportes de inventario
- [x] Existencias actuales
- [x] Valor del inventario según costo de adquisición
- [x] Valor potencial de venta del inventario
- [x] Medicamentos agotados
- [x] Medicamentos con stock mínimo
- [x] Medicamentos próximos por vencer
- [x] Medicamentos vencidos
- [x] Entradas y salidas de inventario
- [x] Historial de movimientos por medicamento

#### 4.3 Reportes de gestión médica
- [x] Número de citas por período
- [x] Citas atendidas
- [x] Citas canceladas
- [x] Citas pendientes
- [x] Consultas realizadas por veterinario
- [x] Consultas realizadas por especialidad
- [x] Mascotas atendidas por período

#### 4.4 Exportación de información
- [x] Seleccionar rango de fechas
- [x] Aplicar filtros por tipo de reporte
- [x] Ver resultados antes de exportar
- [x] Descargar resultados en PDF
- [x] Descargar resultados en Excel
- [x] Incluir encabezados, fecha de generación y totales
- [x] Restringir acceso según permisos del usuario

---

## Requisito 5. Módulo de despacho y dispensación de medicamentos

### Estado: No implementado

#### 5.1 Gestión de usuarios y permisos
- [x] Crear perfil DESPACHO_MEDICO
- [x] Permitir acceso solo a inventario y despacho
- [x] Bloquear acceso a citas médicas
- [x] Bloquear acceso a atención médica
- [x] Bloquear acceso a historias clínicas
- [x] Bloquear acceso a creación de fórmulas
- [x] Bloquear acceso a facturación
- [x] Bloquear acceso a reportes administrativos y financieros
- [x] Bloquear acceso a administración de usuarios
- [x] Controlar permisos desde backend
- [x] Restringir navegación del frontend por perfil

#### 5.2 Consulta de fórmulas pendientes de despacho
- [x] Listar fórmulas pendientes
- [ ] Mostrar número o identificador
- [ ] Mostrar fecha de generación
- [ ] Mostrar médico veterinario
- [ ] Mostrar propietario
- [ ] Mostrar mascota
- [ ] Mostrar medicamentos formulados
- [ ] Mostrar cantidades prescritas
- [ ] Mostrar dosis y frecuencia
- [ ] Mostrar duración del tratamiento
- [ ] Mostrar indicaciones médicas
- [ ] Mostrar estado del despacho
- [x] Buscar por identificación, propietario, mascota o fecha

#### 5.3 Validación y preparación del despacho
- [ ] Consultar cantidades formuladas
- [ ] Consultar existencias actuales
- [ ] Consultar lotes disponibles
- [ ] Identificar medicamentos vencidos
- [ ] Identificar medicamentos próximos a vencer
- [ ] Validar inventario suficiente
- [ ] Seleccionar lotes de dispensación
- [ ] Registrar cantidades entregadas
- [ ] Identificar medicamentos sin existencia
- [ ] Priorizar lotes con vencimiento más próximo
- [x] Permitir despachos parciales
- [x] Impedir entregas con inventario insuficiente

#### 5.4 Confirmación de entrega y actualización del inventario
- [x] Registrar fecha y hora de dispensación
- [x] Registrar usuario responsable
- [x] Registrar medicamentos entregados
- [x] Registrar cantidades efectivamente dispensadas
- [x] Identificar lotes utilizados
- [x] Descontar cantidades del inventario
- [x] Generar movimientos de salida
- [x] Relacionar movimientos con la fórmula
- [x] Generar comprobante PDF
- [x] Ejecutar transacción atómica
- [x] Garantizar consistencia ante errores

#### 5.5 Estados del proceso de despacho
- [x] Pendiente de despacho
- [x] En preparación
- [x] Despacho parcial
- [x] Despacho completo
- [x] Entregado
- [x] Cancelado

---

## Requisito adicional del proyecto: módulo de clientes, usuarios y roles

### Estado: Parcial

- [ ] Gestión de clientes
- [ ] Registro de propietarios
- [ ] Consulta de propietarios y mascotas asociadas
- [ ] Gestión de usuarios
- [ ] Roles y permisos por perfil
- [ ] Autenticación y autorización
- [ ] Asociación de usuarios con clientes y médicos

---

## Requisitos de documentación y sustentación

### Estado: No implementado

- [ ] Instrucciones de instalación y ejecución
- [ ] Arquitectura de la solución
- [ ] Documentación de servicios API
- [ ] Descripción de reglas de negocio
- [ ] Credenciales de acceso de prueba por rol (sin contraseñas reales)
- [ ] Documentación Swagger/OpenAPI
- [ ] Evidencia funcional de carga masiva
- [ ] Evidencia funcional de descuento de inventario por dispensación
- [ ] Evidencia funcional de gestión de citas por calendario
- [ ] Evidencia de generación de fórmulas y facturas
- [ ] Evidencia de generación de reportes PDF y Excel
- [ ] Guía de sustentación

---

## Priorización sugerida para trabajar por requerimientos

### Fase 1. Core mínimo funcional
1. Inventario básico y lotes
2. Fórmulas médicas con diferencia entre formulación y dispensación
3. Despacho y descuentos de inventario
4. Facturación mínima
5. Roles y permisos básicos

### Fase 2. Operación y control
6. Citas en calendario
7. Reportes de inventario y médicos
8. Exportación Excel/PDF

### Fase 3. Compleción y documentación
9. Swagger y documentación técnica
10. Evidencias funcionales
11. Sustentación

---

## Observación importante

La solución actual ya tiene componentes base de CRUD, pero no cumple el alcance integral del examen final. El trabajo real deberá enfocarse en consolidar los módulos de negocio y no solo en formularios aislados.
