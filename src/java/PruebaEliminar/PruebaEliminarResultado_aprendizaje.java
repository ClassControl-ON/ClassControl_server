package PruebaEliminar;

import java.util.Scanner;
import Controlador.Resultado_aprendizajeDAO;

public class PruebaEliminarResultado_aprendizaje {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Resultado_aprendizajeDAO resultado_aprendizajeDAO = new Resultado_aprendizajeDAO();
        
        System.out.println("Ingrese el ID de resultado de aprendizaje que desea eliminar: ");
        int Id_resultado_aprendizaje = sc.nextInt();
        
        boolean eliminado = resultado_aprendizajeDAO.eliminarResultado_aprendizaje(Id_resultado_aprendizaje);
        
        if(eliminado) {
            System.out.println("\nEl resultado de aprendizaje con ID: " + Id_resultado_aprendizaje + " se elimino correctamente.");
        } else {
            System.out.println("\nNo se puede eliminar el resultado de aprendizaje. Puede que no exista");
        }
        
        sc.close();
    }
}
