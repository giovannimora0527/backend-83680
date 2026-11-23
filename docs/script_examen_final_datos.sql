-- =====================================================================
-- EXAMEN FINAL - Datos de prueba del inventario (despues del script de ajustes)
-- =====================================================================
USE clinica;
SET NAMES utf8mb4;

-- Productos nuevos (medicamentos con registro Invima y utensilios sin el)
INSERT INTO medicamento (codigo, nombre, descripcion, presentacion, tipo, registro_invima, precio_venta, iva_porcentaje,
                         stock_minimo, fecha_proximo_pedido, fecha_creacion_registro) VALUES
 ('AMOX0250','Amoxicilina 250mg','Antibiótico de amplio espectro','Tabletas x 10','MEDICAMENTO','INVIMA2021M-001122',12000,0,20,DATE_ADD(CURDATE(), INTERVAL 15 DAY),NOW()),
 ('MELOX015','Meloxicam 1.5mg/ml','Antiinflamatorio oral para perros','Suspensión 10ml','MEDICAMENTO','INVIMA2019M-003344',35000,0,5,DATE_ADD(CURDATE(), INTERVAL 30 DAY),NOW()),
 ('IVERM010','Ivermectina 1%','Antiparasitario','Frasco 50ml','MEDICAMENTO','INVIMA2022M-005566',28000,0,5,NULL,NOW()),
 ('JERIN005','Jeringa 5ml','Jeringa desechable estéril','Unidad','UTENSILIO',NULL,800,19,100,NULL,NOW()),
 ('GASAS010','Gasas estériles','Paquete de gasas 10x10','Paquete x 10','UTENSILIO',NULL,3500,19,30,NULL,NOW());

-- Lotes (un medicamento puede tener varios lotes con distinto precio y vencimiento)
INSERT INTO medicamento_lote (medicamento_id, numero_lote, fecha_compra, fecha_ingreso, fecha_vence, precio_compra, cantidad_inicial, cantidad_disponible)
SELECT id, 'L-2026-01', CURDATE() - INTERVAL 60 DAY, NOW(), CURDATE() + INTERVAL 300 DAY, 4000, 100, 100 FROM medicamento WHERE codigo = 'MED00002';
INSERT INTO medicamento_lote (medicamento_id, numero_lote, fecha_compra, fecha_ingreso, fecha_vence, precio_compra, cantidad_inicial, cantidad_disponible)
SELECT id, 'L-2025-09', CURDATE() - INTERVAL 200 DAY, NOW(), CURDATE() + INTERVAL 20 DAY, 3800, 30, 30 FROM medicamento WHERE codigo = 'MED00002';
INSERT INTO medicamento_lote (medicamento_id, numero_lote, fecha_compra, fecha_ingreso, fecha_vence, precio_compra, cantidad_inicial, cantidad_disponible)
SELECT id, 'L-2024-11', CURDATE() - INTERVAL 400 DAY, NOW(), CURDATE() - INTERVAL 5 DAY, 3500, 15, 15 FROM medicamento WHERE codigo = 'MED00002';
INSERT INTO medicamento_lote (medicamento_id, numero_lote, fecha_compra, fecha_ingreso, fecha_vence, precio_compra, cantidad_inicial, cantidad_disponible)
SELECT id, 'INY-01', CURDATE() - INTERVAL 30 DAY, NOW(), CURDATE() + INTERVAL 400 DAY, 9000, 8, 8 FROM medicamento WHERE codigo = 'MED00005';
INSERT INTO medicamento_lote (medicamento_id, numero_lote, fecha_compra, fecha_ingreso, fecha_vence, precio_compra, cantidad_inicial, cantidad_disponible)
SELECT id, 'AMX-77', CURDATE() - INTERVAL 10 DAY, NOW(), CURDATE() + INTERVAL 500 DAY, 6000, 50, 50 FROM medicamento WHERE codigo = 'AMOX0250';
INSERT INTO medicamento_lote (medicamento_id, numero_lote, fecha_compra, fecha_ingreso, fecha_vence, precio_compra, cantidad_inicial, cantidad_disponible)
SELECT id, 'MLX-12', CURDATE() - INTERVAL 10 DAY, NOW(), CURDATE() + INTERVAL 45 DAY, 20000, 3, 3 FROM medicamento WHERE codigo = 'MELOX015';
INSERT INTO medicamento_lote (medicamento_id, numero_lote, fecha_compra, fecha_ingreso, fecha_vence, precio_compra, cantidad_inicial, cantidad_disponible)
SELECT id, 'JER-500', CURDATE() - INTERVAL 10 DAY, NOW(), NULL, 300, 500, 500 FROM medicamento WHERE codigo = 'JERIN005';
-- IVERM010 y GASAS010 quedan sin lotes (agotados) para ver las alertas

-- Movimiento de ENTRADA por cada lote cargado
INSERT INTO movimiento_inventario (medicamento_id, lote_id, tipo, cantidad, saldo_lote, fecha, usuario_id, observacion)
SELECT l.medicamento_id, l.id, 'ENTRADA', l.cantidad_inicial, l.cantidad_disponible, l.fecha_ingreso,
       (SELECT id FROM usuario WHERE username = 'admin'), 'Inventario inicial'
FROM medicamento_lote l;

-- Una cita futura del usuario "medico" (Carlos Martinez Ruiz, id 3) para probar la atencion
INSERT INTO cita (cliente_id, mascota_id, medico_id, fecha_hora, fecha_hora_fin, estado, motivo)
SELECT m.cliente_id, m.mascota_id, 3, TIMESTAMP(CURDATE() + INTERVAL 1 DAY, '09:00:00'),
       TIMESTAMP(CURDATE() + INTERVAL 1 DAY, '09:30:00'), 'confirmada', 'Control y vacunación'
FROM mascota m WHERE m.mascota_id = 1;
