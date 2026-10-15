package Servlet;

import Controlador.ReportesDAO;
import jakarta.servlet.annotation.WebServlet;

/** Elimina un reporte de incidencia (solo Administrador). */
@WebServlet("/EliminarReporte")
public class EliminarReporte extends EliminarBaseServlet {

    @Override
    protected String getTipo() { return "reporte"; }

    @Override
    protected boolean eliminar(int id) {
        return new ReportesDAO().eliminar(id);
    }
}
