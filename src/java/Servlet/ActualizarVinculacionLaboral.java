package Servlet;

import Controlador.VinculacionLaboralDAO;
import Modelo.VinculacionLaboral;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/ActualizarVinculacionLaboral")
public class ActualizarVinculacionLaboral extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "vinculacionLaboral"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        VinculacionLaboral vinculacion = new VinculacionLaboral();
        vinculacion.setId_vinculacion_Laboral(entero(request, "id"));
        vinculacion.setDescripcion(texto(request, "descripcion"));
        vinculacion.setNumero_Contrato(texto(request, "numero_Contrato"));
        vinculacion.setFecha_Inicio(fecha(request, "fecha_Inicio"));
        vinculacion.setFecha_Fin(fecha(request, "fecha_Fin"));
        vinculacion.setUsuarios_id_usuarios(entero(request, "Usuarios_id_usuarios"));
        return new VinculacionLaboralDAO().actualizarVinculacionLaboral(vinculacion);
    }
}
