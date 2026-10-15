/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PruebaConsultar;

import Modelo.Usuarios;
import Controlador.UsuariosDAO;
import java.sql.SQLException;


/**
 *
 * @author Aprendiz
 */
public class PruebaConsultarUsuarios {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

            UsuariosDAO usuariosDAO = new UsuariosDAO();

            // ID del rol que quieres consultar
            int idRol = 1;

            Usuarios rol = usuariosDAO.consultaUsuarios(idRol);
            

            if (rol != null) {
                System.out.println("=== Usuario encontrado ===");
                System.out.println("ID del usuario: " + rol.getId_usuarios());
                System.out.println("Nombres: " + rol.getNombres());
                System.out.println("Apellidos: " + rol.getApellidos());
                System.out.println("Identificacion: " + rol.getIdentificacion());
                System.out.println("Correo: " + rol.getCorreo());
                System.out.println("Telefono: " + rol.getTelefono());
                System.out.println("Direccion: " + rol.getDireccion());
                System.out.println("Username: " + rol.getUsername());
                System.out.println("Nivel Educativo: " + rol.getNivel_Educativo());
                System.out.println("Profesion: " + rol.getProfesion());
                System.out.println("Clave: " + rol.getClave());
                System.out.println("Activo: " + rol.isActivo());
                System.out.println("ID del rol: " + rol.getRoles_id_roles());
                System.out.println("ID del tipo de documento: " + rol.getTipo_Documento_id_tipo_Documento());
                System.out.println("ID del tipo de vinculacion: " + rol.getTipo_vinculacion_id_tipo_vinculacion());
                
            } else {
                System.out.println("No se encontró el usuario con ID: " + idRol);
            }
        }
    
}