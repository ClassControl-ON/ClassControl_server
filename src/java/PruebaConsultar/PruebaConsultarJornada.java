/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Jornada;
import Controlador.JornadaDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarJornada {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            JornadaDAO jornadaDAO = new JornadaDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Jornada rol = jornadaDAO.consultaJornada(idRol);
            

            if (rol != null) {
                System.out.println("=== Jornada encontrada ===");
                System.out.println("ID: " + rol.getId_jornada());
                System.out.println("Descripcion de la jornada: " + rol.getDescripcion_Jornada());
            } else {
                System.out.println("No se encontró el rol con ID: " + idRol);
            }
        }
    
}