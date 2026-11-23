-- =====================================================================
-- EXAMEN FINAL - Script de ajustes al modelo de datos (base: clinica)
-- Inventario, citas con calendario, atencion, formulas, facturacion,
-- despacho de medicamentos, usuarios/perfiles/menus.
-- Se ejecuta UNA vez sobre la base del segundo parcial.
-- =====================================================================
USE clinica;
SET NAMES utf8mb4;

-- ---------------------------------------------------------------------
-- 1. SEGURIDAD: perfiles, menus, usuarios y sesiones (base del docente)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS perfil (
  perfil_id          INT NOT NULL AUTO_INCREMENT,
  nombre             VARCHAR(50) NOT NULL,
  activo             TINYINT(1) NOT NULL,
  fecha_creacion     DATETIME NOT NULL,
  fecha_modificacion DATETIME DEFAULT NULL,
  PRIMARY KEY (perfil_id)
);
INSERT INTO perfil VALUES
 (1,'Administrador',1,'2026-01-01 00:00:00',NULL),
 (2,'Recepcion',1,'2026-01-01 00:00:00',NULL),
 (3,'Medico',1,'2026-01-01 00:00:00',NULL),
 (4,'DESPACHO_MEDICO',1,'2026-01-01 00:00:00',NULL);

CREATE TABLE IF NOT EXISTS menu (
  menu_id            INT NOT NULL AUTO_INCREMENT,
  code               VARCHAR(100) NOT NULL,
  title              VARCHAR(255) NOT NULL,
  url                LONGTEXT NOT NULL,
  icon               VARCHAR(255) NOT NULL,
  activo             TINYINT(1) NOT NULL,
  fecha_creacion     DATETIME NOT NULL,
  fecha_modificacion DATETIME DEFAULT NULL,
  PRIMARY KEY (menu_id)
);
INSERT INTO menu VALUES
 (1,'cita','Gestión de citas','/inicio/citas','bi bi-calendar',1,'2026-01-01 00:00:00',NULL),
 (2,'clientes','Gestión de clientes','/inicio/clientes','bi bi-person-check',1,'2026-01-01 00:00:00',NULL),
 (3,'especializaciones','Gestión de Especializaciones','/inicio/especializaciones','fa-solid fa-user-graduate',1,'2026-01-01 00:00:00',NULL),
 (4,'formulas','Gestión de Formulas','/inicio/formulas','fa-solid fa-clipboard-list',1,'2026-01-01 00:00:00',NULL),
 (5,'historias','Gestión de Historias','/inicio/historias','fa-solid fa-book-medical',1,'2026-01-01 00:00:00',NULL),
 (6,'mascotas','Gestión de Mascotas','/inicio/mascotas','fa-solid fa-paw',1,'2026-01-01 00:00:00',NULL),
 (7,'medicamentos','Gestión de Medicamentos','/inicio/medicamentos','fa-solid fa-capsules',1,'2026-01-01 00:00:00',NULL),
 (8,'medicos','Gestión de Medicos','/inicio/medicos','fa-solid fa-user-doctor',1,'2026-01-01 00:00:00',NULL),
 (9,'usuarios','Gestión de Usuarios','/inicio/usuarios','feather icon-users',1,'2026-01-01 00:00:00',NULL),
 (10,'agenda','Mi agenda','/inicio/agenda','feather icon-calendar',1,'2026-01-01 00:00:00',NULL),
 (11,'tarifas','Tarifas y servicios','/inicio/tarifas','fa-solid fa-tags',1,'2026-01-01 00:00:00',NULL),
 (12,'facturas','Facturación','/inicio/facturas','feather icon-file-text',1,'2026-01-01 00:00:00',NULL),
 (13,'reportes','Reportes','/inicio/reportes','feather icon-bar-chart-2',1,'2026-01-01 00:00:00',NULL),
 (14,'despacho','Despacho de medicamentos','/inicio/despacho','feather icon-package',1,'2026-01-01 00:00:00',NULL);

