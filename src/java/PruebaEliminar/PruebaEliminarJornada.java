package PruebaEliminar;

import java.util.Scanner;
import Controlador.JornadaDAO;

public class PruebaEliminarJornada {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        JornadaDAO jornadaDAO = new JornadaDAO();
        
        System.out.println("Ingrese el ID de la jornada que desea eliminar: ");
        int Id_jornada = sc.nextInt();
        
        boolean eliminado = jornadaDAO.eliminarJornada(Id_jornada);
        
        if(eliminado) {
            System.out.println("\nLa jornada con ID: " + Id_jornada + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar la jornada. Puede que no exista");
        }
        
        sc.close();
    }
}
