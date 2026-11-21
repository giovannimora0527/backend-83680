INSERT INTO especializacion (nombre, descripcion, codigo_especializacion)
SELECT 'Medicina general demo', 'Especialidad para datos de presentación', 'DEMO'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM especializacion WHERE codigo_especializacion = 'DEMO'
);

INSERT INTO raza (nombre, especie, fecha_creacion)
SELECT 'Labrador demo', 'Canino', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM raza WHERE nombre = 'Labrador demo' AND especie = 'Canino');

INSERT INTO raza (nombre, especie, fecha_creacion)
SELECT 'Mestizo demo', 'Canino', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM raza WHERE nombre = 'Mestizo demo' AND especie = 'Canino');

INSERT INTO raza (nombre, especie, fecha_creacion)
SELECT 'Criollo demo', 'Felino', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM raza WHERE nombre = 'Criollo demo' AND especie = 'Felino');

INSERT INTO cliente (
    tipo_documento, numero_documento, nombres, apellidos, fecha_nacimiento,
    genero, telefono, direccion, activo
)
SELECT 'CC', '9999999901', 'Ana', 'Demo', '1990-04-12', 'F', '3000000001',
       'Propietaria de prueba', TRUE
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM cliente WHERE tipo_documento = 'CC' AND numero_documento = '9999999901'
);

INSERT INTO cliente (
    tipo_documento, numero_documento, nombres, apellidos, fecha_nacimiento,
    genero, telefono, direccion, activo
)
SELECT 'CC', '9999999902', 'Carlos', 'Prueba', '1987-09-23', 'M', '3000000002',
       'Propietario de prueba', TRUE
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM cliente WHERE tipo_documento = 'CC' AND numero_documento = '9999999902'
);

INSERT INTO medico (
    tipo_documento, numero_documento, nombres, apellidos, telefono,
    registro_profesional, especializacion_id
)
SELECT 'CC', '9999999903', 'Valentina', 'Veterinaria Demo', '3000000003',
       'DEMO-RM-001', e.id
FROM especializacion e
WHERE e.codigo_especializacion = 'DEMO'
  AND NOT EXISTS (SELECT 1 FROM medico WHERE numero_documento = '9999999903');

INSERT INTO mascota (nombre_mascota, edad, raza_id, fecha_registro, cliente_id)
SELECT 'Rocky demo', 4, r.raza_id, NOW(), c.cliente_id
FROM raza r
JOIN cliente c ON c.tipo_documento = 'CC' AND c.numero_documento = '9999999901'
WHERE r.nombre = 'Labrador demo' AND r.especie = 'Canino'
  AND NOT EXISTS (
      SELECT 1 FROM mascota m WHERE m.nombre_mascota = 'Rocky demo' AND m.cliente_id = c.cliente_id
  );

INSERT INTO mascota (nombre_mascota, edad, raza_id, fecha_registro, cliente_id)
SELECT 'Luna demo', 2, r.raza_id, NOW(), c.cliente_id
FROM raza r
JOIN cliente c ON c.tipo_documento = 'CC' AND c.numero_documento = '9999999901'
WHERE r.nombre = 'Mestizo demo' AND r.especie = 'Canino'
  AND NOT EXISTS (
      SELECT 1 FROM mascota m WHERE m.nombre_mascota = 'Luna demo' AND m.cliente_id = c.cliente_id
  );

INSERT INTO mascota (nombre_mascota, edad, raza_id, fecha_registro, cliente_id)
SELECT 'Nube demo', 3, r.raza_id, NOW(), c.cliente_id
FROM raza r
JOIN cliente c ON c.tipo_documento = 'CC' AND c.numero_documento = '9999999902'
WHERE r.nombre = 'Criollo demo' AND r.especie = 'Felino'
  AND NOT EXISTS (
      SELECT 1 FROM mascota m WHERE m.nombre_mascota = 'Nube demo' AND m.cliente_id = c.cliente_id
  );

INSERT INTO cita (
    cliente_id, mascota_id, medico_id, fecha_hora, fecha_hora_fin,
    estado, motivo, historial_cambios
)
SELECT c.cliente_id, m.mascota_id, d.id,
       TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:00:00'),
       TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:30:00'),
       'CONFIRMADA', 'Consulta de control demo', 'Registro de demostración'
FROM cliente c
JOIN mascota m ON m.cliente_id = c.cliente_id AND m.nombre_mascota = 'Rocky demo'
JOIN medico d ON d.numero_documento = '9999999903'
WHERE c.numero_documento = '9999999901'
  AND NOT EXISTS (
      SELECT 1 FROM cita x
      WHERE x.cliente_id = c.cliente_id AND x.motivo = 'Consulta de control demo'
  );

INSERT INTO cita (
    cliente_id, mascota_id, medico_id, fecha_hora, fecha_hora_fin,
    estado, motivo, historial_cambios
)
SELECT c.cliente_id, m.mascota_id, d.id,
       TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 2 DAY), '10:30:00'),
       TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 2 DAY), '11:00:00'),
       'PENDIENTE', 'Valoración inicial demo', 'Registro de demostración'
