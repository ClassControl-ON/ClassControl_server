/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Competencias;
import Controlador.CompetenciasDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarCompetencias{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            CompetenciasDAO competenciasDAO = new CompetenciasDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Competencias rol = competenciasDAO.consultaCompetencias(idRol);
            

            if (rol != null) {
                System.out.println("=== Sede encontrada ===");
                System.out.println("ID: " + rol.getId_competencias());
                System.out.println("Codigo de la competencia: " + rol.getCodigo_Competencias());
                System.out.println("Descripcion de la competencia: " + rol.getDescripcion_Competencias());
                System.out.println("ID de la programacion de instructores: " + rol.getProgramas_idProgramas());
            } else {
                System.out.println("No se encontró la competencia con ID: " + idRol);
            }
        }
    
}