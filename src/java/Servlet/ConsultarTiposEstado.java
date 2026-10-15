package Servlet;

import Controlador.Tipo_EstadoDAO;
import Modelo.Tipo_Estado;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarTiposEstado")
public class ConsultarTiposEstado extends ConsultarBaseServlet<Tipo_Estado> {

    @Override
    protected String getTipo() { return "tiposEstado"; }

    @Override
    protected List<Tipo_Estado> obtenerLista() {
        return new Tipo_EstadoDAO().listarTiposEstado();
    }

    @Override
    protected String camposJson(Tipo_Estado t) {
        return campoNum("id", t.getId_tipo_estado()) + ","
             + campoStr("descripcion", t.getDescripcion());
    }
}
