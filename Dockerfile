# Imagen base con Tomcat 10.1 (necesario por el namespace jakarta.servlet) y Java 17
FROM tomcat:10.1-jdk17

# Limpia la webapp de ejemplo que trae Tomcat por defecto
RUN rm -rf /usr/local/tomcat/webapps/*

# Copia tu WAR compilado y lo despliega como app raíz (ROOT), así la URL
# queda limpia: https://tuapp.up.railway.app/  en vez de /ClassControl/
COPY dist/ClassControl.war /usr/local/tomcat/webapps/ROOT.war

# Railway asigna el puerto dinámicamente en la variable de entorno PORT.
# Tomcat por defecto escucha en 8080; este script ajusta el puerto de arranque
# al que Railway indique.
COPY railway-entrypoint.sh /railway-entrypoint.sh
RUN chmod +x /railway-entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["/railway-entrypoint.sh"]
