package Servlet;

import Controlador.Tipo_vinculacionDAO;
import Modelo.Tipo_vinculacion;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarTiposVinculacion")
public class ConsultarTiposVinculacion extends ConsultarBaseServlet<Tipo_vinculacion> {

    @Override
    protected String getTipo() { return "tiposVinculacion"; }

    @Override
    protected List<Tipo_vinculacion> obtenerLista() {
        return new Tipo_vinculacionDAO().listarTiposVinculacion();
    }

    @Override
    protected String camposJson(Tipo_vinculacion t) {
        return campoNum("id", t.getId_tipo_vinculacion()) + ","
             + campoStr("descripcion", t.getDescripcion_vinculacion());
    }
}
