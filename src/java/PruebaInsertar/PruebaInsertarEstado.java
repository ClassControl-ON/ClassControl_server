package PruebaInsertar;

import java.util.Scanner;
import Modelo.Estado;
import Controlador.EstadoDAO;

public class PruebaInsertarEstado {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Estado rol = new Estado();
        EstadoDAO dao = new EstadoDAO();
        
        System.out.println("=== INSERTAR NUEVO ESTADO ===");
        
       
        System.out.print("Ingrese el ID del estado: ");
        rol.setId_estado(sc.nextInt());
        sc.nextLine();
        
       
        System.out.print("Ingrese la descripcion del estado: ");
        rol.setDescripcion_Estado (sc.nextLine());
        
        
        boolean resultado = dao.insertarEstado(rol);
        
        if (resultado) {
            System.out.println("✅ El estado se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar el estado.");
        }
        
        sc.close();
    }
}