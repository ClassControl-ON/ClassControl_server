/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Tipo_Estado;
import Controlador.Tipo_EstadoDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarTipo_Estado {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            Tipo_EstadoDAO tipo_EstadoDAO = new Tipo_EstadoDAO();

            // ID del tipo de estado que quieres consultar
            int idRol = 1;

            Tipo_Estado rol = tipo_EstadoDAO.consultaTipo_Estado(idRol);
            

            if (rol != null) {
                System.out.println("=== Tipo de estado encontrado ===");
                System.out.println("ID: " + rol.getId_tipo_estado());
                System.out.println("Descripcion del tipo de estado: " + rol.getDescripcion());
            } else {
                System.out.println("No se encontró el tipo de estado con ID: " + idRol);
            }
        }
    
}