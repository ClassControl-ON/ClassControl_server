/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Sede;
import Controlador.SedeDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarSede{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            SedeDAO sedeDAO = new SedeDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Sede rol = sedeDAO.consultaSede(idRol);
            

            if (rol != null) {
                System.out.println("=== Sede encontrada ===");
                System.out.println("ID: " + rol.getId_sede());
                System.out.println("Nombre de la sede: " + rol.getNombre_sede());
            } else {
                System.out.println("No se encontró la sede con ID: " + idRol);
            }
        }
    
}