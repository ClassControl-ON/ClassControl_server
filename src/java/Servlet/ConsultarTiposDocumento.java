package Servlet;

import Controlador.Tipo_DocumentoDAO;
import Modelo.Tipo_Documento;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarTiposDocumento")
public class ConsultarTiposDocumento extends ConsultarBaseServlet<Tipo_Documento> {

    @Override
    protected String getTipo() { return "tiposDocumento"; }

    @Override
    protected List<Tipo_Documento> obtenerLista() {
        return new Tipo_DocumentoDAO().listarTiposDocumento();
    }

    @Override
    protected String camposJson(Tipo_Documento t) {
        return campoNum("id", t.getId_tipo_Documento()) + ","
             + campoStr("descripcion", t.getDescripcion_Tipo_Doc());
    }
}
