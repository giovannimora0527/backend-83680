# ENTREGA - BACKEND CLINICA VETERINARIA

## Alcance
Implementación del backend solicitado en el Primer Parcial de Programación Web.

## Tecnologías
- Java 21
- Spring Boot 4.0.4
- Spring Web
- Spring Data JPA
- MySQL
- Maven

## Configuración
Editar:
src/main/resources/application-dev.properties

Cambiar usuario y contraseña de MySQL según la instalación local.

## Base de datos
La aplicación espera una base llamada `clinica`.
Las tablas deben ser las entregadas por el docente.

## Ejecución
1. Abrir como proyecto Maven en NetBeans.
2. Ejecutar `ClinicaApplication`.
3. Verificar que el servidor quede en el puerto 8080.

URL base:
http://localhost:8080/clinica/v1

## Servicios

### Punto 1
GET /formulas-medicas

URL completa:
http://localhost:8080/clinica/v1/formulas-medicas

### Punto 2 / 3
GET /citas?fechaInicial=YYYY-MM-DD&fechaFinal=YYYY-MM-DD

### Punto 4
POST /citas
PUT /citas/{id}

### Punto 5
GET /anotaciones-historia
GET /anotaciones-historia/rango?fechaInicial=YYYY-MM-DD&fechaFinal=YYYY-MM-DD
POST /anotaciones-historia
PUT /anotaciones-historia/{id}

## Antes de entregar
1. Verificar Java 21.
2. Verificar MySQL.
3. Verificar la base `clinica`.
4. Cambiar contraseña de MySQL si corresponde.
5. Ejecutar:
   mvn clean package
6. Confirmar `BUILD SUCCESS`.
7. Probar los endpoints.
8. Crear branch con formato `codigo_nombre`.
9. Crear commits descriptivos.
10. Hacer push.
11. Entregar el ZIP individual.

## Nota
Los puntos 2 y 3 del enunciado contienen el mismo requerimiento de filtro de citas. Se implementó una sola operación para evitar duplicación innecesaria.
