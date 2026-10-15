package Servlet;

import Controlador.EtapaDAO;
import Modelo.Etapa;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarEtapas")
public class ConsultarEtapas extends ConsultarBaseServlet<Etapa> {

    @Override
    protected String getTipo() { return "etapas"; }

    @Override
    protected List<Etapa> obtenerLista() {
        return new EtapaDAO().listarEtapas();
    }

    @Override
    protected String camposJson(Etapa e) {
        return campoNum("id", e.getId_etapa()) + ","
             + campoStr("descripcion", e.getDescripcion_Etapa());
    }
}
