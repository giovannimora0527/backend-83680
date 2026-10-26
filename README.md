# backend2026-80869
Backend para una clinica veterinaria

## Configuracion JWT

Antes de iniciar la aplicacion, define la variable de entorno `JWT_SECRET` con
una clave aleatoria de al menos 64 bytes codificada en Base64 para la firma HS512.
Conserva la misma clave entre reinicios e instancias y no la guardes en el repositorio.
Los tokens generados contienen el identificador del usuario y expiran en una hora.
