package PruebaEliminar;

import java.util.Scanner;
import Controlador.FichaDAO;

public class PruebaEliminarFicha {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        FichaDAO fichaDAO = new FichaDAO();
        
        System.out.println("Ingrese el ID de la ficha que desea eliminar: ");
        int Id_ficha = sc.nextInt();
        
        boolean eliminado = fichaDAO.eliminarFicha(Id_ficha);
        
        if(eliminado) {
            System.out.println("\nLa ficha con ID: " + Id_ficha + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar la ficha. Puede que no exista");
        }
        
        sc.close();
    }
}
