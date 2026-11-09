# Imagen base con Tomcat 10.1 (necesario por el namespace jakarta.servlet) y Java 17
FROM tomcat:10.1-jdk17

# Crea un usuario sin privilegios para ejecutar Tomcat (evita correr como root)
RUN groupadd --system tomcat \
    && useradd --system --gid tomcat --no-create-home \
       --home-dir /usr/local/tomcat --shell /usr/sbin/nologin tomcat

# Limpia la webapp de ejemplo que trae Tomcat por defecto
RUN rm -rf /usr/local/tomcat/webapps/*

# Copia tu WAR compilado y lo despliega como app raíz (ROOT), así la URL
# queda limpia: https://tuapp.up.railway.app/  en vez de /ClassControl/
COPY --chown=tomcat:tomcat dist/ClassControl.war /usr/local/tomcat/webapps/ROOT.war

# Railway asigna el puerto dinámicamente en la variable de entorno PORT.
# Tomcat por defecto escucha en 8080; este script ajusta el puerto de arranque
# al que Railway indique.
COPY --chown=tomcat:tomcat railway-entrypoint.sh /railway-entrypoint.sh
RUN chmod +x /railway-entrypoint.sh \
    && chown -R tomcat:tomcat /usr/local/tomcat

# Ejecuta el contenedor con el usuario sin privilegios
USER tomcat

EXPOSE 8080
ENTRYPOINT ["/railway-entrypoint.sh"]