CREATE TABLE IF NOT EXISTS perfil_menu (
  perfil_menu_id INT NOT NULL AUTO_INCREMENT,
  perfil_id      INT NOT NULL,
  menu_id        INT NOT NULL,
  PRIMARY KEY (perfil_menu_id),
  UNIQUE KEY uk_perfil_menu (perfil_id, menu_id),
  CONSTRAINT fk_pm_perfil FOREIGN KEY (perfil_id) REFERENCES perfil (perfil_id),
  CONSTRAINT fk_pm_menu   FOREIGN KEY (menu_id)   REFERENCES menu (menu_id)
);
-- Administrador (menus del docente + nuevos modulos administrativos)
INSERT INTO perfil_menu (perfil_id, menu_id) VALUES
 (1,1),(1,3),(1,6),(1,7),(1,8),(1,9),(1,2),(1,11),(1,12),(1,13),(1,14);
-- Recepcion / secretaria
INSERT INTO perfil_menu (perfil_id, menu_id) VALUES (2,1),(2,2),(2,6),(2,12);
-- Medico: su agenda en calendario, historias y formulas
INSERT INTO perfil_menu (perfil_id, menu_id) VALUES (3,10),(3,5),(3,4);
-- Despacho medico: inventario (solo consulta) y despacho
INSERT INTO perfil_menu (perfil_id, menu_id) VALUES (4,7),(4,14);

CREATE TABLE IF NOT EXISTS otp_token (
  id               BIGINT NOT NULL AUTO_INCREMENT,
  usuario_id       BIGINT NOT NULL,
  codigo           VARCHAR(6) NOT NULL,
  fecha_creacion   DATETIME NOT NULL,
  fecha_expiracion DATETIME NOT NULL,
  intentos         INT NOT NULL,
  usado            TINYINT(1) NOT NULL,
  PRIMARY KEY (id)
);

-- usuario.rol pasa a ser el id del perfil (como en el script del docente)
UPDATE usuario SET rol = '1' WHERE rol NOT REGEXP '^[0-9]+$';
ALTER TABLE usuario MODIFY rol INT NOT NULL;
ALTER TABLE usuario ADD CONSTRAINT fk_usuario_perfil FOREIGN KEY (rol) REFERENCES perfil (perfil_id);

-- Usuarios de PRUEBA por rol (contrasena de prueba: Clinica2026*)
INSERT INTO usuario (username, password_hash, rol, activo, email) VALUES
 ('admin',      MD5('Clinica2026*'), 1, 1, 'admin@clinica.test'),
 ('recepcion',  MD5('Clinica2026*'), 2, 1, 'recepcion@clinica.test'),
 ('medico',     MD5('Clinica2026*'), 3, 1, 'medico@clinica.test'),
 ('medico2',    MD5('Clinica2026*'), 3, 1, 'medico2@clinica.test'),
 ('despacho',   MD5('Clinica2026*'), 4, 1, 'despacho@clinica.test');

-- ---------------------------------------------------------------------
-- 2. MEDICOS, ESPECIALIDADES, HORARIOS Y ESTADOS DE CITA
-- ---------------------------------------------------------------------
ALTER TABLE medico
  ADD COLUMN usuario_id BIGINT UNSIGNED NULL,
  ADD COLUMN activo TINYINT(1) NOT NULL DEFAULT 1,
  ADD UNIQUE KEY uk_medico_usuario (usuario_id),
  ADD CONSTRAINT fk_medico_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id);
UPDATE medico SET usuario_id = (SELECT id FROM usuario WHERE username = 'medico')  WHERE id = 3;
UPDATE medico SET usuario_id = (SELECT id FROM usuario WHERE username = 'medico2') WHERE id = 11;

ALTER TABLE especializacion ADD COLUMN activo TINYINT(1) NOT NULL DEFAULT 1;

-- Horario de disponibilidad del veterinario (dia_semana: 1 = lunes ... 7 = domingo)
CREATE TABLE IF NOT EXISTS horario_medico (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  medico_id   BIGINT UNSIGNED NOT NULL,
  dia_semana  TINYINT NOT NULL,
  hora_inicio TIME NOT NULL,
  hora_fin    TIME NOT NULL,
  activo      TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  CONSTRAINT fk_horario_medico FOREIGN KEY (medico_id) REFERENCES medico (id),
  CONSTRAINT ck_horario_dia CHECK (dia_semana BETWEEN 1 AND 7),
  CONSTRAINT ck_horario_horas CHECK (hora_inicio < hora_fin)
);
-- Todos los medicos atienden de lunes a sabado de 7:00 a 19:00
INSERT INTO horario_medico (medico_id, dia_semana, hora_inicio, hora_fin)
SELECT m.id, d.dia, '07:00:00', '19:00:00'
FROM medico m CROSS JOIN (SELECT 1 dia UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) d;

