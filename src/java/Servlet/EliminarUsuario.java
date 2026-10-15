package Servlet;

import Controlador.UsuariosDAO;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/EliminarUsuario")
public class EliminarUsuario extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "usuario"; }

    @Override
    protected boolean eliminar(int id) {
        return new UsuariosDAO().eliminarUsuarios(id);
    }
}
