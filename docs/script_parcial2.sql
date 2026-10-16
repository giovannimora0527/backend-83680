-- =====================================================================
-- Script de cambios para el segundo parcial (citas, historias y formulas)
-- Base de datos: clinica
-- Solo AGREGA columnas, no borra nada.
-- =====================================================================
USE clinica;

-- ---------------------------------------------------------------------
-- 1. Historia medica
-- paciente_id ahora se usa como el id de la MASCOTA.
-- Se agregan la cita, el veterinario y los datos de la consulta.
-- ---------------------------------------------------------------------
ALTER TABLE historia_medica
    ADD COLUMN cita_id INT NULL,
    ADD COLUMN medico_id INT NULL,
    ADD COLUMN fecha_hora DATETIME NULL,
    ADD COLUMN motivo TEXT NULL,
    ADD COLUMN diagnostico TEXT NULL,
    ADD COLUMN observaciones TEXT NULL;

-- ---------------------------------------------------------------------
-- 2. Anotaciones de la historia
-- ---------------------------------------------------------------------
ALTER TABLE anotacion_historia
    ADD COLUMN observacion TEXT NULL,
    ADD COLUMN diagnostico TEXT NULL;

-- ---------------------------------------------------------------------
-- 3. Formula medica
-- activo = 1 -> vigente, activo = 0 -> inactiva (no se borra fisicamente)
-- ---------------------------------------------------------------------
ALTER TABLE formula_medica
    ADD COLUMN medico_id INT NULL,
    ADD COLUMN frecuencia VARCHAR(100) NULL,
    ADD COLUMN duracion_dias INT NULL,
    ADD COLUMN fecha_emision DATE NULL,
    ADD COLUMN activo TINYINT(1) NOT NULL DEFAULT 1;

-- Llenar los datos de las formulas viejas para que no queden vacias
UPDATE formula_medica f
    JOIN cita c ON c.id = f.cita_id
SET f.medico_id     = c.medico_id,
    f.fecha_emision = DATE(f.fecha_creacion_registro),
    f.frecuencia    = 'Ver dosis',
    f.duracion_dias = 7
WHERE f.medico_id IS NULL;

-- ---------------------------------------------------------------------
-- 4. Citas: el estado "completada" pasa a llamarse "atendida"
-- Estados validos: pendiente, confirmada, programada, atendida, cancelada
-- ---------------------------------------------------------------------
UPDATE cita SET estado = 'atendida' WHERE estado = 'completada';

-- La historia que ya existia queda ligada a la mascota 1 (paciente_id = 1)
UPDATE historia_medica h
    JOIN mascota m ON m.mascota_id = h.paciente_id
SET h.fecha_hora = h.fecha_creacion,
    h.motivo     = 'Consulta general'
WHERE h.fecha_hora IS NULL;
