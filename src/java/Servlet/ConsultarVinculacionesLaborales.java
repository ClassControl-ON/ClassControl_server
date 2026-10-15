package Servlet;

import Controlador.VinculacionLaboralDAO;
import Modelo.VinculacionLaboral;
import jakarta.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet("/ConsultarVinculacionesLaborales")
public class ConsultarVinculacionesLaborales extends ConsultarBaseServlet<VinculacionLaboral> {

    @Override
    protected String getTipo() { return "vinculacionesLaborales"; }

    @Override
    protected List<VinculacionLaboral> obtenerLista() {
        return new VinculacionLaboralDAO().listarVinculacionesLaborales();
    }

    @Override
    protected String camposJson(VinculacionLaboral v) {
        return campoNum("id", v.getId_vinculacion_Laboral()) + ","
             + campoStr("descripcion", v.getDescripcion()) + ","
             + campoStr("numeroContrato", v.getNumero_Contrato()) + ","
             + campoFecha("fechaInicio", v.getFecha_Inicio()) + ","
             + campoFecha("fechaFin", v.getFecha_Fin()) + ","
             + campoNum("usuarioId", v.getUsuarios_id_usuarios());
    }
}
