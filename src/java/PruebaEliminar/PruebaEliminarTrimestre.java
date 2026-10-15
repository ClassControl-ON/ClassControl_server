package PruebaEliminar;

import java.util.Scanner;
import Controlador.TrimestreDAO;

public class PruebaEliminarTrimestre {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TrimestreDAO trimestreDAO = new TrimestreDAO();
        
        System.out.println("Ingrese el ID del trimestre que desea eliminar: ");
        int Id_trimestre = sc.nextInt();
        
        boolean eliminado = trimestreDAO.eliminarTrimestre(Id_trimestre);
        
        if(eliminado) {
            System.out.println("\nEl trimestre con ID: " + Id_trimestre + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar el trimestre. Puede que no exista");
        }
        
        sc.close();
    }
}
