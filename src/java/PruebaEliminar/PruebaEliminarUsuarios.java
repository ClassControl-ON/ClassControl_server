package PruebaEliminar;

import java.util.Scanner;
import Controlador.UsuariosDAO;

public class PruebaEliminarUsuarios {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UsuariosDAO usuariosDAO = new UsuariosDAO();
        
        System.out.println("Ingrese el ID del usuario que desea eliminar: ");
        int Id_usuarios = sc.nextInt();
        
        boolean eliminado = usuariosDAO.eliminarUsuarios(Id_usuarios);
        
        if(eliminado) {
            System.out.println("\nEl usuario con ID: " + Id_usuarios + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar el usuario. Puede que no exista");
        }
        
        sc.close();
    }
}
