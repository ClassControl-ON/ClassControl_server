package PruebaEliminar;

import java.util.Scanner;
import Controlador.CompetenciasDAO;

public class PruebaEliminarCompetencias {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        CompetenciasDAO competenciasDAO = new CompetenciasDAO();
        
        System.out.println("Ingrese el ID de la competencia que desea eliminar: ");
        int Id_ambientes = sc.nextInt();
        
        boolean eliminado = competenciasDAO.eliminarCompetencias(Id_ambientes);
        
        if(eliminado) {
            System.out.println("\nLa competencia con ID: " + Id_ambientes + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar la competencia. Puede que no exista");
        }
        
        sc.close();
    }
}
