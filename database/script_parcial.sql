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
    ADD COLUMN frecuencia VARCHAR(100) NULL AFTER dosis,
    ADD COLUMN duracion_dias INT NULL AFTER frecuencia,
    ADD COLUMN activo TINYINT(1) NOT NULL DEFAULT 1 AFTER indicaciones;

-- Las formulas que ya existian toman la mascota y el veterinario de su cita.
UPDATE formula_medica f
    INNER JOIN cita c ON c.id = f.cita_id
SET f.mascota_id = c.mascota_id,
    f.medico_id = c.medico_id,
    f.fecha_emision = GREATEST(DATE(f.fecha_creacion_registro), DATE(c.fecha_hora))
WHERE f.mascota_id IS NULL;
