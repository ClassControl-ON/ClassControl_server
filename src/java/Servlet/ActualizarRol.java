package Servlet;

import Controlador.RolesDAO;
import Modelo.Roles;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarRol")
public class ActualizarRol extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "rol"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        Roles rol = new Roles();
        rol.setId_roles(entero(request, "id"));
        rol.setDescripcion_Roles(texto(request, "descripcion_Roles"));
        return new RolesDAO().actualizarRoles(rol);
    }
}
