/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Estado;
import Controlador.EstadoDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarEstado {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            EstadoDAO estadoDAO = new EstadoDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Estado rol = estadoDAO.consultaEstado(idRol);
            

            if (rol != null) {
                System.out.println("=== Estado encontrado ===");
                System.out.println("ID: " + rol.getId_estado());
                System.out.println("Descripcion del estado: " + rol.getDescripcion_Estado());
                System.out.println("ID del tipo de estado: " + rol.getTipo_Estado_id_tipo_estado());
            } else {
                System.out.println("No se encontró el rol con ID: " + idRol);
            }
        }
    
}