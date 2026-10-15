package PruebaEliminar;

import java.util.Scanner;
import Controlador.Programacion_InstructoresDAO;

public class PruebaEliminarProgramacion_Instructores {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Programacion_InstructoresDAO programacion_InstructoresDAO = new Programacion_InstructoresDAO();
        
        System.out.println("Ingrese el ID de la programacion de instructores que desea eliminar: ");
        int Id_programacion_Instructores = sc.nextInt();
        
        boolean eliminado = programacion_InstructoresDAO.eliminarProgramacion_Instructores(Id_programacion_Instructores);
        
        if(eliminado) {
            System.out.println("\nLa programacion de instructores con ID: " + Id_programacion_Instructores + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar la programacion de instructores. Puede que no exista");
        }
        
        sc.close();
    }
}
