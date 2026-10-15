package Servlet;

import Controlador.AmbientesDAO;
import Modelo.Ambientes;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarAmbientes")
public class ConsultarAmbientes extends ConsultarBaseServlet<Ambientes> {

    @Override
    protected String getTipo() { return "ambientes"; }

    @Override
    protected List<Ambientes> obtenerLista() {
        return new AmbientesDAO().listarAmbientes();
    }

    @Override
    protected String camposJson(Ambientes a) {
        return campoNum("id", a.getId_ambientes()) + ","
             + campoStr("descripcion", a.getDescripcion_Ambiente()) + ","
             + campoNum("capacidad", a.getCapacidad()) + ","
             + campoNum("sedeId", a.getSede_id_sede()) + ","
             + campoStr("estado", a.getEstado_Ambiente());
    }
}
