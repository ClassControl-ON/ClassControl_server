/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Tipo_vinculacion;
import Controlador.Tipo_vinculacionDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarTipo_vinculacion {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            Tipo_vinculacionDAO tipo_vinculacionDAO = new Tipo_vinculacionDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Tipo_vinculacion rol = tipo_vinculacionDAO.consultaTipo_vinculacion(idRol);
            

            if (rol != null) {
                System.out.println("=== Tipo de vinculacion encontrado ===");
                System.out.println("ID: " + rol.getId_tipo_vinculacion());
                System.out.println("Descripcion del tipo de vinculacion: " + rol.getDescripcion_vinculacion());
            } else {
                System.out.println("No se encontró el tipo de vinculacion con ID: " + idRol);
            }
        }
    
}