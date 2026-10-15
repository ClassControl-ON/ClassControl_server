package Servlet;

import Controlador.ModalidadDAO;
import Modelo.Modalidad;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarModalidades")
public class ConsultarModalidades extends ConsultarBaseServlet<Modalidad> {

    @Override
    protected String getTipo() { return "modalidades"; }

    @Override
    protected List<Modalidad> obtenerLista() {
        return new ModalidadDAO().listarModalidades();
    }

    @Override
    protected String camposJson(Modalidad m) {
        return campoNum("id", m.getId_modalidad()) + ","
             + campoStr("descripcion", m.getDescripcion_Modalidad());
    }
}
