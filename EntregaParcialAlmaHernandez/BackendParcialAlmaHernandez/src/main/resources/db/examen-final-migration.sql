CREATE DATABASE IF NOT EXISTS clinica
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE clinica;

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS perfil (
  perfil_id INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(50) NOT NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion DATETIME NULL,
  PRIMARY KEY (perfil_id),
  UNIQUE KEY uq_perfil_nombre (nombre)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS menu (
  menu_id INT NOT NULL AUTO_INCREMENT,
  code VARCHAR(100) NOT NULL,
  title VARCHAR(255) NOT NULL,
  url LONGTEXT NOT NULL,
  icon VARCHAR(255) NOT NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion DATETIME NULL,
  PRIMARY KEY (menu_id),
  UNIQUE KEY uq_menu_code (code)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS perfil_menu (
  perfil_menu_id INT NOT NULL AUTO_INCREMENT,
  perfil_id INT NOT NULL,
  menu_id INT NOT NULL,
  PRIMARY KEY (perfil_menu_id),
  UNIQUE KEY uq_perfil_menu (perfil_id, menu_id),
  CONSTRAINT fk_perfil_menu_perfil FOREIGN KEY (perfil_id) REFERENCES perfil(perfil_id),
  CONSTRAINT fk_perfil_menu_menu FOREIGN KEY (menu_id) REFERENCES menu(menu_id)
) ENGINE=InnoDB;

INSERT INTO perfil (nombre, activo, fecha_creacion) VALUES
('Administrador',1,NOW()),('Recepcion',1,NOW()),('Medico',1,NOW()),
('DESPACHO_MEDICO',1,NOW())
ON DUPLICATE KEY UPDATE activo=1, fecha_modificacion=NOW();

INSERT INTO menu (code,title,url,icon,activo,fecha_creacion) VALUES
('cita','Gestión de citas','/inicio/citas','bi bi-calendar',1,NOW()),
('clientes','Gestión de clientes','/inicio/clientes','bi bi-person-check',1,NOW()),
('especializaciones','Gestión de Especializaciones','/inicio/especializaciones','fa-solid fa-user-graduate',1,NOW()),
('formulas','Gestión de Fórmulas','/inicio/formulas-medicas','fa-solid fa-clipboard-list',1,NOW()),
('historias','Gestión de Historias','/inicio/historias-medicas','fa-solid fa-book-medical',1,NOW()),
('mascotas','Gestión de Mascotas','/inicio/mascotas','fa-solid fa-paw',1,NOW()),
('medicamentos','Gestión de Medicamentos','/inicio/medicamentos','fa-solid fa-capsules',1,NOW()),
('medicos','Gestión de Médicos','/inicio/medicos','fa-solid fa-user-doctor',1,NOW()),
('usuarios','Gestión de Usuarios','/inicio/usuarios','feather icon-users',1,NOW()),
('despacho','Despacho de medicamentos','/inicio/despacho','feather icon-package',1,NOW()),
('reportes','Reportes administrativos','/inicio/reportes','feather icon-bar-chart-2',1,NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title),url=VALUES(url),activo=1,fecha_modificacion=NOW();

DELETE pm FROM perfil_menu pm
JOIN perfil p ON p.perfil_id=pm.perfil_id
WHERE p.nombre='DESPACHO_MEDICO';

INSERT INTO perfil_menu (perfil_id,menu_id)
SELECT p.perfil_id,m.menu_id
FROM perfil p JOIN menu m
WHERE p.nombre='DESPACHO_MEDICO' AND m.code IN ('medicamentos','despacho');

INSERT INTO perfil_menu (perfil_id,menu_id)
SELECT p.perfil_id,m.menu_id
FROM perfil p JOIN menu m
WHERE p.nombre='Medico' AND m.code IN ('cita','formulas','historias','mascotas')
ON DUPLICATE KEY UPDATE perfil_id=VALUES(perfil_id);

INSERT INTO perfil_menu (perfil_id,menu_id)
SELECT p.perfil_id,m.menu_id
FROM perfil p JOIN menu m
WHERE p.nombre='Recepcion' AND m.code IN ('cita','clientes','mascotas')
ON DUPLICATE KEY UPDATE perfil_id=VALUES(perfil_id);

CREATE TABLE IF NOT EXISTS especializacion (
  id INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT NULL,
  codigo_especializacion VARCHAR(50) NULL,
  activa TINYINT(1) NOT NULL DEFAULT 1,
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion DATETIME NULL,
  PRIMARY KEY(id),
  UNIQUE KEY uq_especializacion_nombre(nombre)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS especialidad (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT NULL,
  activa TINYINT(1) NOT NULL DEFAULT 1,
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion DATETIME NULL,
  PRIMARY KEY(id),
  UNIQUE KEY uq_especialidad_nombre(nombre)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cliente (
  cliente_id BIGINT NOT NULL AUTO_INCREMENT,
  usuario_id BIGINT NULL,
  tipo_documento VARCHAR(20) NULL,
  numero_documento VARCHAR(30) NULL,
  nombres VARCHAR(100) NULL,
  apellidos VARCHAR(100) NULL,
  fecha_nacimiento DATE NULL,
  genero VARCHAR(20) NULL,
  telefono VARCHAR(30) NULL,
  direccion VARCHAR(255) NULL,
  PRIMARY KEY(cliente_id),
  UNIQUE KEY uq_cliente_documento(numero_documento)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS mascota (
  mascota_id INT NOT NULL AUTO_INCREMENT,
  cliente_id BIGINT NULL,
  propietario_id BIGINT NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  especie VARCHAR(50) NULL,
  raza VARCHAR(100) NULL,
  sexo VARCHAR(20) NULL,
  fecha_nacimiento DATE NULL,
  activa TINYINT(1) NOT NULL DEFAULT 1,
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(mascota_id),
  UNIQUE KEY uq_mascota_propietario_nombre(cliente_id,nombre),
  INDEX idx_mascota_propietario(propietario_id),
  CONSTRAINT fk_mascota_propietario FOREIGN KEY(cliente_id) REFERENCES cliente(cliente_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS medico (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tipo_documento VARCHAR(20) NULL,
  numero_documento VARCHAR(30) NULL,
  nombres VARCHAR(100) NOT NULL,
  apellidos VARCHAR(100) NOT NULL,
  telefono VARCHAR(30) NULL,
  registro_profesional VARCHAR(100) NULL,
  especializacion_id INT NULL,
  especialidad_id BIGINT UNSIGNED NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  usuario_id BIGINT NULL,
  PRIMARY KEY(id),
  UNIQUE KEY uq_medico_documento(numero_documento),
  CONSTRAINT fk_medico_especializacion FOREIGN KEY(especializacion_id) REFERENCES especializacion(id),
  CONSTRAINT fk_medico_especialidad FOREIGN KEY(especialidad_id) REFERENCES especialidad(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS disponibilidad_medico (
  id BIGINT NOT NULL AUTO_INCREMENT,
  medico_id BIGINT NOT NULL,
  dia_semana TINYINT NOT NULL,
  hora_inicio TIME NOT NULL,
  hora_fin TIME NOT NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY(id),
  UNIQUE KEY uq_disponibilidad_medico(medico_id,dia_semana,hora_inicio,hora_fin),
  INDEX idx_disponibilidad_medico(medico_id,dia_semana),
  CONSTRAINT fk_disponibilidad_medico FOREIGN KEY(medico_id) REFERENCES medico(id),
  CONSTRAINT ck_disponibilidad_horario CHECK(hora_fin>hora_inicio)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS medicamento (
  id BIGINT NOT NULL AUTO_INCREMENT,
  codigo VARCHAR(8) NULL,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT NULL,
  presentacion VARCHAR(100) NULL,
  tipo_producto VARCHAR(20) NOT NULL DEFAULT 'MEDICAMENTO',
  precio_compra DECIMAL(12,2) NULL,
  precio_venta DECIMAL(12,2) NULL,
  cantidad_actual DECIMAL(12,3) NOT NULL DEFAULT 0,
  stock_minimo DECIMAL(12,3) NOT NULL DEFAULT 0,
  fecha_compra DATE NULL,
  fecha_vence DATE NULL,
  fecha_ingreso DATE NULL,
  fecha_proximo_pedido DATE NULL,
  registro_invima VARCHAR(100) NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  fecha_creacion_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion_registro DATETIME NULL,
  PRIMARY KEY(id),
  UNIQUE KEY uq_medicamento_nombre(nombre),
  UNIQUE KEY uq_medicamento_codigo(codigo),
  CONSTRAINT ck_medicamento_tipo CHECK(tipo_producto IN ('MEDICAMENTO','UTENSILIO')),
  CONSTRAINT ck_medicamento_precios CHECK(
    (precio_compra IS NULL OR precio_compra>=0) AND
    (precio_venta IS NULL OR precio_venta>=0)),
  CONSTRAINT ck_medicamento_stock CHECK(cantidad_actual>=0 AND stock_minimo>=0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS lote_medicamento (
  id BIGINT NOT NULL AUTO_INCREMENT,
  medicamento_id BIGINT NOT NULL,
  codigo_lote VARCHAR(100) NOT NULL,
  cantidad_actual DECIMAL(12,3) NOT NULL DEFAULT 0,
  precio_adquisicion DECIMAL(12,2) NOT NULL,
  fecha_compra DATE NOT NULL,
  fecha_vencimiento DATE NOT NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  UNIQUE KEY uq_lote_medicamento(medicamento_id,codigo_lote),
  INDEX idx_lote_vencimiento(fecha_vencimiento,activo),
  CONSTRAINT fk_lote_medicamento FOREIGN KEY(medicamento_id) REFERENCES medicamento(id),
  CONSTRAINT ck_lote_cantidad CHECK(cantidad_actual>=0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cita (
  id BIGINT NOT NULL AUTO_INCREMENT,
  cliente_id BIGINT NULL,
  propietario_id BIGINT NULL,
  mascota_id INT NOT NULL,
  medico_id BIGINT NOT NULL,
  especialidad_id BIGINT UNSIGNED NULL,
  fecha_hora DATETIME NOT NULL,
  fecha_fin DATETIME NOT NULL,
  estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
  motivo TEXT NULL,
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion DATETIME NULL,
  PRIMARY KEY(id),
  INDEX idx_cita_medico_horario(medico_id,fecha_hora,fecha_fin),
  INDEX idx_cita_mascota_horario(mascota_id,fecha_hora,fecha_fin),
  INDEX idx_cita_estado(estado),
  CONSTRAINT fk_cita_propietario FOREIGN KEY(propietario_id) REFERENCES cliente(cliente_id),
  CONSTRAINT fk_cita_mascota FOREIGN KEY(mascota_id) REFERENCES mascota(mascota_id),
  CONSTRAINT fk_cita_medico FOREIGN KEY(medico_id) REFERENCES medico(id),
  CONSTRAINT fk_cita_especialidad FOREIGN KEY(especialidad_id) REFERENCES especialidad(id),
  CONSTRAINT ck_cita_horario CHECK(fecha_fin>fecha_hora)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS historia_medica (
  id BIGINT NOT NULL AUTO_INCREMENT,
  cita_id BIGINT NULL,
  paciente_id BIGINT NULL,
  mascota_id INT NOT NULL,
  propietario_id BIGINT NULL,
  motivo_consulta TEXT NULL,
  sintomas TEXT NULL,
  diagnostico TEXT NULL,
  observaciones TEXT NULL,
  recomendaciones TEXT NULL,
  fecha_atencion DATETIME NULL,
  medico_id BIGINT NULL,
  estado ENUM('ABIERTA','FINALIZADA','ANULADA') NOT NULL DEFAULT 'ABIERTA',
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  INDEX idx_historia_mascota(fecha_creacion,mascota_id),
  CONSTRAINT fk_historia_cita FOREIGN KEY(cita_id) REFERENCES cita(id),
  CONSTRAINT fk_historia_paciente FOREIGN KEY(paciente_id) REFERENCES cliente(cliente_id),
  CONSTRAINT fk_historia_mascota FOREIGN KEY(mascota_id) REFERENCES mascota(mascota_id),
  CONSTRAINT fk_historia_medico FOREIGN KEY(medico_id) REFERENCES medico(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS anotacion_historia (
  id BIGINT NOT NULL AUTO_INCREMENT,
  historia_id BIGINT NOT NULL,
  medico_id BIGINT NULL,
  fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  descripcion TEXT NOT NULL,
  PRIMARY KEY(id),
  INDEX idx_anotacion_historia(historia_id,fecha),
  CONSTRAINT fk_anotacion_historia FOREIGN KEY(historia_id) REFERENCES historia_medica(id),
  CONSTRAINT fk_anotacion_medico FOREIGN KEY(medico_id) REFERENCES medico(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS formula_medica_legacy (
  id BIGINT NOT NULL AUTO_INCREMENT,
  cita_id BIGINT NOT NULL,
  medicamento_id BIGINT NOT NULL,
  dosis TEXT NOT NULL,
  indicaciones TEXT NULL,
  cantidad_prescrita DECIMAL(12,3) NOT NULL DEFAULT 1,
  estado_despacho VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE_DESPACHO',
  fecha_creacion_registro DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  CONSTRAINT fk_formula_legacy_cita FOREIGN KEY(cita_id) REFERENCES cita(id),
  CONSTRAINT fk_formula_legacy_medicamento FOREIGN KEY(medicamento_id) REFERENCES medicamento(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS formula_medica (
  id BIGINT NOT NULL AUTO_INCREMENT,
  cita_id BIGINT NOT NULL,
  historia_medica_id BIGINT NULL,
  medico_id BIGINT NOT NULL,
  mascota_id INT NOT NULL,
  propietario_id BIGINT NULL,
  estado_despacho ENUM(
    'PENDIENTE_DESPACHO','EN_PREPARACION','DESPACHO_PARCIAL',
    'DESPACHADO','PENDIENTE_POR_DISPONIBILIDAD','CANCELADO'
  ) NOT NULL DEFAULT 'PENDIENTE_DESPACHO',
  observaciones TEXT NULL,
  usuario_creador_id BIGINT NULL,
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_confirmacion DATETIME NULL,
  PRIMARY KEY(id),
  INDEX idx_formula_estado(estado_despacho),
  CONSTRAINT fk_formula_cita FOREIGN KEY(cita_id) REFERENCES cita(id),
  CONSTRAINT fk_formula_historia FOREIGN KEY(historia_medica_id) REFERENCES historia_medica(id),
  CONSTRAINT fk_formula_medico FOREIGN KEY(medico_id) REFERENCES medico(id),
  CONSTRAINT fk_formula_mascota FOREIGN KEY(mascota_id) REFERENCES mascota(mascota_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS detalle_formula_medica (
  id BIGINT NOT NULL AUTO_INCREMENT,
  formula_id BIGINT NOT NULL,
  medicamento_id BIGINT NOT NULL,
  cantidad_prescrita DECIMAL(12,3) NOT NULL,
  cantidad_dispensada DECIMAL(12,3) NOT NULL DEFAULT 0,
  dosis VARCHAR(255) NULL,
  frecuencia VARCHAR(255) NULL,
  duracion VARCHAR(255) NULL,
  via_administracion VARCHAR(100) NULL,
  indicaciones TEXT NULL,
  PRIMARY KEY(id),
  UNIQUE KEY uq_formula_medicamento(formula_id,medicamento_id),
  CONSTRAINT fk_detalle_formula FOREIGN KEY(formula_id) REFERENCES formula_medica(id),
  CONSTRAINT fk_detalle_formula_medicamento FOREIGN KEY(medicamento_id) REFERENCES medicamento(id),
  CONSTRAINT ck_formula_cantidad CHECK(cantidad_prescrita>0),
  CONSTRAINT ck_formula_dispensada CHECK(cantidad_dispensada>=0 AND cantidad_dispensada<=cantidad_prescrita)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS despacho_medicamento (
  id BIGINT NOT NULL AUTO_INCREMENT,
  formula_id BIGINT NULL,
  formula_legacy_id BIGINT NULL,
  usuario_despacho_id BIGINT NOT NULL,
  estado ENUM('PENDIENTE','PREPARACION','PARCIAL','COMPLETADO','CANCELADO') NOT NULL DEFAULT 'PENDIENTE',
  observaciones TEXT NULL,
  fecha_despacho DATETIME NULL,
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_anulacion DATETIME NULL,
  PRIMARY KEY(id),
  UNIQUE KEY uq_despacho_formula(formula_id),
  UNIQUE KEY uq_despacho_formula_legacy(formula_legacy_id),
  CONSTRAINT fk_despacho_formula FOREIGN KEY(formula_id) REFERENCES formula_medica(id),
  CONSTRAINT fk_despacho_formula_legacy FOREIGN KEY(formula_legacy_id) REFERENCES formula_medica_legacy(id),
  CONSTRAINT ck_despacho_formula_unica CHECK ((formula_id IS NULL) <> (formula_legacy_id IS NULL))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS detalle_despacho (
  id BIGINT NOT NULL AUTO_INCREMENT,
  despacho_id BIGINT NOT NULL,
  medicamento_id BIGINT NOT NULL,
  lote_id BIGINT NOT NULL,
  cantidad_entregada DECIMAL(12,3) NOT NULL,
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  CONSTRAINT fk_detalle_despacho FOREIGN KEY(despacho_id) REFERENCES despacho_medicamento(id),
  CONSTRAINT fk_detalle_despacho_medicamento FOREIGN KEY(medicamento_id) REFERENCES medicamento(id),
  CONSTRAINT fk_detalle_despacho_lote FOREIGN KEY(lote_id) REFERENCES lote_medicamento(id),
  CONSTRAINT ck_detalle_despacho_cantidad CHECK(cantidad_entregada>0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS estado_despacho_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  formula_id BIGINT NOT NULL,
  estado VARCHAR(50) NOT NULL,
  usuario_id BIGINT NOT NULL,
  observacion TEXT NULL,
  fecha_cambio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  INDEX idx_log_formula(formula_id,fecha_cambio),
  CONSTRAINT fk_log_despacho_formula FOREIGN KEY(formula_id) REFERENCES formula_medica_legacy(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS movimiento_inventario (
  id BIGINT NOT NULL AUTO_INCREMENT,
  medicamento_id BIGINT NOT NULL,
  lote_id BIGINT NULL,
  tipo_movimiento ENUM('ENTRADA','SALIDA','AJUSTE','REVERSO') NOT NULL,
  cantidad DECIMAL(12,3) NOT NULL,
  stock_resultante DECIMAL(12,3) NOT NULL,
  usuario_id BIGINT NULL,
  formula_id BIGINT NULL,
  despacho_id BIGINT NULL,
  referencia VARCHAR(100) NULL,
  motivo TEXT NULL,
  fecha_movimiento DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  INDEX idx_movimiento_medicamento(medicamento_id,fecha_movimiento),
  CONSTRAINT fk_movimiento_medicamento FOREIGN KEY(medicamento_id) REFERENCES medicamento(id),
  CONSTRAINT fk_movimiento_lote FOREIGN KEY(lote_id) REFERENCES lote_medicamento(id),
  CONSTRAINT ck_movimiento_cantidad CHECK(cantidad>0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tarifa_servicio (
  id BIGINT NOT NULL AUTO_INCREMENT,
  especialidad_id BIGINT UNSIGNED NULL,
  nombre VARCHAR(150) NOT NULL,
  descripcion TEXT NULL,
  valor DECIMAL(12,2) NOT NULL,
  porcentaje_iva DECIMAL(5,2) NOT NULL DEFAULT 0,
  activa TINYINT(1) NOT NULL DEFAULT 1,
  fecha_vigencia DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_fin_vigencia DATETIME NULL,
  PRIMARY KEY(id),
  INDEX idx_tarifa_especialidad(especialidad_id,activa),
  CONSTRAINT fk_tarifa_especialidad FOREIGN KEY(especialidad_id) REFERENCES especialidad(id),
  CONSTRAINT ck_tarifa_valor CHECK(valor>=0),
  CONSTRAINT ck_tarifa_iva CHECK(porcentaje_iva>=0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tarifa_historial (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tarifa_id BIGINT NOT NULL,
  usuario_id BIGINT NOT NULL,
  valor_anterior DECIMAL(12,2) NULL,
  valor_nuevo DECIMAL(12,2) NOT NULL,
  iva_anterior DECIMAL(5,2) NULL,
  iva_nuevo DECIMAL(5,2) NOT NULL,
  fecha_cambio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  INDEX idx_tarifa_historial(tarifa_id,fecha_cambio),
  CONSTRAINT fk_tarifa_historial FOREIGN KEY(tarifa_id) REFERENCES tarifa_servicio(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS factura (
  id BIGINT NOT NULL AUTO_INCREMENT,
  cita_id BIGINT NULL,
  propietario_id BIGINT NULL,
  mascota_id INT NULL,
  medico_id BIGINT NULL,
  subtotal DECIMAL(12,2) NOT NULL DEFAULT 0,
  impuestos DECIMAL(12,2) NOT NULL DEFAULT 0,
  descuentos DECIMAL(12,2) NOT NULL DEFAULT 0,
  total DECIMAL(12,2) NOT NULL DEFAULT 0,
  estado ENUM('PENDIENTE','PAGO_PARCIAL','PAGADA','ANULADA') NOT NULL DEFAULT 'PENDIENTE',
  fecha_generacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_anulacion DATETIME NULL,
  motivo_anulacion TEXT NULL,
  usuario_generador_id BIGINT NULL,
  PRIMARY KEY(id),
  UNIQUE KEY uq_factura_cita(cita_id),
  INDEX idx_factura_propietario(propietario_id),
  INDEX idx_factura_estado(estado),
  CONSTRAINT fk_factura_cita FOREIGN KEY(cita_id) REFERENCES cita(id),
  CONSTRAINT fk_factura_mascota FOREIGN KEY(mascota_id) REFERENCES mascota(mascota_id),
  CONSTRAINT fk_factura_medico FOREIGN KEY(medico_id) REFERENCES medico(id),
  CONSTRAINT ck_factura_valores CHECK(subtotal>=0 AND impuestos>=0 AND descuentos>=0 AND total>=0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS detalle_factura (
  id BIGINT NOT NULL AUTO_INCREMENT,
  factura_id BIGINT NOT NULL,
  tipo_item ENUM('SERVICIO','MEDICAMENTO','PROCEDIMIENTO') NOT NULL,
  referencia_id BIGINT NULL,
  descripcion VARCHAR(255) NOT NULL,
  cantidad DECIMAL(12,3) NOT NULL,
  precio_unitario DECIMAL(12,2) NOT NULL,
  impuesto DECIMAL(12,2) NOT NULL DEFAULT 0,
  descuento DECIMAL(12,2) NOT NULL DEFAULT 0,
  subtotal DECIMAL(12,2) NOT NULL,
  PRIMARY KEY(id),
  INDEX idx_detalle_factura(factura_id),
  CONSTRAINT fk_detalle_factura FOREIGN KEY(factura_id) REFERENCES factura(id),
  CONSTRAINT ck_detalle_factura_cantidades CHECK(cantidad>0 AND precio_unitario>=0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pago (
  id BIGINT NOT NULL AUTO_INCREMENT,
  factura_id BIGINT NOT NULL,
  usuario_registro_id BIGINT NOT NULL,
  medio_pago VARCHAR(50) NOT NULL,
  monto DECIMAL(12,2) NOT NULL,
  referencia VARCHAR(100) NULL,
  fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  observacion TEXT NULL,
  PRIMARY KEY(id),
  INDEX idx_pago_factura(factura_id,fecha_pago),
  CONSTRAINT fk_pago_factura FOREIGN KEY(factura_id) REFERENCES factura(id),
  CONSTRAINT ck_pago_monto CHECK(monto>0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS carga_inventario_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  usuario_id BIGINT NOT NULL,
  nombre_archivo VARCHAR(255) NOT NULL,
  total_filas INT NOT NULL,
  filas_validas INT NOT NULL,
  filas_rechazadas INT NOT NULL,
  resultado TEXT NULL,
  fecha_carga DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id)
) ENGINE=InnoDB;

-- Datos iniciales y datos de demostracion
INSERT INTO especializacion (nombre, descripcion, activa) VALUES
('Medicina general','Atención clínica general',1),
('Cirugía','Procedimientos quirúrgicos',1),
('Dermatología','Diagnóstico y tratamiento dermatológico',1)
ON DUPLICATE KEY UPDATE activa=1;

INSERT INTO especialidad (nombre, descripcion, activa) VALUES
('Medicina general','Atención clínica general',1),
('Cirugía','Procedimientos quirúrgicos',1),
('Dermatología','Diagnóstico y tratamiento dermatológico',1)
ON DUPLICATE KEY UPDATE activa=1;

INSERT INTO usuario
(username,password_hash,rol,fecha_creacion,activo,email,perfil_id)
SELECT 'gmora','$2a$10$H8WHzA2Hv2j7yL7AgEVn/OYgzur.ZVgh0SPopJItU1I7oDPMSwh5W','Administrador',NOW(),1,'gmora.demo@clinica.local',perfil_id
FROM perfil WHERE nombre='Administrador'
ON DUPLICATE KEY UPDATE activo=1,rol='Administrador';

INSERT INTO usuario
(username,password_hash,rol,fecha_creacion,activo,email,perfil_id)
SELECT 'recepcion','$2a$10$H8WHzA2Hv2j7yL7AgEVn/OYgzur.ZVgh0SPopJItU1I7oDPMSwh5W','Recepcion',NOW(),1,'recepcion.demo@clinica.local',perfil_id
FROM perfil WHERE nombre='Recepcion'
ON DUPLICATE KEY UPDATE activo=1,rol='Recepcion';

INSERT INTO usuario
(username,password_hash,rol,fecha_creacion,activo,email,perfil_id)
SELECT 'medico','$2a$10$H8WHzA2Hv2j7yL7AgEVn/OYgzur.ZVgh0SPopJItU1I7oDPMSwh5W','Medico',NOW(),1,'medico.demo@clinica.local',perfil_id
FROM perfil WHERE nombre='Medico'
ON DUPLICATE KEY UPDATE activo=1,rol='Medico';

INSERT INTO usuario
(username,password_hash,rol,fecha_creacion,activo,email,perfil_id)
SELECT 'despacho','$2a$10$H8WHzA2Hv2j7yL7AgEVn/OYgzur.ZVgh0SPopJItU1I7oDPMSwh5W','DESPACHO_MEDICO',NOW(),1,'despacho.demo@clinica.local',perfil_id
FROM perfil WHERE nombre='DESPACHO_MEDICO'
ON DUPLICATE KEY UPDATE activo=1,rol='DESPACHO_MEDICO';

INSERT INTO cliente
(tipo_documento,numero_documento,nombres,apellidos,fecha_nacimiento,genero,telefono,direccion)
VALUES
('CC','1001','Ana','Martínez','1988-05-12','F','3001112233','Calle 10 #20-30'),
('CC','1002','Carlos','Ramírez','1985-08-21','M','3002223344','Carrera 20 #15-40'),
('CE','1003','Laura','Gómez','1992-03-15','F','3003334455','Avenida 30 #8-12'),
('CC','1004','Pedro','Torres','1980-11-09','M','3004445566','Calle 45 #33-21')
ON DUPLICATE KEY UPDATE nombres=VALUES(nombres),apellidos=VALUES(apellidos);

INSERT INTO mascota
(cliente_id,propietario_id,nombre,especie,raza,sexo,fecha_nacimiento,activa)
SELECT c.cliente_id,c.cliente_id,'Luna','Perro','Criollo','Hembra','2021-03-10',1
FROM cliente c WHERE c.numero_documento='1001'
ON DUPLICATE KEY UPDATE activa=1;

INSERT INTO mascota
(cliente_id,propietario_id,nombre,especie,raza,sexo,fecha_nacimiento,activa)
SELECT c.cliente_id,c.cliente_id,'Simón','Gato','Persa','Macho','2020-07-18',1
FROM cliente c WHERE c.numero_documento='1002'
ON DUPLICATE KEY UPDATE activa=1;

INSERT INTO mascota
(cliente_id,propietario_id,nombre,especie,raza,sexo,fecha_nacimiento,activa)
SELECT c.cliente_id,c.cliente_id,'Milo','Perro','Beagle','Macho','2022-01-05',1
FROM cliente c WHERE c.numero_documento='1003'
ON DUPLICATE KEY UPDATE activa=1;

INSERT INTO mascota
(cliente_id,propietario_id,nombre,especie,raza,sexo,fecha_nacimiento,activa)
SELECT c.cliente_id,c.cliente_id,'Nala','Conejo','Mini Lop','Hembra','2023-04-22',1
FROM cliente c WHERE c.numero_documento='1004'
ON DUPLICATE KEY UPDATE activa=1;

INSERT INTO medicamento
(codigo,nombre,descripcion,presentacion,tipo_producto,precio_compra,precio_venta,cantidad_actual,stock_minimo,fecha_compra,fecha_vence,fecha_ingreso,registro_invima,activo)
VALUES
('AMOX250','Amoxicilina 250mg','Antibiótico veterinario','Tableta','MEDICAMENTO',8000,12000,60,10,'2026-01-10','2027-06-30','2026-01-12','INV-0001',1),
('CARNP10','Carprofeno 100mg','Antiinflamatorio','Tableta','MEDICAMENTO',14000,21000,25,5,'2026-02-15','2027-08-20','2026-02-16','INV-0002',1),
('IVERM01','Ivermectina','Desparasitación','Ampolla','MEDICAMENTO',9000,15000,40,8,'2026-03-01','2027-04-30','2026-03-02','INV-0003',1),
('JERING10','Jeringa 10ml','Utensilio de aplicación','Unidad','UTENSILIO',1500,2500,100,20,'2026-03-10','2030-03-10','2026-03-11',NULL,1)
ON DUPLICATE KEY UPDATE
descripcion=VALUES(descripcion),precio_compra=VALUES(precio_compra),
precio_venta=VALUES(precio_venta),cantidad_actual=VALUES(cantidad_actual),
stock_minimo=VALUES(stock_minimo),activo=1;

INSERT INTO lote_medicamento
(medicamento_id,codigo_lote,cantidad_actual,precio_adquisicion,fecha_compra,fecha_vencimiento,activo)
SELECT m.id,'LOT-AMOX-001',60,8000,'2026-01-10','2027-06-30',1
FROM medicamento m WHERE m.codigo='AMOX250'
ON DUPLICATE KEY UPDATE cantidad_actual=VALUES(cantidad_actual),activo=1;

INSERT INTO lote_medicamento
(medicamento_id,codigo_lote,cantidad_actual,precio_adquisicion,fecha_compra,fecha_vencimiento,activo)
SELECT m.id,'LOT-CARN-001',25,14000,'2026-02-15','2027-08-20',1
FROM medicamento m WHERE m.codigo='CARNP10'
ON DUPLICATE KEY UPDATE cantidad_actual=VALUES(cantidad_actual),activo=1;

INSERT INTO lote_medicamento
(medicamento_id,codigo_lote,cantidad_actual,precio_adquisicion,fecha_compra,fecha_vencimiento,activo)
SELECT m.id,'LOT-IVERM-001',40,9000,'2026-03-01','2027-04-30',1
FROM medicamento m WHERE m.codigo='IVERM01'
ON DUPLICATE KEY UPDATE cantidad_actual=VALUES(cantidad_actual),activo=1;

INSERT INTO lote_medicamento
(medicamento_id,codigo_lote,cantidad_actual,precio_adquisicion,fecha_compra,fecha_vencimiento,activo)
SELECT m.id,'LOT-JERG-001',100,1500,'2026-03-10','2030-03-10',1
FROM medicamento m WHERE m.codigo='JERING10'
ON DUPLICATE KEY UPDATE cantidad_actual=VALUES(cantidad_actual),activo=1;

INSERT INTO medico
(tipo_documento,numero_documento,nombres,apellidos,telefono,registro_profesional,especializacion_id,especialidad_id,activo)
SELECT 'CC','2001','María','Rodríguez','3015556677','RM-1001',e.id,ed.id,1
FROM especializacion e JOIN especialidad ed
WHERE e.nombre='Medicina general' AND ed.nombre='Medicina general'
ON DUPLICATE KEY UPDATE nombres=VALUES(nombres),apellidos=VALUES(apellidos),activo=1;

INSERT INTO medico
(tipo_documento,numero_documento,nombres,apellidos,telefono,registro_profesional,especializacion_id,especialidad_id,activo)
SELECT 'CC','2002','Andrés','López','3026667788','RM-1002',e.id,ed.id,1
FROM especializacion e JOIN especialidad ed
WHERE e.nombre='Cirugía' AND ed.nombre='Cirugía'
ON DUPLICATE KEY UPDATE nombres=VALUES(nombres),apellidos=VALUES(apellidos),activo=1;

INSERT INTO disponibilidad_medico
(medico_id,dia_semana,hora_inicio,hora_fin,activo)
SELECT m.id,dia, '08:00:00','18:00:00',1
FROM medico m
JOIN (
 SELECT 1 AS dia UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL
 SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6
) d
WHERE m.numero_documento IN ('2001','2002')
ON DUPLICATE KEY UPDATE activo=1;

INSERT INTO tarifa_servicio
(especialidad_id,nombre,descripcion,valor,porcentaje_iva,activa,fecha_vigencia)
SELECT e.id,'Consulta general','Consulta médica general',85000,19,1,NOW()
FROM especialidad e WHERE e.nombre='Medicina general'
ON DUPLICATE KEY UPDATE valor=VALUES(valor),porcentaje_iva=VALUES(porcentaje_iva),activa=1;

INSERT INTO tarifa_servicio
(especialidad_id,nombre,descripcion,valor,porcentaje_iva,activa,fecha_vigencia)
SELECT e.id,'Consulta quirúrgica','Evaluación prequirúrgica',150000,19,1,NOW()
FROM especialidad e WHERE e.nombre='Cirugía'
ON DUPLICATE KEY UPDATE valor=VALUES(valor),porcentaje_iva=VALUES(porcentaje_iva),activa=1;

INSERT INTO cita
(cliente_id,propietario_id,mascota_id,medico_id,especialidad_id,fecha_hora,fecha_fin,estado,motivo)
SELECT c.cliente_id,c.cliente_id,m.mascota_id,md.id,e.id,
       NOW()+INTERVAL 1 DAY,NOW()+INTERVAL 1 DAY+INTERVAL 30 MINUTE,'CONFIRMADA','Control general'
FROM cliente c
JOIN mascota m ON m.cliente_id=c.cliente_id
JOIN medico md ON md.numero_documento='2001'
JOIN especialidad e ON e.nombre='Medicina general'
WHERE c.numero_documento='1001'
AND NOT EXISTS (
 SELECT 1 FROM cita ci
 WHERE ci.mascota_id=m.mascota_id
   AND ci.estado IN ('PENDIENTE','CONFIRMADA','Programada')
);

INSERT INTO cita
(cliente_id,propietario_id,mascota_id,medico_id,especialidad_id,fecha_hora,fecha_fin,estado,motivo)
SELECT c.cliente_id,c.cliente_id,m.mascota_id,md.id,e.id,
       NOW()+INTERVAL 2 DAY,NOW()+INTERVAL 2 DAY+INTERVAL 30 MINUTE,'PENDIENTE','Vacunación y revisión'
FROM cliente c
JOIN mascota m ON m.cliente_id=c.cliente_id
JOIN medico md ON md.numero_documento='2001'
JOIN especialidad e ON e.nombre='Medicina general'
WHERE c.numero_documento='1002'
AND NOT EXISTS (
 SELECT 1 FROM cita ci
 WHERE ci.mascota_id=m.mascota_id
   AND ci.estado IN ('PENDIENTE','CONFIRMADA','Programada')
);

INSERT INTO historia_medica
(cita_id,paciente_id,mascota_id,propietario_id,motivo_consulta,sintomas,diagnostico,observaciones,recomendaciones,fecha_atencion,medico_id,estado,fecha_creacion)
SELECT ci.id,c.cliente_id,m.mascota_id,c.cliente_id,
       'Control general','Sin síntomas aparentes','Paciente estable','Atención inicial','Mantener controles preventivos',
       NOW()-INTERVAL 1 DAY,ci.medico_id,'ABIERTA',NOW()-INTERVAL 1 DAY
FROM cita ci
JOIN mascota m ON m.mascota_id=ci.mascota_id
JOIN cliente c ON c.cliente_id=m.cliente_id
WHERE NOT EXISTS (
 SELECT 1 FROM historia_medica h WHERE h.cita_id=ci.id
);

INSERT INTO formula_medica_legacy
(cita_id,medicamento_id,dosis,indicaciones,cantidad_prescrita,estado_despacho,fecha_creacion_registro)
SELECT ci.id,md.id,'1 tableta cada 12 horas','Administrar después de alimentos',10,'PENDIENTE_DESPACHO',NOW()
FROM cita ci
JOIN mascota ma ON ma.mascota_id=ci.mascota_id
JOIN cliente c ON c.cliente_id=ma.cliente_id
JOIN medicamento md ON md.codigo='AMOX250'
WHERE c.numero_documento='1001'
AND NOT EXISTS (
 SELECT 1 FROM formula_medica_legacy f
 WHERE f.cita_id=ci.id AND f.medicamento_id=md.id
);

INSERT INTO movimiento_inventario
(medicamento_id,lote_id,tipo_movimiento,cantidad,stock_resultante,usuario_id,referencia,motivo,fecha_movimiento)
SELECT md.id,l.id,'ENTRADA',60,60,u.id,'CARGA-SEED','Inventario inicial automático',NOW()
FROM medicamento md
JOIN lote_medicamento l ON l.medicamento_id=md.id
JOIN usuario u ON u.username='gmora'
WHERE md.codigo='AMOX250'
AND NOT EXISTS (
 SELECT 1 FROM movimiento_inventario mi
 WHERE mi.referencia='CARGA-SEED' AND mi.medicamento_id=md.id
);

SET FOREIGN_KEY_CHECKS = 1;
