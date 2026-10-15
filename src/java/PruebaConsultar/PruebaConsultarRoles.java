/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Roles;
import Controlador.RolesDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarRoles {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            RolesDAO rolesDAO = new RolesDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Roles rol = rolesDAO.consultaRoles(idRol);
            

            if (rol != null) {
                System.out.println("=== Rol encontrado ===");
                System.out.println("ID: " + rol.getId_roles());
                System.out.println("Descripcion del rol: " + rol.getDescripcion_Roles());
            } else {
                System.out.println("No se encontró el rol con ID: " + idRol);
            }
        }
    
}