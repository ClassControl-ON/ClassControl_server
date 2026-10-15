package PruebaInsertar;

import java.util.Scanner;
import Modelo.Modalidad;
import Controlador.ModalidadDAO;

public class PruebaInsertarModalidad {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Modalidad rol = new Modalidad();
        ModalidadDAO dao = new ModalidadDAO();
        
        System.out.println("=== INSERTAR NUEVA MODALIDAD ===");
        
       
        System.out.print("Ingrese el ID de la modalidad: ");
        rol.setId_modalidad(sc.nextInt());
        sc.nextLine();
        
       
        System.out.print("Ingrese la descripcion de la modalidad: ");
        rol.setDescripcion_Modalidad (sc.nextLine());
        
        
        boolean resultado = dao.insertarModalidad(rol);
        
        if (resultado) {
            System.out.println("✅ La modalidad se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar la modalidad.");
        }
        
        sc.close();
    }
}