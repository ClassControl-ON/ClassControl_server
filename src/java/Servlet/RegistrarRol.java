package Servlet;

import Controlador.RolesDAO;
import Modelo.Roles;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarRol")
public class RegistrarRol extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "rol"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Roles rol = new Roles();
        rol.setId_roles(0);
        rol.setDescripcion_Roles(texto(request, "descripcion_Roles"));
        return new RolesDAO().insertarRoles(rol);
    }
}
