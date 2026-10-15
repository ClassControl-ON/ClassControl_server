#!/bin/sh
# Railway inyecta el puerto en $PORT; si no existe (pruebas locales), usa 8080.
PORT="${PORT:-8080}"

# Reemplaza el puerto por defecto de Tomcat (8080) en server.xml por el de Railway
sed -i "s/port=\"8080\"/port=\"${PORT}\"/" /usr/local/tomcat/conf/server.xml

exec catalina.sh run
