<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // Invalida la sesión de verdad (antes solo se redirigía sin cerrar nada)
    session.invalidate();
    response.sendRedirect("Inicio_de_sesion.jsp");
%>
