package Servlet;

import Controlador.EstadoDAO;
import Modelo.Estado;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarEstados")
public class ConsultarEstados extends ConsultarBaseServlet<Estado> {

    @Override
    protected String getTipo() { return "estados"; }

    @Override
    protected List<Estado> obtenerLista() {
        return new EstadoDAO().listarEstados();
    }

    @Override
    protected String camposJson(Estado e) {
        return campoNum("id", e.getId_estado()) + ","
             + campoStr("descripcion", e.getDescripcion_Estado());
    }
}
