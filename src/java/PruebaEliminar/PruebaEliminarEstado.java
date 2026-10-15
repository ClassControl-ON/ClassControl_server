package PruebaEliminar;

import java.util.Scanner;
import Controlador.EstadoDAO;

public class PruebaEliminarEstado {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EstadoDAO estadoDAO = new EstadoDAO();
        
        System.out.println("Ingrese el ID del estado que desea eliminar: ");
        int Id_estado = sc.nextInt();
        
        boolean eliminado = estadoDAO.eliminarEstado(Id_estado);
        
        if(eliminado) {
            System.out.println("\nEl estado con ID: " + Id_estado + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar el estado. Puede que no exista");
        }
        
        sc.close();
    }
}