-- Catalogo de estados de la cita
CREATE TABLE IF NOT EXISTS estado_cita (
  codigo VARCHAR(20) NOT NULL,
  nombre VARCHAR(50) NOT NULL,
  color  VARCHAR(10) NOT NULL,
  PRIMARY KEY (codigo)
);
INSERT INTO estado_cita VALUES
 ('pendiente','Pendiente','#94a3b8'),
 ('confirmada','Confirmada','#3b82f6'),
 ('programada','Programada','#f59e0b'),
 ('atendida','Atendida / finalizada','#10b981'),
 ('cancelada','Cancelada','#ef4444');

ALTER TABLE cita ADD COLUMN fecha_hora_fin DATETIME NULL;
UPDATE cita SET fecha_hora_fin = DATE_ADD(fecha_hora, INTERVAL 30 MINUTE) WHERE fecha_hora_fin IS NULL;
ALTER TABLE cita ADD CONSTRAINT fk_cita_estado FOREIGN KEY (estado) REFERENCES estado_cita (codigo);

-- ---------------------------------------------------------------------
-- 3. TARIFAS (servicios que se cobran) Y SU LOG DE CAMBIOS
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tarifa (
  id                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  codigo             VARCHAR(20) NOT NULL,
  nombre             VARCHAR(150) NOT NULL,
  tipo               VARCHAR(20) NOT NULL,          -- CONSULTA, PROCEDIMIENTO, SERVICIO
  especializacion_id BIGINT UNSIGNED NULL,
  valor              DECIMAL(12,2) NOT NULL,
  iva_porcentaje     DECIMAL(5,2) NOT NULL DEFAULT 0,
  activo             TINYINT(1) NOT NULL DEFAULT 1,
  fecha_creacion     DATETIME NOT NULL,
  fecha_modificacion DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_tarifa_codigo (codigo),
  CONSTRAINT fk_tarifa_especializacion FOREIGN KEY (especializacion_id) REFERENCES especializacion (id),
  CONSTRAINT ck_tarifa_valor CHECK (valor >= 0),
  CONSTRAINT ck_tarifa_iva CHECK (iva_porcentaje BETWEEN 0 AND 100)
);
INSERT INTO tarifa (codigo, nombre, tipo, especializacion_id, valor, iva_porcentaje, fecha_creacion)
SELECT CONCAT('CONS-', LPAD(e.id, 3, '0')), CONCAT('Consulta de ', e.nombre), 'CONSULTA', e.id, 60000, 0, NOW()
FROM especializacion e;
INSERT INTO tarifa (codigo, nombre, tipo, especializacion_id, valor, iva_porcentaje, fecha_creacion) VALUES
 ('CONS-GEN', 'Consulta general', 'CONSULTA', NULL, 50000, 0, NOW()),
 ('PROC-VAC', 'Aplicación de vacuna', 'PROCEDIMIENTO', NULL, 25000, 19, NOW()),
 ('PROC-CUR', 'Curación de heridas', 'PROCEDIMIENTO', NULL, 35000, 19, NOW()),
 ('SERV-BAN', 'Baño medicado', 'SERVICIO', NULL, 40000, 19, NOW());

