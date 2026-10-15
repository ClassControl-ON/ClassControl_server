package PruebaInsertar;

import java.util.Scanner;
import Modelo.Programas;
import Controlador.ProgramasDAO;

public class PruebaInsertarProgramas {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Programas rol = new Programas();
        ProgramasDAO dao = new ProgramasDAO();
        
        System.out.println("=== INSERTAR NUEVO PROGRAMA ===");
        
       
        System.out.print("Ingrese el ID del programa: ");
        rol.setIdProgramas(sc.nextInt());
        
        
        System.out.print("Ingrese el codigo del programa: ");
        rol.setCodigo_programa(sc.nextInt());
        sc.nextLine();
        
       
        System.out.print("Ingrese la descripcion del rol: ");
        rol.setNombre_programa (sc.nextLine());
        
        
        boolean resultado = dao.insertarProgramas(rol);
        
        if (resultado) {
            System.out.println("✅ El programa se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar el programa.");
        }
        
        sc.close();
    }
}