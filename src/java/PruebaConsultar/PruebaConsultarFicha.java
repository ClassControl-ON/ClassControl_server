/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Ficha;
import Controlador.FichaDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarFicha {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            FichaDAO fichaDAO = new FichaDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Ficha rol = fichaDAO.consultaFicha(idRol);
            

            if (rol != null) {
                System.out.println("=== Ficha encontrada ===");
                System.out.println("ID: " + rol.getId_ficha());
                System.out.println("Codigo de la ficha: " + rol.getCodigo_ficha());
                System.out.println("Fecha de inicio de la ficha: " + rol.getFecha_inicio());
                System.out.println("Fecha de finalizacion de la ficha: " + rol.getFecha_fin());
                System.out.println("Cantidad de aprendicez de la ficha: " + rol.getCantidad_aprendices());
                System.out.println("ID del programa:  " + rol.getProgramas_idProgramas());
                System.out.println("ID de la jornada: " + rol.getJornada_id_jornada());
                System.out.println("ID de la modalidad: " + rol.getModalidad_id_modalidad());
                System.out.println("ID del nivel de formacion: " + rol.getNivel_formacion_id_nivel_formacion());
                System.out.println("ID de la sede: " + rol.getSede_id_sede());
                System.out.println("ID del estado: " + rol.getEstado_id_estado());
                System.out.println("ID de la etapa: " + rol.getEtapa_id_etapa());
            } else {
                System.out.println("No se encontró la ficha con ID: " + idRol);
            }
        }
    
}