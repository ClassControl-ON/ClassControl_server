package Servlet;

import Controlador.SedeDAO;
import Modelo.Sede;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarSedes")
public class ConsultarSedes extends ConsultarBaseServlet<Sede> {

    @Override
    protected String getTipo() { return "sedes"; }

    @Override
    protected List<Sede> obtenerLista() {
        return new SedeDAO().listarSedes();
    }

    @Override
    protected String camposJson(Sede s) {
        return campoNum("id", s.getId_sede()) + ","
             + campoStr("nombre", s.getNombre_sede());
    }
}
