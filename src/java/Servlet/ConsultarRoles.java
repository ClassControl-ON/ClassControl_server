package Servlet;

import Controlador.RolesDAO;
import Modelo.Roles;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarRoles")
public class ConsultarRoles extends ConsultarBaseServlet<Roles> {

    @Override
    protected String getTipo() { return "roles"; }

    @Override
    protected List<Roles> obtenerLista() {
        return new RolesDAO().listarRoles();
    }

    @Override
    protected String camposJson(Roles r) {
        return campoNum("id", r.getId_roles()) + ","
             + campoStr("descripcion", r.getDescripcion_Roles());
    }
}
