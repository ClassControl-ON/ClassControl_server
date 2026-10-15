package PruebaInsertar;

import java.util.Scanner;
import Modelo.Sede;
import Controlador.SedeDAO;

public class PruebaInsertarSede {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Sede rol = new Sede();
        SedeDAO dao = new SedeDAO();
        
        System.out.println("=== INSERTAR NUEVA SEDE ===");
        
       
        System.out.print("Ingrese el ID de la sede: ");
        rol.setId_sede(sc.nextInt());
        sc.nextLine();
        
       
        System.out.print("Ingrese el nombre de la sede: ");
        rol.setNombre_sede (sc.nextLine());
        
        
        boolean resultado = dao.insertarSede(rol);
        
        if (resultado) {
            System.out.println("✅ La sede se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar la sede.");
        }
        
        sc.close();
    }
}