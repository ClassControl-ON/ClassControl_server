/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Modalidad;
import Controlador.ModalidadDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarModalidad {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            ModalidadDAO modalidadDAO = new ModalidadDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Modalidad rol = modalidadDAO.consultaModalidad(idRol);
            

            if (rol != null) {
                System.out.println("=== Modalidad encontrada ===");
                System.out.println("ID: " + rol.getId_modalidad());
                System.out.println("Descripcion de la modalidad: " + rol.getDescripcion_Modalidad());
            } else {
                System.out.println("No se encontró el rol con ID: " + idRol);
            }
        }
    
}