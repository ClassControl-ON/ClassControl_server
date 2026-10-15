package PruebaInsertar;

import java.util.Scanner;
import Modelo.Ambientes;
import Controlador.AmbientesDAO;

public class PruebaInsertarAmbientes {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Ambientes rol = new Ambientes();
        AmbientesDAO dao = new AmbientesDAO();
        
        System.out.println("=== INSERTAR NUEVO AMBIENTE ===");
        
       
        System.out.print("Ingrese el ID del ambiente: ");
        rol.setId_ambientes(sc.nextInt());
        sc.nextLine();
        
       
        System.out.print("Ingrese la descripcion del ambiente: ");
        rol.setDescripcion_Ambiente (sc.nextLine());
        
        
        System.out.print("Ingrese la capacidad del ambiente: ");
        rol.setCapacidad(sc.nextInt());
        
        
        System.out.print("Ingrese el ID de la sede: ");
        rol.setSede_id_sede(sc.nextInt());
        
        
        boolean resultado = dao.insertarAmbientes(rol);
        
        if (resultado) {
            System.out.println("✅ El ambiente se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar el ambiente.");
        }
        
        sc.close();
    }
}