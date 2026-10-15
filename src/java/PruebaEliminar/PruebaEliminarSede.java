package PruebaEliminar;

import java.util.Scanner;
import Controlador.SedeDAO;

public class PruebaEliminarSede {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SedeDAO sedeDAO = new SedeDAO();
        
        System.out.println("Ingrese el ID de la sede que desea eliminar: ");
        int Id_sede = sc.nextInt();
        
        boolean eliminado = sedeDAO.eliminarSede(Id_sede);
        
        if(eliminado) {
            System.out.println("\nLa sede con ID: " + Id_sede + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar la sede. Puede que no exista");
        }
        
        sc.close();
    }
}
