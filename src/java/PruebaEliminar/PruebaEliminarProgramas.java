package PruebaEliminar;

import java.util.Scanner;
import Controlador.ProgramasDAO;

public class PruebaEliminarProgramas {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ProgramasDAO programasDAO = new ProgramasDAO();
        
        System.out.println("Ingrese el ID del programa que desea eliminar: ");
        int Id_programas = sc.nextInt();
        
        boolean eliminado = programasDAO.eliminarProgramas(Id_programas);
        
        if(eliminado) {
            System.out.println("\nEl programa con ID: " + Id_programas + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar el programa. Puede que no exista");
        }
        
        sc.close();
    }
}
