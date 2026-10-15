package Servlet;

import Controlador.FichaDAO;
import Modelo.Ficha;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/RegistrarFicha")
public class RegistrarFicha extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "ficha"; }

    @Override
    protected boolean guardar(HttpServletRequest request) {
        Ficha ficha = new Ficha();
        ficha.setId_ficha(0);
        ficha.setCodigo_ficha(entero(request, "codigo_ficha"));
        ficha.setProgramas_idProgramas(entero(request, "Programas_idProgramas"));
        ficha.setJornada_id_jornada(entero(request, "Jornada_id_jornada"));
        ficha.setModalidad_id_modalidad(entero(request, "Modalidad_id_modalidad"));
        ficha.setNivel_formacion_id_nivel_formacion(entero(request, "Nivel_formacion_id_nivel_formacion"));
        ficha.setSede_id_sede(entero(request, "Sede_id_sede"));
        ficha.setEstado_id_estado(entero(request, "Estado_id_estado"));
        ficha.setEtapa_id_etapa(entero(request, "Etapa_id_etapa"));
        ficha.setCantidad_aprendices(enteroOpcional(request, "cantidad_aprendices", 0));
        ficha.setFecha_inicio(fechaOpcional(request, "fecha_inicio"));
        ficha.setFecha_fin(fechaOpcional(request, "fecha_fin"));
        return new FichaDAO().insertarFicha(ficha);
    }
}