CREATE TABLE IF NOT EXISTS tarifa_log (
  id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  tarifa_id      BIGINT UNSIGNED NOT NULL,
  accion         VARCHAR(20) NOT NULL,           -- CREACION, ACTUALIZACION, ACTIVACION, INACTIVACION
  valor_anterior DECIMAL(12,2) NULL,
  valor_nuevo    DECIMAL(12,2) NULL,
  iva_anterior   DECIMAL(5,2) NULL,
  iva_nuevo      DECIMAL(5,2) NULL,
  usuario_id     BIGINT UNSIGNED NULL,
  fecha          DATETIME NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_tlog_tarifa FOREIGN KEY (tarifa_id) REFERENCES tarifa (id),
  CONSTRAINT fk_tlog_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

-- ---------------------------------------------------------------------
-- 4. INVENTARIO: medicamento/producto, lotes, movimientos y log de cargue
-- ---------------------------------------------------------------------
ALTER TABLE medicamento
  ADD COLUMN codigo CHAR(8) NULL,
  ADD COLUMN tipo VARCHAR(20) NOT NULL DEFAULT 'MEDICAMENTO',   -- MEDICAMENTO o UTENSILIO
  ADD COLUMN registro_invima VARCHAR(50) NULL,
  ADD COLUMN precio_venta DECIMAL(12,2) NOT NULL DEFAULT 0,
  ADD COLUMN iva_porcentaje DECIMAL(5,2) NOT NULL DEFAULT 0,
  ADD COLUMN stock_minimo INT NOT NULL DEFAULT 0,
  ADD COLUMN fecha_proximo_pedido DATE NULL,
  ADD COLUMN activo TINYINT(1) NOT NULL DEFAULT 1,
  MODIFY fecha_compra DATE NULL,
  MODIFY fecha_vence DATE NULL;
UPDATE medicamento SET codigo = CONCAT('MED', LPAD(id, 5, '0')),
                       registro_invima = CONCAT('INVIMA2020M-', LPAD(id, 6, '0')),
                       precio_venta = 8000, stock_minimo = 10
 WHERE codigo IS NULL;
ALTER TABLE medicamento
  MODIFY codigo CHAR(8) NOT NULL,
  ADD UNIQUE KEY uk_medicamento_codigo (codigo),
  ADD CONSTRAINT ck_medicamento_precio CHECK (precio_venta >= 0),
  ADD CONSTRAINT ck_medicamento_stock CHECK (stock_minimo >= 0);

CREATE TABLE IF NOT EXISTS medicamento_lote (
  id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  medicamento_id      BIGINT UNSIGNED NOT NULL,
  numero_lote         VARCHAR(50) NOT NULL,
  fecha_compra        DATE NOT NULL,
  fecha_ingreso       DATETIME NOT NULL,
  fecha_vence         DATE NULL,
  precio_compra       DECIMAL(12,2) NOT NULL,
  cantidad_inicial    INT NOT NULL,
  cantidad_disponible INT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_lote (medicamento_id, numero_lote),
  CONSTRAINT fk_lote_medicamento FOREIGN KEY (medicamento_id) REFERENCES medicamento (id),
  CONSTRAINT ck_lote_cantidad CHECK (cantidad_disponible >= 0 AND cantidad_disponible <= cantidad_inicial),
  CONSTRAINT ck_lote_precio CHECK (precio_compra >= 0)
);

CREATE TABLE IF NOT EXISTS carga_inventario_log (
  id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  usuario_id     BIGINT UNSIGNED NULL,
  fecha          DATETIME NOT NULL,
  nombre_archivo VARCHAR(255) NOT NULL,
  total_filas    INT NOT NULL,
  filas_ok       INT NOT NULL,
  filas_error    INT NOT NULL,
  detalle        LONGTEXT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_carga_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

-- ---------------------------------------------------------------------
-- 5. FORMULA (cabecera + detalle), DESPACHO Y MOVIMIENTOS
-- ---------------------------------------------------------------------
ALTER TABLE formula_medica
  MODIFY medicamento_id INT NULL,
  MODIFY dosis TEXT NULL,
  ADD COLUMN historia_id BIGINT UNSIGNED NULL,
  ADD COLUMN estado_despacho VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE_DESPACHO';

CREATE TABLE IF NOT EXISTS formula_detalle (
  id                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  formula_id         BIGINT UNSIGNED NOT NULL,
  medicamento_id     BIGINT UNSIGNED NOT NULL,
  dosis              VARCHAR(200) NOT NULL,
  frecuencia         VARCHAR(100) NOT NULL,
  duracion_dias      INT NOT NULL,
  cantidad           INT NOT NULL,
  via_administracion VARCHAR(50) NOT NULL,
  indicaciones       TEXT NULL,
  cantidad_entregada INT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT fk_fdet_formula FOREIGN KEY (formula_id) REFERENCES formula_medica (id),
  CONSTRAINT fk_fdet_medicamento FOREIGN KEY (medicamento_id) REFERENCES medicamento (id),
  CONSTRAINT ck_fdet_cantidad CHECK (cantidad > 0 AND cantidad_entregada >= 0 AND cantidad_entregada <= cantidad)
);
-- Las formulas del segundo parcial (un medicamento por fila) pasan a ser detalle
INSERT INTO formula_detalle (formula_id, medicamento_id, dosis, frecuencia, duracion_dias, cantidad, via_administracion, indicaciones)
SELECT f.id, f.medicamento_id, IFNULL(f.dosis,'-'), IFNULL(f.frecuencia,'-'), IFNULL(f.duracion_dias,1), 1, 'Oral', f.indicaciones
FROM formula_medica f WHERE f.medicamento_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS despacho (
  id                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  formula_id           BIGINT UNSIGNED NOT NULL,
  estado               VARCHAR(30) NOT NULL,          -- CONFIRMADO o REVERTIDO
  usuario_id           BIGINT UNSIGNED NOT NULL,
  fecha                DATETIME NOT NULL,
  observacion          TEXT NULL,
  usuario_reversion_id BIGINT UNSIGNED NULL,
  fecha_reversion      DATETIME NULL,
  motivo_reversion     TEXT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_desp_formula FOREIGN KEY (formula_id) REFERENCES formula_medica (id),
  CONSTRAINT fk_desp_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
  CONSTRAINT fk_desp_usuario_rev FOREIGN KEY (usuario_reversion_id) REFERENCES usuario (id)
);

CREATE TABLE IF NOT EXISTS despacho_detalle (
  id                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  despacho_id        BIGINT UNSIGNED NOT NULL,
  formula_detalle_id BIGINT UNSIGNED NOT NULL,
  lote_id            BIGINT UNSIGNED NOT NULL,
  cantidad           INT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_ddet_despacho FOREIGN KEY (despacho_id) REFERENCES despacho (id),
  CONSTRAINT fk_ddet_fdetalle FOREIGN KEY (formula_detalle_id) REFERENCES formula_detalle (id),
  CONSTRAINT fk_ddet_lote FOREIGN KEY (lote_id) REFERENCES medicamento_lote (id),
  CONSTRAINT ck_ddet_cantidad CHECK (cantidad > 0)
);

CREATE TABLE IF NOT EXISTS despacho_estado_log (
  id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  formula_id      BIGINT UNSIGNED NOT NULL,
  estado_anterior VARCHAR(30) NULL,
  estado_nuevo    VARCHAR(30) NOT NULL,
  usuario_id      BIGINT UNSIGNED NOT NULL,
  fecha           DATETIME NOT NULL,
  observacion     TEXT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_dlog_formula FOREIGN KEY (formula_id) REFERENCES formula_medica (id),
  CONSTRAINT fk_dlog_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

CREATE TABLE IF NOT EXISTS movimiento_inventario (
  id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  medicamento_id BIGINT UNSIGNED NOT NULL,
  lote_id        BIGINT UNSIGNED NULL,
  tipo           VARCHAR(20) NOT NULL,          -- ENTRADA, CARGA_MASIVA, SALIDA, REVERSION
  cantidad       INT NOT NULL,
  saldo_lote     INT NOT NULL,
  fecha          DATETIME NOT NULL,
  usuario_id     BIGINT UNSIGNED NULL,
  formula_id     BIGINT UNSIGNED NULL,
  despacho_id    BIGINT UNSIGNED NULL,
  observacion    VARCHAR(255) NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_mov_medicamento FOREIGN KEY (medicamento_id) REFERENCES medicamento (id),
  CONSTRAINT fk_mov_lote FOREIGN KEY (lote_id) REFERENCES medicamento_lote (id),
  CONSTRAINT fk_mov_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
  CONSTRAINT fk_mov_formula FOREIGN KEY (formula_id) REFERENCES formula_medica (id),
  CONSTRAINT fk_mov_despacho FOREIGN KEY (despacho_id) REFERENCES despacho (id),
  CONSTRAINT ck_mov_cantidad CHECK (cantidad > 0)
);

-- Las formulas viejas ya se entregaron (no deben aparecer como pendientes)
UPDATE formula_medica SET estado_despacho = 'DESPACHADO' WHERE id IN (SELECT formula_id FROM formula_detalle);
UPDATE formula_detalle SET cantidad_entregada = cantidad;

-- ---------------------------------------------------------------------
-- 6. ATENCION MEDICA: campos nuevos de la historia clinica
-- ---------------------------------------------------------------------
ALTER TABLE historia_medica
  ADD COLUMN sintomas TEXT NULL,
  ADD COLUMN recomendaciones TEXT NULL,
  ADD COLUMN tarifa_id BIGINT UNSIGNED NULL,
  ADD CONSTRAINT fk_historia_tarifa FOREIGN KEY (tarifa_id) REFERENCES tarifa (id);

-- ---------------------------------------------------------------------
-- 7. FACTURACION: factura, detalle, pagos y log
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS factura (
  id                    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  numero                VARCHAR(20) NOT NULL,
  cita_id               BIGINT UNSIGNED NOT NULL,
  cliente_id            BIGINT UNSIGNED NOT NULL,
  mascota_id            INT NOT NULL,
  medico_id             BIGINT UNSIGNED NOT NULL,
  especialidad          VARCHAR(100) NULL,
  fecha                 DATETIME NOT NULL,
  subtotal              DECIMAL(12,2) NOT NULL,
  iva                   DECIMAL(12,2) NOT NULL,
  descuento             DECIMAL(12,2) NOT NULL DEFAULT 0,
  total                 DECIMAL(12,2) NOT NULL,
  total_pagado          DECIMAL(12,2) NOT NULL DEFAULT 0,
  estado                VARCHAR(20) NOT NULL,          -- PENDIENTE, PAGO_PARCIAL, PAGADA, ANULADA
  usuario_id            BIGINT UNSIGNED NOT NULL,
  motivo_anulacion      TEXT NULL,
  fecha_anulacion       DATETIME NULL,
  usuario_anulacion_id  BIGINT UNSIGNED NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_factura_numero (numero),
  CONSTRAINT fk_fact_cita FOREIGN KEY (cita_id) REFERENCES cita (id),
  CONSTRAINT fk_fact_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (cliente_id),
  CONSTRAINT fk_fact_mascota FOREIGN KEY (mascota_id) REFERENCES mascota (mascota_id),
  CONSTRAINT fk_fact_medico FOREIGN KEY (medico_id) REFERENCES medico (id),
  CONSTRAINT fk_fact_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
  CONSTRAINT ck_fact_valores CHECK (subtotal >= 0 AND iva >= 0 AND descuento >= 0 AND total >= 0 AND total_pagado >= 0)
);

CREATE TABLE IF NOT EXISTS factura_detalle (
  id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  factura_id      BIGINT UNSIGNED NOT NULL,
  tipo            VARCHAR(20) NOT NULL,           -- SERVICIO o MEDICAMENTO
  referencia_id   BIGINT UNSIGNED NOT NULL,       -- tarifa.id o medicamento.id
  descripcion     VARCHAR(200) NOT NULL,
  cantidad        INT NOT NULL,
  precio_unitario DECIMAL(12,2) NOT NULL,
  iva_porcentaje  DECIMAL(5,2) NOT NULL,
  subtotal        DECIMAL(12,2) NOT NULL,
  iva_valor       DECIMAL(12,2) NOT NULL,
  total           DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_fdet_factura FOREIGN KEY (factura_id) REFERENCES factura (id)
);

CREATE TABLE IF NOT EXISTS pago (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  factura_id BIGINT UNSIGNED NOT NULL,
  valor      DECIMAL(12,2) NOT NULL,
  metodo     VARCHAR(30) NOT NULL,               -- EFECTIVO, TARJETA, TRANSFERENCIA
  referencia VARCHAR(100) NULL,
  fecha      DATETIME NOT NULL,
  usuario_id BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_pago_factura FOREIGN KEY (factura_id) REFERENCES factura (id),
  CONSTRAINT fk_pago_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
  CONSTRAINT ck_pago_valor CHECK (valor > 0)
);

CREATE TABLE IF NOT EXISTS factura_log (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  factura_id BIGINT UNSIGNED NOT NULL,
  accion     VARCHAR(30) NOT NULL,               -- GENERADA, PAGO, ANULADA
  detalle    TEXT NULL,
  usuario_id BIGINT UNSIGNED NOT NULL,
  fecha      DATETIME NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_flog_factura FOREIGN KEY (factura_id) REFERENCES factura (id),
  CONSTRAINT fk_flog_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);
