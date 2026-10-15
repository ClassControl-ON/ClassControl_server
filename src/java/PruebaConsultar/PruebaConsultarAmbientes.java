/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Ambientes;
import Controlador.AmbientesDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarAmbientes {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            AmbientesDAO ambientesDAO = new AmbientesDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Ambientes rol = ambientesDAO.consultaAmbientes(idRol);
            

            if (rol != null) {
                System.out.println("=== Ambiente encontrado ===");
                System.out.println("ID: " + rol.getId_ambientes());
                System.out.println("Descripcion del ambiente: " + rol.getDescripcion_Ambiente());
                System.out.println("Capacidad: " + rol.getCapacidad());
                System.out.println("ID de la sede: " + rol.getSede_id_sede());
            } else {
                System.out.println("No se encontró el ambiente con ID: " + idRol);
            }
        }
    
}