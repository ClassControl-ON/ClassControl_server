package Servlet;

import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Listado exclusivo de instructores para el módulo "Instructores".
 *
 * Hereda de ConsultarUsuarios para reutilizar exactamente el mismo
 * serializador JSON (camposJson), pero la fuente de datos es el DAO
 * filtrado en SQL por rol Instructor. Así el módulo Usuarios conserva
 * su catálogo completo y este endpoint nunca devuelve otros perfiles.
 */
@WebServlet("/ConsultarInstructores")
public class ConsultarInstructores extends ConsultarUsuarios {

    @Override
    protected String getTipo() { return "instructores"; }

    @Override
    protected List<Usuarios> obtenerLista() {
        return new UsuariosDAO().listarInstructores();
    }

    @Override
    protected List<Usuarios> obtenerLista(HttpServletRequest request) {
        return obtenerLista();
    }
}
