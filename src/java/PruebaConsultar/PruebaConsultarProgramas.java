/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Programas;
import Controlador.ProgramasDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarProgramas {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            ProgramasDAO programasDAO = new ProgramasDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Programas rol = programasDAO.consultaProgramas(idRol);
            

            if (rol != null) {
                System.out.println("=== Programa encontrado ===");
                System.out.println("ID: " + rol.getIdProgramas());
                System.out.println("Codigo del programa: " + rol.getCodigo_programa());
                System.out.println("Nombre del programa: " + rol.getNombre_programa());
            } else {
                System.out.println("No se encontró el programa con ID: " + idRol);
            }
        }
    
}