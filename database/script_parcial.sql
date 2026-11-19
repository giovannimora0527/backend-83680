-- =====================================================================
-- Script de cambios para el segundo parcial de Programación Web.
-- Se ejecuta sobre la base de datos del proyecto (Veterinaria) despues
-- de cargar el respaldo base entregado por el docente.
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. Citas
-- Los estados se manejan en mayuscula: PENDIENTE, PROGRAMADA,
-- CONFIRMADA, ATENDIDA y CANCELADA.
-- ---------------------------------------------------------------------
UPDATE cita SET estado = UPPER(estado);

-- Indices para validar rapido el horario del veterinario y de la mascota.
CREATE INDEX idx_cita_medico_fecha ON cita (medico_id, fecha_hora);
CREATE INDEX idx_cita_mascota_fecha ON cita (mascota_id, fecha_hora);

-- Campos para hora de fin y seguimiento de cambios en citas (Requisito 2).
ALTER TABLE cita
    ADD COLUMN fecha_hora_fin DATETIME NULL AFTER fecha_hora,
    ADD COLUMN fecha_actualizacion DATETIME NULL AFTER motivo,
    ADD COLUMN historial_cambios TEXT NULL AFTER fecha_actualizacion;

UPDATE cita
SET fecha_hora_fin = DATE_ADD(fecha_hora, INTERVAL 30 MINUTE)
WHERE fecha_hora_fin IS NULL;

-- Tabla de bloqueos de horario por veterinario (Requisito 2.3).
CREATE TABLE IF NOT EXISTS bloqueo_horario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medico_id BIGINT NOT NULL,
    fecha_hora_inicio DATETIME NOT NULL,
    fecha_hora_fin DATETIME NOT NULL,
    motivo VARCHAR(250) NOT NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_bloqueo_medico_fechas (medico_id, fecha_hora_inicio, fecha_hora_fin)
);

-- ---------------------------------------------------------------------
-- 2. Historias clinicas
-- Cada historia corresponde a una consulta (cita atendida) de una mascota.
-- paciente_id sigue siendo el identificador de la mascota.
-- ---------------------------------------------------------------------
ALTER TABLE historia_medica
    ADD COLUMN cliente_id INT NULL AFTER paciente_id,
    ADD COLUMN cita_id INT NULL AFTER cliente_id,
    ADD COLUMN medico_id INT NULL AFTER cita_id,
    ADD COLUMN fecha_hora DATETIME NULL AFTER medico_id,
    ADD COLUMN motivo TEXT NULL AFTER fecha_hora,
    ADD COLUMN diagnostico TEXT NULL AFTER motivo,
    ADD COLUMN observaciones TEXT NULL AFTER diagnostico,
    ADD COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'ABIERTA' AFTER observaciones;

-- Una cita solo puede generar una historia clinica.
ALTER TABLE historia_medica ADD UNIQUE KEY uk_historia_cita (cita_id);

-- ---------------------------------------------------------------------
-- 3. Anotaciones de la historia clinica
-- ---------------------------------------------------------------------
ALTER TABLE anotacion_historia
    ADD COLUMN observacion TEXT NULL AFTER descripcion,
    ADD COLUMN diagnostico TEXT NULL AFTER observacion;

-- ---------------------------------------------------------------------
-- 4. Formulas medicas
-- Se agregan mascota, veterinario, fecha de emision, frecuencia,
-- duracion del tratamiento y el campo activo para inactivar en lugar
-- de eliminar.
-- ---------------------------------------------------------------------
ALTER TABLE formula_medica
    ADD COLUMN mascota_id INT NULL AFTER cita_id,
    ADD COLUMN medico_id INT NULL AFTER mascota_id,
    ADD COLUMN fecha_emision DATE NULL AFTER medicamento_id,
    ADD COLUMN cantidad_solicitada INT NOT NULL DEFAULT 1 AFTER fecha_emision,
    ADD COLUMN cantidad_entregada INT NOT NULL DEFAULT 0 AFTER cantidad_solicitada,
    ADD COLUMN estado VARCHAR(30) NOT NULL DEFAULT 'FORMULADA' AFTER cantidad_entregada,
    ADD COLUMN frecuencia VARCHAR(100) NULL AFTER dosis,
    ADD COLUMN duracion_dias INT NULL AFTER frecuencia,
    ADD COLUMN activo TINYINT(1) NOT NULL DEFAULT 1 AFTER indicaciones,
    ADD COLUMN fecha_dispensacion DATETIME NULL AFTER activo;

-- Las formulas que ya existian toman la mascota y el veterinario de su cita.
UPDATE formula_medica f
    INNER JOIN cita c ON c.id = f.cita_id
SET f.mascota_id = c.mascota_id,
    f.medico_id = c.medico_id,
    f.fecha_emision = GREATEST(DATE(f.fecha_creacion_registro), DATE(c.fecha_hora))
WHERE f.mascota_id IS NULL;

-- Tabla de movimientos de inventario (Requisito 1.3 y 3.1).
CREATE TABLE IF NOT EXISTS movimiento_medicamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medicamento_id BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    cantidad INT NOT NULL,
    motivo VARCHAR(200) NULL,
    referencia_tipo VARCHAR(50) NULL,
    referencia_id BIGINT NULL,
    usuario_registro VARCHAR(100) NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacion TEXT NULL
);

-- ---------------------------------------------------------------------
-- 5. Facturacion y Pagos (Requisito 3.2 y 3.3)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS factura (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_factura VARCHAR(30) NOT NULL UNIQUE,
    cita_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    mascota_id INT NOT NULL,
    medico_id BIGINT NOT NULL,
    fecha_generacion DATETIME NOT NULL,
    subtotal_servicios DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    subtotal_medicamentos DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    porcentaje_descuento DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    valor_descuento DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    porcentaje_impuesto DECIMAL(5,2) NOT NULL DEFAULT 19.00,
    valor_impuesto DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_pagado DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    saldo_pendiente DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    observaciones TEXT NULL,
    motivo_anulacion TEXT NULL,
    fecha_anulacion DATETIME NULL,
    historial_operaciones TEXT NULL,
    INDEX idx_factura_cita (cita_id),
    INDEX idx_factura_cliente (cliente_id),
    INDEX idx_factura_mascota (mascota_id),
    INDEX idx_factura_fecha (fecha_generacion)
);

CREATE TABLE IF NOT EXISTS detalle_factura (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    factura_id BIGINT NOT NULL,
    tipo_item VARCHAR(20) NOT NULL,
    codigo_servicio VARCHAR(60) NULL,
    formula_medica_id BIGINT NULL,
    medicamento_id BIGINT NULL,
    concepto VARCHAR(250) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario_historico DECIMAL(12,2) NOT NULL,
    descuento_item DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    subtotal_item DECIMAL(12,2) NOT NULL,
    INDEX idx_detalle_factura (factura_id)
);

CREATE TABLE IF NOT EXISTS pago_factura (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    factura_id BIGINT NOT NULL,
    fecha_pago DATETIME NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    metodo_pago VARCHAR(30) NOT NULL,
    referencia_pago VARCHAR(100) NULL,
    usuario_registro VARCHAR(100) NULL,
    observacion VARCHAR(300) NULL,
    INDEX idx_pago_factura (factura_id)
);
