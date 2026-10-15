/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Programacion_Instructores;
import Controlador.Programacion_InstructoresDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarProgramacion_Instructores {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            Programacion_InstructoresDAO programacion_InstructoresDAO = new Programacion_InstructoresDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Programacion_Instructores rol = programacion_InstructoresDAO.consultaProgramacion_Instructores(idRol);
            

            if (rol != null) {
                System.out.println("=== Programacion de instructores encontrado ===");
                System.out.println("ID: " + rol.getId_programacion_Instructores());
                System.out.println("Observacion de la programacion de instructores: " + rol.getObservaciones());
                System.out.println("Fecha de inicio de la programacion de instructores: " + rol.getFecha_inicial_Prog());
                System.out.println("Fecha de finalizacion de la programacion de instructores: " + rol.getFecha_fin_Prog());
                System.out.println("Dia de la semana de la programacion de instructores: " + rol.getDias_Semana());
                System.out.println("Hora de inicio de la programacion de instructores: " + rol.getHora_inicio());
                System.out.println("Hora de finalizacion de la programacion de instructores: " + rol.getHora_fin());
                System.out.println("ID de la ficha: " + rol.getFicha_id_ficha());
                System.out.println("ID del usuario: " + rol.getUsuarios_id_usuarios());
                System.out.println("ID del ambiente: " + rol.getAmbientes_id_ambientes());
                System.out.println("ID del trimestre: " + rol.getTrimestre_id_trimestre());
                System.out.println("ID del estado: " + rol.getEstado_id_estado());
            } else {
                System.out.println("No se encontró la programacion de instructores con ID: " + idRol);
            }
        }
    
}