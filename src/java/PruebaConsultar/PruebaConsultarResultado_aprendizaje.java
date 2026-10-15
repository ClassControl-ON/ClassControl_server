/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Resultado_aprendizaje;
import Controlador.Resultado_aprendizajeDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarResultado_aprendizaje {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            Resultado_aprendizajeDAO resultado_aprendizajeDAO = new Resultado_aprendizajeDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Resultado_aprendizaje rol = resultado_aprendizajeDAO.consultaResultado_aprendizaje(idRol);
            

            if (rol != null) {
                System.out.println("=== Resultado de aprendizaje encontrado ===");
                System.out.println("ID: " + rol.getId_resultado_aprendizaje());
                System.out.println("Codigo del resultado de aprendizaje: " + rol.getCodigo_ResultadoAp());
                System.out.println("Descripcion del resultado de aprendizaje: " + rol.getDescripcion_Resul());
                System.out.println("ID de la competencia: " + rol.getCompetencias_id_competencias());
            } else {
                System.out.println("No se encontró el resultado de aprendizaje con ID: " + idRol);
            }
        }
    
}