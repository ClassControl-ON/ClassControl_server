package PruebaInsertar;

import java.util.Scanner;
import Modelo.Actividades;
import Controlador.ActividadesDAO;

public class PruebaInsertarActividades {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Actividades rol = new Actividades();
        ActividadesDAO dao = new ActividadesDAO();
        
        System.out.println("=== INSERTAR NUEVA ACTIVIDAD ===");
        
       
        System.out.print("Ingrese el ID de la actividad: ");
        rol.setId_actividades(sc.nextInt());
        sc.nextLine();
        
        System.out.print("Ingrese el codigo de la actividad: ");
        rol.setCodigo_Actividad(sc.nextInt());
        
       
        System.out.print("Ingrese el nombre de la actividad: ");
        rol.setNombre_Act (sc.nextLine());
        
        
        System.out.print("Ingrese la descripcion de la actividad: ");
        rol.setDescripcion (sc.nextLine());
        
        
        System.out.print("Ingrese el ID del resultado de aprendizaje: ");
        rol.setResultado_aprendizaje_id_resultado_aprendizaje(sc.nextInt());
        
        
        boolean resultado = dao.insertarActividades(rol);
        
        if (resultado) {
            System.out.println("✅ La actividad se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar la actividad.");
        }
        
        sc.close();
    }
}