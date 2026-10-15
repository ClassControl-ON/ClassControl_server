package PruebaEliminar;

import java.util.Scanner;
import Controlador.AmbientesDAO;

public class PruebaEliminarAmbientes {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AmbientesDAO ambientesDAO = new AmbientesDAO();
        
        System.out.println("Ingrese el ID del ambiente que desea eliminar: ");
        int Id_ambientes = sc.nextInt();
        
        boolean eliminado = ambientesDAO.eliminarAmbientes(Id_ambientes);
        
        if(eliminado) {
            System.out.println("\nEl ambiente con ID: " + Id_ambientes + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar el ambiente. Puede que no exista");
        }
        
        sc.close();
    }
}
