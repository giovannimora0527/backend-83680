-- Base de datos completa de la clinica (estructura + datos) para Docker
CREATE DATABASE IF NOT EXISTS clinica CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE clinica;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS `anotacion_historia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `anotacion_historia` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `historia_id` int NOT NULL,
  `medico_id` int NOT NULL,
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP,
  `descripcion` text NOT NULL,
  `observacion` text,
  `diagnostico` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `anotacion_historia` WRITE;
/*!40000 ALTER TABLE `anotacion_historia` DISABLE KEYS */;
INSERT INTO `anotacion_historia` VALUES (1,1,1,'2026-09-21 14:39:32','Paciente ingresa a consulta por vómito y disminución del apetito desde hace aproximadamente 24 horas. Al examen físico se encuentra alerta, con mucosas rosadas, temperatura de 38,7 °C y leve dolor abdominal a la palpación. Se recomienda hidratación y se administra tratamiento antiemético. Se indica dieta blanda y vigilancia de signos de alarma. Se recomienda control en 48 horas o antes en caso de presentar empeoramiento.',NULL,NULL),(3,3,11,'2026-10-16 11:21:47','seguimiento','habshbadhs','el mismo');
/*!40000 ALTER TABLE `anotacion_historia` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `carga_inventario_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carga_inventario_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint unsigned DEFAULT NULL,
  `fecha` datetime NOT NULL,
  `nombre_archivo` varchar(255) NOT NULL,
  `total_filas` int NOT NULL,
  `filas_ok` int NOT NULL,
  `filas_error` int NOT NULL,
  `detalle` longtext,
  PRIMARY KEY (`id`),
  KEY `fk_carga_usuario` (`usuario_id`),
  CONSTRAINT `fk_carga_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `carga_inventario_log` WRITE;
/*!40000 ALTER TABLE `carga_inventario_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `carga_inventario_log` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `cita`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cita` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `cliente_id` int NOT NULL,
  `mascota_id` int NOT NULL,
  `medico_id` int NOT NULL,
  `fecha_hora` datetime NOT NULL,
  `estado` varchar(20) NOT NULL,
  `motivo` text,
  `fecha_hora_fin` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `fk_cita_estado` (`estado`),
  CONSTRAINT `fk_cita_estado` FOREIGN KEY (`estado`) REFERENCES `estado_cita` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `cita` WRITE;
/*!40000 ALTER TABLE `cita` DISABLE KEYS */;
INSERT INTO `cita` VALUES (1,1,1,3,'2025-10-16 21:30:00','programada','Consulta general','2025-10-16 22:00:00'),(2,2,11,3,'2024-06-21 14:30:00','programada','Consulta odontologica','2024-06-21 15:00:00'),(3,3,3,2,'2024-06-20 18:30:00','programada','Consulta general','2024-06-20 19:00:00'),(4,6,6,11,'2026-09-22 08:11:00','atendida','aun le lele pancha','2026-09-22 08:41:00'),(5,6,6,11,'2026-09-22 08:12:00','atendida','Revisión médica realizada con exito','2026-09-22 08:42:00'),(6,7,7,13,'2026-09-22 08:12:00','cancelada','Dolor de barriga','2026-09-22 08:42:00'),(9,2,12,1,'2026-10-16 14:15:00','programada','vomitos y diarrea y dolor','2026-10-16 14:45:00'),(10,11,21,6,'2026-10-16 15:50:00','atendida','No quiere comer','2026-10-16 16:20:00'),(11,1,1,3,'2026-11-23 09:00:00','confirmada','Control y vacunación','2026-11-23 09:30:00');
/*!40000 ALTER TABLE `cita` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `cliente_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `usuario_id` int DEFAULT NULL,
  `tipo_documento` varchar(10) NOT NULL,
  `numero_documento` varchar(20) NOT NULL,
  `nombres` varchar(100) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `fecha_nacimiento` date NOT NULL,
  `genero` char(1) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `direccion` text,
  `activo` tinyint(1) NOT NULL,
  PRIMARY KEY (`cliente_id`),
  UNIQUE KEY `id` (`cliente_id`),
  UNIQUE KEY `numero_documento` (`numero_documento`),
  UNIQUE KEY `usuario_id` (`usuario_id`),
  KEY `idx_paciente_documento` (`numero_documento`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES (1,1,'CC','100000001','Juan','Pérez López','1990-05-12','M','3001112233','Calle 10 #12-34',1),(2,2,'CC','100000002','María','Gómez Rodríguez','1985-08-22','F','3012223344','Carrera 15 #20-10',1),(3,3,'TI','900000003','Andrés','Martínez Díaz','2005-03-18','M','3023334455','Av. Boyacá #45-67',1),(4,4,'CC','100000004','Laura','Hernández Ruiz','1992-12-01','F','3034445566','Calle 5 #7-89',1),(5,5,'CC','100000005','Carlos','Ramírez Torres','1980-07-30','M','3045556677','Carrera 8 #15-20',1),(6,6,'CE','200000006','Ana','Castro Moreno','1998-01-25','F','3056667788','Diagonal 12 #25-50',1),(7,7,'CC','100000007','Santiago','Moreno Vargas','1995-09-14','M','3067778899','Transversal 20 #40-22',1),(8,8,'CC','100000008','Paula','Ríos Fernández','1987-11-10','F','3078889900','Calle 22 #18-45',1),(9,9,'TI','900000009','Felipe','Gutiérrez Méndez','2006-06-06','M','3089990011','Carrera 50 #60-12',1),(10,10,'CC','100000010','Diana','Salazar Peña','1993-04-21','F','3090001122','Av. Caracas #100-50',1),(11,NULL,'CC','1052075544','Enoc','Arenas Hernandez','2006-10-29','M','3208014961','Diagonal 74 Sur # 79-13',1);
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `despacho`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `despacho` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `formula_id` bigint unsigned NOT NULL,
  `estado` varchar(30) NOT NULL,
  `usuario_id` bigint unsigned NOT NULL,
  `fecha` datetime NOT NULL,
  `observacion` text,
  `usuario_reversion_id` bigint unsigned DEFAULT NULL,
  `fecha_reversion` datetime DEFAULT NULL,
  `motivo_reversion` text,
  PRIMARY KEY (`id`),
  KEY `fk_desp_formula` (`formula_id`),
  KEY `fk_desp_usuario` (`usuario_id`),
  KEY `fk_desp_usuario_rev` (`usuario_reversion_id`),
  CONSTRAINT `fk_desp_formula` FOREIGN KEY (`formula_id`) REFERENCES `formula_medica` (`id`),
  CONSTRAINT `fk_desp_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`),
  CONSTRAINT `fk_desp_usuario_rev` FOREIGN KEY (`usuario_reversion_id`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `despacho` WRITE;
/*!40000 ALTER TABLE `despacho` DISABLE KEYS */;
/*!40000 ALTER TABLE `despacho` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `despacho_detalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `despacho_detalle` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `despacho_id` bigint unsigned NOT NULL,
  `formula_detalle_id` bigint unsigned NOT NULL,
  `lote_id` bigint unsigned NOT NULL,
  `cantidad` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_ddet_despacho` (`despacho_id`),
  KEY `fk_ddet_fdetalle` (`formula_detalle_id`),
  KEY `fk_ddet_lote` (`lote_id`),
  CONSTRAINT `fk_ddet_despacho` FOREIGN KEY (`despacho_id`) REFERENCES `despacho` (`id`),
  CONSTRAINT `fk_ddet_fdetalle` FOREIGN KEY (`formula_detalle_id`) REFERENCES `formula_detalle` (`id`),
  CONSTRAINT `fk_ddet_lote` FOREIGN KEY (`lote_id`) REFERENCES `medicamento_lote` (`id`),
  CONSTRAINT `ck_ddet_cantidad` CHECK ((`cantidad` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `despacho_detalle` WRITE;
/*!40000 ALTER TABLE `despacho_detalle` DISABLE KEYS */;
/*!40000 ALTER TABLE `despacho_detalle` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `despacho_estado_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `despacho_estado_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `formula_id` bigint unsigned NOT NULL,
  `estado_anterior` varchar(30) DEFAULT NULL,
  `estado_nuevo` varchar(30) NOT NULL,
  `usuario_id` bigint unsigned NOT NULL,
  `fecha` datetime NOT NULL,
  `observacion` text,
  PRIMARY KEY (`id`),
  KEY `fk_dlog_formula` (`formula_id`),
  KEY `fk_dlog_usuario` (`usuario_id`),
  CONSTRAINT `fk_dlog_formula` FOREIGN KEY (`formula_id`) REFERENCES `formula_medica` (`id`),
  CONSTRAINT `fk_dlog_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `despacho_estado_log` WRITE;
/*!40000 ALTER TABLE `despacho_estado_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `despacho_estado_log` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `especializacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `especializacion` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text,
  `codigo_especializacion` varchar(10) NOT NULL DEFAULT '""',
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `nombre` (`nombre`),
  UNIQUE KEY `codigo_especializacion_UNIQUE` (`codigo_especializacion`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `especializacion` WRITE;
/*!40000 ALTER TABLE `especializacion` DISABLE KEYS */;
INSERT INTO `especializacion` VALUES (1,'Cardiología 2','Especialización médica centrada en el diagnóstico y tratamiento de enfermedades del corazón y otras enfermedades asociadas.','COD_100',1),(2,'Neurología','Se ocupa de los trastornos del sistema nervioso central y periférico.','COD_02',1),(3,'Pediatría','Atención médica especializada en niños y adolescentes.','COD_03',1),(4,'Oncología','Estudio y tratamiento de diferentes tipos de cáncer.','COD_04',1),(5,'Dermatología','Diagnóstico y tratamiento de enfermedades de la piel.','COD_05',1),(6,'Ginecología','Enfocada en el sistema reproductor femenino.','COD_06',1),(7,'Urología','Tratamiento del sistema urinario y el sistema reproductor masculino.','COD_07',1),(8,'Psiquiatría','Estudio y tratamiento de enfermedades mentales y emocionales.','COD_08',1),(9,'Oftalmología','Especialización en la salud ocular y la visión.','COD_09',1),(10,'Otorrinolaringología','Se encarga de enfermedades de oído, nariz y garganta.','COD_10',1),(11,'Traumatología','Tratamiento de lesiones y enfermedades del sistema musculoesquelético.','COD_11',1),(12,'Anestesiología','Responsable de la anestesia y control del dolor durante procedimientos quirúrgicos.','COD_12',1),(13,'Reumatología','Estudia enfermedades autoinmunes y musculoesqueléticas.','COD_13',1),(14,'Endocrinología','Especialización en glándulas y trastornos hormonales.','COD_14',1),(15,'Gastroenterología','Diagnóstico y tratamiento de enfermedades del aparato digestivo.','COD_15',1);
/*!40000 ALTER TABLE `especializacion` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `estado_cita`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `estado_cita` (
  `codigo` varchar(20) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `color` varchar(10) NOT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `estado_cita` WRITE;
/*!40000 ALTER TABLE `estado_cita` DISABLE KEYS */;
INSERT INTO `estado_cita` VALUES ('atendida','Atendida / finalizada','#10b981'),('cancelada','Cancelada','#ef4444'),('confirmada','Confirmada','#3b82f6'),('pendiente','Pendiente','#94a3b8'),('programada','Programada','#f59e0b');
/*!40000 ALTER TABLE `estado_cita` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `factura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `factura` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `numero` varchar(20) NOT NULL,
  `cita_id` bigint unsigned NOT NULL,
  `cliente_id` bigint unsigned NOT NULL,
  `mascota_id` int NOT NULL,
  `medico_id` bigint unsigned NOT NULL,
  `especialidad` varchar(100) DEFAULT NULL,
  `fecha` datetime NOT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  `iva` decimal(12,2) NOT NULL,
  `descuento` decimal(12,2) NOT NULL DEFAULT '0.00',
  `total` decimal(12,2) NOT NULL,
  `total_pagado` decimal(12,2) NOT NULL DEFAULT '0.00',
  `estado` varchar(20) NOT NULL,
  `usuario_id` bigint unsigned NOT NULL,
  `motivo_anulacion` text,
  `fecha_anulacion` datetime DEFAULT NULL,
  `usuario_anulacion_id` bigint unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_factura_numero` (`numero`),
  KEY `fk_fact_cita` (`cita_id`),
  KEY `fk_fact_cliente` (`cliente_id`),
  KEY `fk_fact_mascota` (`mascota_id`),
  KEY `fk_fact_medico` (`medico_id`),
  KEY `fk_fact_usuario` (`usuario_id`),
  CONSTRAINT `fk_fact_cita` FOREIGN KEY (`cita_id`) REFERENCES `cita` (`id`),
  CONSTRAINT `fk_fact_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `cliente` (`cliente_id`),
  CONSTRAINT `fk_fact_mascota` FOREIGN KEY (`mascota_id`) REFERENCES `mascota` (`mascota_id`),
  CONSTRAINT `fk_fact_medico` FOREIGN KEY (`medico_id`) REFERENCES `medico` (`id`),
  CONSTRAINT `fk_fact_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`),
  CONSTRAINT `ck_fact_valores` CHECK (((`subtotal` >= 0) and (`iva` >= 0) and (`descuento` >= 0) and (`total` >= 0) and (`total_pagado` >= 0)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `factura` WRITE;
/*!40000 ALTER TABLE `factura` DISABLE KEYS */;
/*!40000 ALTER TABLE `factura` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `factura_detalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `factura_detalle` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `factura_id` bigint unsigned NOT NULL,
  `tipo` varchar(20) NOT NULL,
  `referencia_id` bigint unsigned NOT NULL,
  `descripcion` varchar(200) NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(12,2) NOT NULL,
  `iva_porcentaje` decimal(5,2) NOT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  `iva_valor` decimal(12,2) NOT NULL,
  `total` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_fdet_factura` (`factura_id`),
  CONSTRAINT `fk_fdet_factura` FOREIGN KEY (`factura_id`) REFERENCES `factura` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `factura_detalle` WRITE;
/*!40000 ALTER TABLE `factura_detalle` DISABLE KEYS */;
/*!40000 ALTER TABLE `factura_detalle` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `factura_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `factura_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `factura_id` bigint unsigned NOT NULL,
  `accion` varchar(30) NOT NULL,
  `detalle` text,
  `usuario_id` bigint unsigned NOT NULL,
  `fecha` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_flog_factura` (`factura_id`),
  KEY `fk_flog_usuario` (`usuario_id`),
  CONSTRAINT `fk_flog_factura` FOREIGN KEY (`factura_id`) REFERENCES `factura` (`id`),
  CONSTRAINT `fk_flog_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `factura_log` WRITE;
/*!40000 ALTER TABLE `factura_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `factura_log` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `formula_detalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `formula_detalle` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `formula_id` bigint unsigned NOT NULL,
  `medicamento_id` bigint unsigned NOT NULL,
  `dosis` varchar(200) NOT NULL,
  `frecuencia` varchar(100) NOT NULL,
  `duracion_dias` int NOT NULL,
  `cantidad` int NOT NULL,
  `via_administracion` varchar(50) NOT NULL,
  `indicaciones` text,
  `cantidad_entregada` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_fdet_formula` (`formula_id`),
  KEY `fk_fdet_medicamento` (`medicamento_id`),
  CONSTRAINT `fk_fdet_formula` FOREIGN KEY (`formula_id`) REFERENCES `formula_medica` (`id`),
  CONSTRAINT `fk_fdet_medicamento` FOREIGN KEY (`medicamento_id`) REFERENCES `medicamento` (`id`),
  CONSTRAINT `ck_fdet_cantidad` CHECK (((`cantidad` > 0) and (`cantidad_entregada` >= 0) and (`cantidad_entregada` <= `cantidad`)))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `formula_detalle` WRITE;
/*!40000 ALTER TABLE `formula_detalle` DISABLE KEYS */;
INSERT INTO `formula_detalle` VALUES (1,1,5,'800 mg cada 12 horas','Ver dosis',7,1,'Oral','Tomar después de las comidas durante 7 días',1),(2,2,6,'500 mg cada 8 horas','Ver dosis',7,1,'Oral','Tomar después de las comidas durante 7 días',1),(3,4,5,'Una pastilla cada 12 horas.','Ver dosis',7,1,'Oral','Cambiando Cualquier cosa',1),(4,5,2,'Una pastilla cada 12 horas.','Ver dosis',7,1,'Oral','Tomar despues del almuerzo y con abundante liquido',1),(5,7,2,'500','cada 8 horas',2,1,'Oral','debe tomarlo en ayunas',1),(6,8,6,'500','cada 8 horas',5,1,'Oral','Tomar cada 8 horas',1);
/*!40000 ALTER TABLE `formula_detalle` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `formula_medica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `formula_medica` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `cita_id` int NOT NULL,
  `medicamento_id` int DEFAULT NULL,
  `dosis` text,
  `indicaciones` text,
  `fecha_creacion_registro` datetime NOT NULL,
  `fecha_actualizacion_registro` datetime DEFAULT NULL,
  `medico_id` int DEFAULT NULL,
  `frecuencia` varchar(100) DEFAULT NULL,
  `duracion_dias` int DEFAULT NULL,
  `fecha_emision` date DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `historia_id` bigint unsigned DEFAULT NULL,
  `estado_despacho` varchar(30) NOT NULL DEFAULT 'PENDIENTE_DESPACHO',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `formula_medica` WRITE;
/*!40000 ALTER TABLE `formula_medica` DISABLE KEYS */;
INSERT INTO `formula_medica` VALUES (1,1,5,'800 mg cada 12 horas','Tomar después de las comidas durante 7 días','2025-09-17 15:17:32',NULL,3,'Ver dosis',7,'2025-09-17',1,NULL,'DESPACHADO'),(2,2,6,'500 mg cada 8 horas','Tomar después de las comidas durante 7 días','2025-09-20 11:52:42',NULL,3,'Ver dosis',7,'2025-09-20',1,NULL,'DESPACHADO'),(4,1,5,'Una pastilla cada 12 horas.','Cambiando Cualquier cosa','2025-10-30 21:09:02','2025-10-30 21:21:51',3,'Ver dosis',7,'2025-10-30',1,NULL,'DESPACHADO'),(5,3,2,'Una pastilla cada 12 horas.','Tomar despues del almuerzo y con abundante liquido','2025-11-01 10:21:04',NULL,2,'Ver dosis',7,'2025-11-01',1,NULL,'DESPACHADO'),(7,5,2,'500','debe tomarlo en ayunas','2026-10-16 11:10:42',NULL,11,'cada 8 horas',2,'2026-10-16',1,NULL,'DESPACHADO'),(8,10,6,'500','Tomar cada 8 horas','2026-10-16 11:52:17','2026-10-17 08:42:53',6,'cada 8 horas',5,'2026-10-16',1,NULL,'DESPACHADO');
/*!40000 ALTER TABLE `formula_medica` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `historia_medica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historia_medica` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `paciente_id` int NOT NULL,
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  `cita_id` int DEFAULT NULL,
  `medico_id` int DEFAULT NULL,
  `fecha_hora` datetime DEFAULT NULL,
  `motivo` text,
  `diagnostico` text,
  `observaciones` text,
  `sintomas` text,
  `recomendaciones` text,
  `tarifa_id` bigint unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `fk_historia_tarifa` (`tarifa_id`),
  CONSTRAINT `fk_historia_tarifa` FOREIGN KEY (`tarifa_id`) REFERENCES `tarifa` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `historia_medica` WRITE;
/*!40000 ALTER TABLE `historia_medica` DISABLE KEYS */;
INSERT INTO `historia_medica` VALUES (1,1,'2026-09-21 13:02:56',NULL,NULL,'2026-09-21 13:02:56','Consulta general',NULL,NULL,NULL,NULL,NULL),(3,6,'2026-10-16 11:20:14',5,11,'2026-09-22 08:12:00','Revisión médica realizada con exito','la mascota presenta una grave intoxicacion por mala alimentacion','n/a',NULL,NULL,NULL),(4,21,'2026-10-16 11:53:06',10,6,'2026-10-16 15:50:00','No quiere comer','No gusta la comida actual','Intentar cambio de comida',NULL,NULL,NULL);
/*!40000 ALTER TABLE `historia_medica` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `horario_medico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `horario_medico` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `medico_id` bigint unsigned NOT NULL,
  `dia_semana` tinyint NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fin` time NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `fk_horario_medico` (`medico_id`),
  CONSTRAINT `fk_horario_medico` FOREIGN KEY (`medico_id`) REFERENCES `medico` (`id`),
  CONSTRAINT `ck_horario_dia` CHECK ((`dia_semana` between 1 and 7)),
  CONSTRAINT `ck_horario_horas` CHECK ((`hora_inicio` < `hora_fin`))
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `horario_medico` WRITE;
/*!40000 ALTER TABLE `horario_medico` DISABLE KEYS */;
INSERT INTO `horario_medico` VALUES (1,1,6,'07:00:00','19:00:00',1),(2,1,5,'07:00:00','19:00:00',1),(3,1,4,'07:00:00','19:00:00',1),(4,1,3,'07:00:00','19:00:00',1),(5,1,2,'07:00:00','19:00:00',1),(6,1,1,'07:00:00','19:00:00',1),(7,2,6,'07:00:00','19:00:00',1),(8,2,5,'07:00:00','19:00:00',1),(9,2,4,'07:00:00','19:00:00',1),(10,2,3,'07:00:00','19:00:00',1),(11,2,2,'07:00:00','19:00:00',1),(12,2,1,'07:00:00','19:00:00',1),(13,3,6,'07:00:00','19:00:00',1),(14,3,5,'07:00:00','19:00:00',1),(15,3,4,'07:00:00','19:00:00',1),(16,3,3,'07:00:00','19:00:00',1),(17,3,2,'07:00:00','19:00:00',1),(18,3,1,'07:00:00','19:00:00',1),(19,4,6,'07:00:00','19:00:00',1),(20,4,5,'07:00:00','19:00:00',1),(21,4,4,'07:00:00','19:00:00',1),(22,4,3,'07:00:00','19:00:00',1),(23,4,2,'07:00:00','19:00:00',1),(24,4,1,'07:00:00','19:00:00',1),(25,5,6,'07:00:00','19:00:00',1),(26,5,5,'07:00:00','19:00:00',1),(27,5,4,'07:00:00','19:00:00',1),(28,5,3,'07:00:00','19:00:00',1),(29,5,2,'07:00:00','19:00:00',1),(30,5,1,'07:00:00','19:00:00',1),(31,6,6,'07:00:00','19:00:00',1),(32,6,5,'07:00:00','19:00:00',1),(33,6,4,'07:00:00','19:00:00',1),(34,6,3,'07:00:00','19:00:00',1),(35,6,2,'07:00:00','19:00:00',1),(36,6,1,'07:00:00','19:00:00',1),(37,7,6,'07:00:00','19:00:00',1),(38,7,5,'07:00:00','19:00:00',1),(39,7,4,'07:00:00','19:00:00',1),(40,7,3,'07:00:00','19:00:00',1),(41,7,2,'07:00:00','19:00:00',1),(42,7,1,'07:00:00','19:00:00',1),(43,8,6,'07:00:00','19:00:00',1),(44,8,5,'07:00:00','19:00:00',1),(45,8,4,'07:00:00','19:00:00',1),(46,8,3,'07:00:00','19:00:00',1),(47,8,2,'07:00:00','19:00:00',1),(48,8,1,'07:00:00','19:00:00',1),(49,9,6,'07:00:00','19:00:00',1),(50,9,5,'07:00:00','19:00:00',1),(51,9,4,'07:00:00','19:00:00',1),(52,9,3,'07:00:00','19:00:00',1),(53,9,2,'07:00:00','19:00:00',1),(54,9,1,'07:00:00','19:00:00',1),(55,10,6,'07:00:00','19:00:00',1),(56,10,5,'07:00:00','19:00:00',1),(57,10,4,'07:00:00','19:00:00',1),(58,10,3,'07:00:00','19:00:00',1),(59,10,2,'07:00:00','19:00:00',1),(60,10,1,'07:00:00','19:00:00',1),(61,11,6,'07:00:00','19:00:00',1),(62,11,5,'07:00:00','19:00:00',1),(63,11,4,'07:00:00','19:00:00',1),(64,11,3,'07:00:00','19:00:00',1),(65,11,2,'07:00:00','19:00:00',1),(66,11,1,'07:00:00','19:00:00',1),(67,12,6,'07:00:00','19:00:00',1),(68,12,5,'07:00:00','19:00:00',1),(69,12,4,'07:00:00','19:00:00',1),(70,12,3,'07:00:00','19:00:00',1),(71,12,2,'07:00:00','19:00:00',1),(72,12,1,'07:00:00','19:00:00',1),(73,13,6,'07:00:00','19:00:00',1),(74,13,5,'07:00:00','19:00:00',1),(75,13,4,'07:00:00','19:00:00',1),(76,13,3,'07:00:00','19:00:00',1),(77,13,2,'07:00:00','19:00:00',1),(78,13,1,'07:00:00','19:00:00',1);
/*!40000 ALTER TABLE `horario_medico` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `mascota`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mascota` (
  `mascota_id` int NOT NULL AUTO_INCREMENT,
  `nombre_mascota` varchar(100) NOT NULL,
  `edad` int NOT NULL,
  `raza_id` int NOT NULL,
  `fecha_registro` datetime NOT NULL,
  `fecha_modificacion` datetime DEFAULT NULL,
  `cliente_id` int NOT NULL,
  PRIMARY KEY (`mascota_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `mascota` WRITE;
/*!40000 ALTER TABLE `mascota` DISABLE KEYS */;
INSERT INTO `mascota` VALUES (1,'Max',3,1,'2026-08-01 08:15:00','2026-08-15 10:20:00',1),(2,'Luna',2,2,'2026-08-01 09:30:00','2026-08-16 11:00:00',2),(3,'Rocky',5,1,'2026-08-02 10:00:00',NULL,3),(4,'Bella',4,2,'2026-08-02 11:20:00','2026-08-17 09:15:00',4),(5,'Toby',1,1,'2026-08-03 08:45:00',NULL,5),(6,'Nala',6,2,'2026-08-03 14:10:00','2026-08-18 14:30:00',6),(7,'Coco',3,1,'2026-08-04 09:00:00',NULL,7),(8,'Simba',2,2,'2026-08-04 15:30:00','2026-08-19 16:00:00',8),(9,'Milo',7,1,'2026-08-05 10:15:00',NULL,9),(10,'Maya',4,2,'2026-08-05 16:00:00','2026-08-20 10:00:00',10),(11,'Thor',5,1,'2026-08-06 08:30:00',NULL,1),(12,'Kira',2,2,'2026-08-06 13:45:00','2026-08-21 13:20:00',2),(13,'Bruno',8,1,'2026-08-07 09:20:00',NULL,3),(14,'Canela',3,2,'2026-08-07 14:30:00','2026-08-22 15:10:00',4),(15,'Zeus',6,1,'2026-08-08 10:45:00',NULL,5),(16,'Sasha',1,2,'2026-08-08 16:15:00','2026-08-23 11:45:00',6),(17,'Duke',4,1,'2026-08-09 08:00:00',NULL,7),(18,'Mia',5,2,'2026-08-09 11:30:00','2026-08-24 09:30:00',8),(19,'Oliver',2,1,'2026-08-10 13:00:00',NULL,9),(20,'Lola',7,2,'2026-08-10 15:45:00','2026-08-25 14:00:00',10),(21,'Balu',4,2,'2026-10-16 11:49:21',NULL,11),(22,'Jeronimo',1,1,'2026-10-16 11:49:41',NULL,11);
/*!40000 ALTER TABLE `mascota` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `medicamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicamento` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text,
  `presentacion` varchar(100) DEFAULT NULL,
  `fecha_compra` date DEFAULT NULL,
  `fecha_vence` date DEFAULT NULL,
  `fecha_creacion_registro` datetime NOT NULL,
  `fecha_modificacion_registro` datetime DEFAULT NULL,
  `codigo` char(8) NOT NULL,
  `tipo` varchar(20) NOT NULL DEFAULT 'MEDICAMENTO',
  `registro_invima` varchar(50) DEFAULT NULL,
  `precio_venta` decimal(12,2) NOT NULL DEFAULT '0.00',
  `iva_porcentaje` decimal(5,2) NOT NULL DEFAULT '0.00',
  `stock_minimo` int NOT NULL DEFAULT '0',
  `fecha_proximo_pedido` date DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `nombre` (`nombre`),
  UNIQUE KEY `uk_medicamento_codigo` (`codigo`),
  CONSTRAINT `ck_medicamento_precio` CHECK ((`precio_venta` >= 0)),
  CONSTRAINT `ck_medicamento_stock` CHECK ((`stock_minimo` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `medicamento` WRITE;
/*!40000 ALTER TABLE `medicamento` DISABLE KEYS */;
INSERT INTO `medicamento` VALUES (2,'Paracetamol','Analgésico y antipirético','Tabletas 500mg','2024-06-01','2026-06-01','2025-09-11 20:56:13','2025-09-13 12:49:23','MED00002','MEDICAMENTO','INVIMA2020M-000002',8000.00,0.00,10,NULL,1),(5,'Paracetamol Inyectable','Analgésico y antipirético','Tabletas 1200mg','2024-06-01','2026-06-01','2025-09-13 10:58:40','2025-09-13 12:49:23','MED00005','MEDICAMENTO','INVIMA2020M-000005',8000.00,0.00,10,NULL,1),(6,'Paracetamol Solución','Analgésico y antipirético','Tabletas 800mg','2024-06-01','2026-06-01','2025-09-13 12:27:12','2025-09-13 12:49:23','MED00006','MEDICAMENTO','INVIMA2020M-000006',8000.00,0.00,10,NULL,1),(7,'Amoxicilina 250mg','Antibiótico de amplio espectro','Tabletas x 10',NULL,NULL,'2026-11-22 19:16:46',NULL,'AMOX0250','MEDICAMENTO','INVIMA2021M-001122',12000.00,0.00,20,'2026-12-07',1),(8,'Meloxicam 1.5mg/ml','Antiinflamatorio oral para perros','Suspensión 10ml',NULL,NULL,'2026-11-22 19:16:46',NULL,'MELOX015','MEDICAMENTO','INVIMA2019M-003344',35000.00,0.00,5,'2026-12-22',1),(9,'Ivermectina 1%','Antiparasitario','Frasco 50ml',NULL,NULL,'2026-11-22 19:16:46',NULL,'IVERM010','MEDICAMENTO','INVIMA2022M-005566',28000.00,0.00,5,NULL,1),(10,'Jeringa 5ml','Jeringa desechable estéril','Unidad',NULL,NULL,'2026-11-22 19:16:46',NULL,'JERIN005','UTENSILIO',NULL,800.00,19.00,100,NULL,1),(11,'Gasas estériles','Paquete de gasas 10x10','Paquete x 10',NULL,NULL,'2026-11-22 19:16:46',NULL,'GASAS010','UTENSILIO',NULL,3500.00,19.00,30,NULL,1);
/*!40000 ALTER TABLE `medicamento` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `medicamento_lote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicamento_lote` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `medicamento_id` bigint unsigned NOT NULL,
  `numero_lote` varchar(50) NOT NULL,
  `fecha_compra` date NOT NULL,
  `fecha_ingreso` datetime NOT NULL,
  `fecha_vence` date DEFAULT NULL,
  `precio_compra` decimal(12,2) NOT NULL,
  `cantidad_inicial` int NOT NULL,
  `cantidad_disponible` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lote` (`medicamento_id`,`numero_lote`),
  CONSTRAINT `fk_lote_medicamento` FOREIGN KEY (`medicamento_id`) REFERENCES `medicamento` (`id`),
  CONSTRAINT `ck_lote_cantidad` CHECK (((`cantidad_disponible` >= 0) and (`cantidad_disponible` <= `cantidad_inicial`))),
  CONSTRAINT `ck_lote_precio` CHECK ((`precio_compra` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `medicamento_lote` WRITE;
/*!40000 ALTER TABLE `medicamento_lote` DISABLE KEYS */;
INSERT INTO `medicamento_lote` VALUES (1,2,'L-2026-01','2026-09-23','2026-11-22 19:16:46','2027-09-18',4000.00,100,100),(2,2,'L-2025-09','2026-05-06','2026-11-22 19:16:46','2026-12-12',3800.00,30,30),(3,2,'L-2024-11','2025-10-18','2026-11-22 19:16:46','2026-11-17',3500.00,15,15),(4,5,'INY-01','2026-10-23','2026-11-22 19:16:46','2027-12-27',9000.00,8,8),(5,7,'AMX-77','2026-11-12','2026-11-22 19:16:46','2028-04-05',6000.00,50,50),(6,8,'MLX-12','2026-11-12','2026-11-22 19:16:46','2027-01-06',20000.00,3,3),(7,10,'JER-500','2026-11-12','2026-11-22 19:16:46',NULL,300.00,500,500);
/*!40000 ALTER TABLE `medicamento_lote` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `medico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medico` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `tipo_documento` varchar(10) NOT NULL,
  `numero_documento` varchar(20) NOT NULL,
  `nombres` varchar(100) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `registro_profesional` varchar(50) NOT NULL,
  `especializacion_id` int NOT NULL DEFAULT '0',
  `usuario_id` bigint unsigned DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `numero_documento` (`numero_documento`),
  UNIQUE KEY `registro_profesional` (`registro_profesional`),
  UNIQUE KEY `uk_medico_usuario` (`usuario_id`),
  KEY `idx_medico_documento` (`numero_documento`),
  CONSTRAINT `fk_medico_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `medico` WRITE;
/*!40000 ALTER TABLE `medico` DISABLE KEYS */;
INSERT INTO `medico` VALUES (1,'CC','1001001001','Juan','Pérez Gómez','3001112233','RP-1001',13,NULL,1),(2,'CC','1001001002','María','Rodríguez López','3001112234','RP-1002',2,NULL,1),(3,'CC','1001001003','Carlos','Martínez Ruiz','3001112235','RP-1003',3,35,1),(4,'CC','1001001004','Laura','Fernández Díaz','3001112236','RP-1004',4,NULL,1),(5,'CC','1001001005','Andrés','Ramírez Soto','3001112237','RP-1005',5,NULL,1),(6,'CC','1001001006','Diana','Gómez Torres','3001112238','RP-1006',6,NULL,1),(7,'CC','1001001007','Ricardo','Jiménez Vargas','3001112239','RP-1007',7,NULL,1),(8,'CC','1001001008','Patricia','Hernández Ríos','3001112240','RP-1008',8,NULL,1),(9,'CC','1001001009','Santiago','Morales Castro','3001112241','RP-1009',9,NULL,1),(10,'CC','1001001010','Natalia','García Mendoza','3001112242','RP-1010',9,NULL,1),(11,'CC','88274388','Giovanni','Mora','5711322','1234656789100',8,36,1),(12,'CC','12345678','Edson Arantes','Do nascimento','3207729661','RP-10011',12,NULL,1),(13,'CC','12345123','Giovanni','Mora Jaimes','3207729661','001RP1234',12,NULL,1);
/*!40000 ALTER TABLE `medico` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu` (
  `menu_id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(100) NOT NULL,
  `title` varchar(255) NOT NULL,
  `url` longtext NOT NULL,
  `icon` varchar(255) NOT NULL,
  `activo` tinyint(1) NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_modificacion` datetime DEFAULT NULL,
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
INSERT INTO `menu` VALUES (1,'cita','Gestión de citas','/inicio/citas','bi bi-calendar',1,'2026-01-01 00:00:00',NULL),(2,'clientes','Gestión de clientes','/inicio/clientes','bi bi-person-check',1,'2026-01-01 00:00:00',NULL),(3,'especializaciones','Gestión de Especializaciones','/inicio/especializaciones','fa-solid fa-user-graduate',1,'2026-01-01 00:00:00',NULL),(4,'formulas','Gestión de Formulas','/inicio/formulas','fa-solid fa-clipboard-list',1,'2026-01-01 00:00:00',NULL),(5,'historias','Gestión de Historias','/inicio/historias','fa-solid fa-book-medical',1,'2026-01-01 00:00:00',NULL),(6,'mascotas','Gestión de Mascotas','/inicio/mascotas','fa-solid fa-paw',1,'2026-01-01 00:00:00',NULL),(7,'medicamentos','Gestión de Medicamentos','/inicio/medicamentos','fa-solid fa-capsules',1,'2026-01-01 00:00:00',NULL),(8,'medicos','Gestión de Medicos','/inicio/medicos','fa-solid fa-user-doctor',1,'2026-01-01 00:00:00',NULL),(9,'usuarios','Gestión de Usuarios','/inicio/usuarios','feather icon-users',1,'2026-01-01 00:00:00',NULL),(10,'agenda','Mi agenda','/inicio/agenda','feather icon-calendar',1,'2026-01-01 00:00:00',NULL),(11,'tarifas','Tarifas y servicios','/inicio/tarifas','fa-solid fa-tags',1,'2026-01-01 00:00:00',NULL),(12,'facturas','Facturación','/inicio/facturas','feather icon-file-text',1,'2026-01-01 00:00:00',NULL),(13,'reportes','Reportes','/inicio/reportes','feather icon-bar-chart-2',1,'2026-01-01 00:00:00',NULL),(14,'despacho','Despacho de medicamentos','/inicio/despacho','feather icon-package',1,'2026-01-01 00:00:00',NULL);
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `movimiento_inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento_inventario` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `medicamento_id` bigint unsigned NOT NULL,
  `lote_id` bigint unsigned DEFAULT NULL,
  `tipo` varchar(20) NOT NULL,
  `cantidad` int NOT NULL,
  `saldo_lote` int NOT NULL,
  `fecha` datetime NOT NULL,
  `usuario_id` bigint unsigned DEFAULT NULL,
  `formula_id` bigint unsigned DEFAULT NULL,
  `despacho_id` bigint unsigned DEFAULT NULL,
  `observacion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_mov_medicamento` (`medicamento_id`),
  KEY `fk_mov_lote` (`lote_id`),
  KEY `fk_mov_usuario` (`usuario_id`),
  KEY `fk_mov_formula` (`formula_id`),
  KEY `fk_mov_despacho` (`despacho_id`),
  CONSTRAINT `fk_mov_despacho` FOREIGN KEY (`despacho_id`) REFERENCES `despacho` (`id`),
  CONSTRAINT `fk_mov_formula` FOREIGN KEY (`formula_id`) REFERENCES `formula_medica` (`id`),
  CONSTRAINT `fk_mov_lote` FOREIGN KEY (`lote_id`) REFERENCES `medicamento_lote` (`id`),
  CONSTRAINT `fk_mov_medicamento` FOREIGN KEY (`medicamento_id`) REFERENCES `medicamento` (`id`),
  CONSTRAINT `fk_mov_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`),
  CONSTRAINT `ck_mov_cantidad` CHECK ((`cantidad` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `movimiento_inventario` WRITE;
/*!40000 ALTER TABLE `movimiento_inventario` DISABLE KEYS */;
INSERT INTO `movimiento_inventario` VALUES (1,2,1,'ENTRADA',100,100,'2026-11-22 19:16:46',33,NULL,NULL,'Inventario inicial'),(2,2,2,'ENTRADA',30,30,'2026-11-22 19:16:46',33,NULL,NULL,'Inventario inicial'),(3,2,3,'ENTRADA',15,15,'2026-11-22 19:16:46',33,NULL,NULL,'Inventario inicial'),(4,5,4,'ENTRADA',8,8,'2026-11-22 19:16:46',33,NULL,NULL,'Inventario inicial'),(5,7,5,'ENTRADA',50,50,'2026-11-22 19:16:46',33,NULL,NULL,'Inventario inicial'),(6,8,6,'ENTRADA',3,3,'2026-11-22 19:16:46',33,NULL,NULL,'Inventario inicial'),(7,10,7,'ENTRADA',500,500,'2026-11-22 19:16:46',33,NULL,NULL,'Inventario inicial');
/*!40000 ALTER TABLE `movimiento_inventario` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `factura_id` bigint unsigned NOT NULL,
  `valor` decimal(12,2) NOT NULL,
  `metodo` varchar(30) NOT NULL,
  `referencia` varchar(100) DEFAULT NULL,
  `fecha` datetime NOT NULL,
  `usuario_id` bigint unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_pago_factura` (`factura_id`),
  KEY `fk_pago_usuario` (`usuario_id`),
  CONSTRAINT `fk_pago_factura` FOREIGN KEY (`factura_id`) REFERENCES `factura` (`id`),
  CONSTRAINT `fk_pago_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`),
  CONSTRAINT `ck_pago_valor` CHECK ((`valor` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `pago` WRITE;
/*!40000 ALTER TABLE `pago` DISABLE KEYS */;
/*!40000 ALTER TABLE `pago` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `perfil`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `perfil` (
  `perfil_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `activo` tinyint(1) NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_modificacion` datetime DEFAULT NULL,
  PRIMARY KEY (`perfil_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `perfil` WRITE;
/*!40000 ALTER TABLE `perfil` DISABLE KEYS */;
INSERT INTO `perfil` VALUES (1,'Administrador',1,'2026-01-01 00:00:00',NULL),(2,'Recepcion',1,'2026-01-01 00:00:00',NULL),(3,'Medico',1,'2026-01-01 00:00:00',NULL),(4,'DESPACHO_MEDICO',1,'2026-01-01 00:00:00',NULL);
/*!40000 ALTER TABLE `perfil` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `perfil_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `perfil_menu` (
  `perfil_menu_id` int NOT NULL AUTO_INCREMENT,
  `perfil_id` int NOT NULL,
  `menu_id` int NOT NULL,
  PRIMARY KEY (`perfil_menu_id`),
  UNIQUE KEY `uk_perfil_menu` (`perfil_id`,`menu_id`),
  KEY `fk_pm_menu` (`menu_id`),
  CONSTRAINT `fk_pm_menu` FOREIGN KEY (`menu_id`) REFERENCES `menu` (`menu_id`),
  CONSTRAINT `fk_pm_perfil` FOREIGN KEY (`perfil_id`) REFERENCES `perfil` (`perfil_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `perfil_menu` WRITE;
/*!40000 ALTER TABLE `perfil_menu` DISABLE KEYS */;
INSERT INTO `perfil_menu` VALUES (1,1,1),(7,1,2),(2,1,3),(3,1,6),(4,1,7),(5,1,8),(6,1,9),(8,1,11),(9,1,12),(10,1,13),(11,1,14),(12,2,1),(13,2,2),(14,2,6),(15,2,12),(18,3,4),(17,3,5),(16,3,10),(19,4,7),(20,4,14);
/*!40000 ALTER TABLE `perfil_menu` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `raza`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `raza` (
  `raza_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `especie` varchar(100) NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_modificacion` datetime DEFAULT NULL,
  PRIMARY KEY (`raza_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `raza` WRITE;
/*!40000 ALTER TABLE `raza` DISABLE KEYS */;
INSERT INTO `raza` VALUES (1,'Gato','Felino','2026-01-01 00:00:00','2026-01-01 00:00:00'),(2,'Perro','Canino','2026-01-01 00:00:00','2026-01-01 00:00:00');
/*!40000 ALTER TABLE `raza` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `tarifa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tarifa` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `codigo` varchar(20) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `tipo` varchar(20) NOT NULL,
  `especializacion_id` bigint unsigned DEFAULT NULL,
  `valor` decimal(12,2) NOT NULL,
  `iva_porcentaje` decimal(5,2) NOT NULL DEFAULT '0.00',
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_creacion` datetime NOT NULL,
  `fecha_modificacion` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tarifa_codigo` (`codigo`),
  KEY `fk_tarifa_especializacion` (`especializacion_id`),
  CONSTRAINT `fk_tarifa_especializacion` FOREIGN KEY (`especializacion_id`) REFERENCES `especializacion` (`id`),
  CONSTRAINT `ck_tarifa_iva` CHECK ((`iva_porcentaje` between 0 and 100)),
  CONSTRAINT `ck_tarifa_valor` CHECK ((`valor` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `tarifa` WRITE;
/*!40000 ALTER TABLE `tarifa` DISABLE KEYS */;
INSERT INTO `tarifa` VALUES (1,'CONS-012','Consulta de Anestesiología','CONSULTA',12,60000.00,0.00,1,'2026-11-22 19:16:11',NULL),(2,'CONS-001','Consulta de Cardiología 2','CONSULTA',1,60000.00,0.00,1,'2026-11-22 19:16:11',NULL),(3,'CONS-005','Consulta de Dermatología','CONSULTA',5,60000.00,0.00,1,'2026-11-22 19:16:11',NULL),(4,'CONS-014','Consulta de Endocrinología','CONSULTA',14,60000.00,0.00,1,'2026-11-22 19:16:11',NULL),(5,'CONS-015','Consulta de Gastroenterología','CONSULTA',15,60000.00,0.00,1,'2026-11-22 19:16:11',NULL),(6,'CONS-006','Consulta de Ginecología','CONSULTA',6,60000.00,0.00,1,'2026-11-22 19:16:11',NULL),(7,'CONS-002','Consulta de Neurología','CONSULTA',2,60000.00,0.00,1,'2026-11-22 19:16:11',NULL),(8,'CONS-009','Consulta de Oftalmología','CONSULTA',9,60000.00,0.00,1,'2026-11-22 19:16:11',NULL),(9,'CONS-004','Consulta de Oncología','CONSULTA',4,60000.00,0.00,1,'2026-11-22 19:16:11',NULL),(10,'CONS-010','Consulta de Otorrinolaringología','CONSULTA',10,60000.00,0.00,1,'2026-11-22 19:16:11',NULL),(11,'CONS-003','Consulta de Pediatría','CONSULTA',3,60000.00,0.00,1,'2026-11-22 19:16:11',NULL),(12,'CONS-008','Consulta de Psiquiatría','CONSULTA',8,60000.00,0.00,1,'2026-11-22 19:16:11',NULL),(13,'CONS-013','Consulta de Reumatología','CONSULTA',13,60000.00,0.00,1,'2026-11-22 19:16:11',NULL),(14,'CONS-011','Consulta de Traumatología','CONSULTA',11,60000.00,0.00,1,'2026-11-22 19:16:11',NULL),(15,'CONS-007','Consulta de Urología','CONSULTA',7,60000.00,0.00,1,'2026-11-22 19:16:11',NULL),(16,'CONS-GEN','Consulta general','CONSULTA',NULL,50000.00,0.00,1,'2026-11-22 19:16:11',NULL),(17,'PROC-VAC','Aplicación de vacuna','PROCEDIMIENTO',NULL,25000.00,19.00,1,'2026-11-22 19:16:11',NULL),(18,'PROC-CUR','Curación de heridas','PROCEDIMIENTO',NULL,35000.00,19.00,1,'2026-11-22 19:16:11',NULL),(19,'SERV-BAN','Baño medicado','SERVICIO',NULL,40000.00,19.00,1,'2026-11-22 19:16:11',NULL);
/*!40000 ALTER TABLE `tarifa` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `tarifa_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tarifa_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `tarifa_id` bigint unsigned NOT NULL,
  `accion` varchar(20) NOT NULL,
  `valor_anterior` decimal(12,2) DEFAULT NULL,
  `valor_nuevo` decimal(12,2) DEFAULT NULL,
  `iva_anterior` decimal(5,2) DEFAULT NULL,
  `iva_nuevo` decimal(5,2) DEFAULT NULL,
  `usuario_id` bigint unsigned DEFAULT NULL,
  `fecha` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_tlog_tarifa` (`tarifa_id`),
  KEY `fk_tlog_usuario` (`usuario_id`),
  CONSTRAINT `fk_tlog_tarifa` FOREIGN KEY (`tarifa_id`) REFERENCES `tarifa` (`id`),
  CONSTRAINT `fk_tlog_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `tarifa_log` WRITE;
/*!40000 ALTER TABLE `tarifa_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `tarifa_log` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` text,
  `rol` int NOT NULL,
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  `activo` tinyint(1) DEFAULT '1',
  `email` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `fk_usuario_perfil` (`rol`),
  CONSTRAINT `fk_usuario_perfil` FOREIGN KEY (`rol`) REFERENCES `perfil` (`perfil_id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (29,'gmora','8f8439bd01556aace5fd2b93ea927958',1,'2025-11-06 20:59:54',1,'giovanimora0527@gmail.com'),(33,'admin','135eaf67abf0dd98b92596d4193d4068',1,'2026-11-22 19:16:11',1,'admin@clinica.test'),(34,'recepcion','135eaf67abf0dd98b92596d4193d4068',2,'2026-11-22 19:16:11',1,'recepcion@clinica.test'),(35,'medico','135eaf67abf0dd98b92596d4193d4068',3,'2026-11-22 19:16:11',1,'medico@clinica.test'),(36,'medico2','135eaf67abf0dd98b92596d4193d4068',3,'2026-11-22 19:16:11',1,'medico2@clinica.test'),(37,'despacho','135eaf67abf0dd98b92596d4193d4068',4,'2026-11-22 19:16:11',1,'despacho@clinica.test');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS `session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `session` (
  `session_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `token` varchar(500) NOT NULL,
  `fecha_ini_sesion` datetime DEFAULT NULL,
  `fecha_expiracion` datetime DEFAULT NULL,
  PRIMARY KEY (`session_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `otp_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `otp_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint NOT NULL,
  `codigo` varchar(6) NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_expiracion` datetime NOT NULL,
  `intentos` int NOT NULL,
  `usado` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

