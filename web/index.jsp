<%-- 
    Document   : index.jsp
    Redirige de forma invisible (server-side forward) a la pagina de login
    real con estilos, evitando el "flash" de contenido sin CSS que producia
    el antiguo meta-refresh.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:forward page="Inicio_de_sesion.jsp"/>