FROM cliente c
JOIN mascota m ON m.cliente_id = c.cliente_id AND m.nombre_mascota = 'Nube demo'
JOIN medico d ON d.numero_documento = '9999999903'
WHERE c.numero_documento = '9999999902'
  AND NOT EXISTS (
      SELECT 1 FROM cita x
      WHERE x.cliente_id = c.cliente_id AND x.motivo = 'Valoración inicial demo'
  );

INSERT INTO medicamento (
    nombre, descripcion, presentacion, codigo, lote, registro_invima,
    requiere_registro_invima, precio_compra, precio_venta, cantidad_disponible,
    stock_minimo, fecha_compra, fecha_ingreso_inventario, fecha_vence,
    fecha_proximo_pedido, activo, fecha_creacion_registro
)
SELECT 'Antiparasitario demo', 'Producto ficticio para pruebas', 'Tableta',
       'TEST0001', 'TEST-LOTE-A', 'INVIMA-DEMO-001', TRUE, 8000, 12000, 30,
       5, CURDATE(), CURDATE(), DATE_ADD(CURDATE(), INTERVAL 365 DAY),
       NULL, TRUE, NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM medicamento WHERE codigo = 'TEST0001');

INSERT INTO medicamento (
    nombre, descripcion, presentacion, codigo, lote, registro_invima,
    requiere_registro_invima, precio_compra, precio_venta, cantidad_disponible,
    stock_minimo, fecha_compra, fecha_ingreso_inventario, fecha_vence,
    fecha_proximo_pedido, activo, fecha_creacion_registro
)
SELECT 'Solución ótica demo', 'Producto ficticio para pruebas', 'Frasco 15 ml',
       'TEST0002', 'TEST-LOTE-B', NULL, FALSE, 9500, 14500, 15,
       4, CURDATE(), CURDATE(), DATE_ADD(CURDATE(), INTERVAL 240 DAY),
       NULL, TRUE, NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM medicamento WHERE codigo = 'TEST0002');

INSERT INTO medicamento (
    nombre, descripcion, presentacion, codigo, lote, registro_invima,
    requiere_registro_invima, precio_compra, precio_venta, cantidad_disponible,
    stock_minimo, fecha_compra, fecha_ingreso_inventario, fecha_vence,
    fecha_proximo_pedido, activo, fecha_creacion_registro
)
SELECT 'Vendaje veterinario demo', 'Insumo ficticio para pruebas', 'Rollo',
       'TEST0003', 'TEST-LOTE-C', NULL, FALSE, 3000, 5000, 20,
       5, CURDATE(), CURDATE(), DATE_ADD(CURDATE(), INTERVAL 730 DAY),
       NULL, TRUE, NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM medicamento WHERE codigo = 'TEST0003');

INSERT INTO lote_medicamento (
    medicamento_id, codigo_lote, cantidad_disponible, precio_compra,
    fecha_compra, fecha_ingreso, fecha_vencimiento, activo, fecha_creacion
)
SELECT m.id, 'TEST0001-A', 12, 7800, CURDATE(), CURDATE(),
       DATE_ADD(CURDATE(), INTERVAL 90 DAY), TRUE, NOW()
FROM medicamento m
WHERE m.codigo = 'TEST0001'
  AND NOT EXISTS (
      SELECT 1 FROM lote_medicamento l WHERE l.medicamento_id = m.id AND l.codigo_lote = 'TEST0001-A'
  );

INSERT INTO lote_medicamento (
    medicamento_id, codigo_lote, cantidad_disponible, precio_compra,
    fecha_compra, fecha_ingreso, fecha_vencimiento, activo, fecha_creacion
)
SELECT m.id, 'TEST0001-B', 18, 8200, CURDATE(), CURDATE(),
       DATE_ADD(CURDATE(), INTERVAL 180 DAY), TRUE, NOW()
FROM medicamento m
WHERE m.codigo = 'TEST0001'
  AND NOT EXISTS (
      SELECT 1 FROM lote_medicamento l WHERE l.medicamento_id = m.id AND l.codigo_lote = 'TEST0001-B'
  );

INSERT INTO lote_medicamento (
    medicamento_id, codigo_lote, cantidad_disponible, precio_compra,
    fecha_compra, fecha_ingreso, fecha_vencimiento, activo, fecha_creacion
)
SELECT m.id, 'TEST0002-A', 15, 9500, CURDATE(), CURDATE(),
       DATE_ADD(CURDATE(), INTERVAL 240 DAY), TRUE, NOW()
FROM medicamento m
WHERE m.codigo = 'TEST0002'
  AND NOT EXISTS (
      SELECT 1 FROM lote_medicamento l WHERE l.medicamento_id = m.id AND l.codigo_lote = 'TEST0002-A'
  );

INSERT INTO lote_medicamento (
    medicamento_id, codigo_lote, cantidad_disponible, precio_compra,
    fecha_compra, fecha_ingreso, fecha_vencimiento, activo, fecha_creacion
)
SELECT m.id, 'TEST0003-A', 20, 3000, CURDATE(), CURDATE(),
       DATE_ADD(CURDATE(), INTERVAL 730 DAY), TRUE, NOW()
FROM medicamento m
WHERE m.codigo = 'TEST0003'
  AND NOT EXISTS (
      SELECT 1 FROM lote_medicamento l WHERE l.medicamento_id = m.id AND l.codigo_lote = 'TEST0003-A'
  );
