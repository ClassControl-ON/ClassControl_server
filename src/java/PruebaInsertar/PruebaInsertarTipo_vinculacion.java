package PruebaInsertar;

import java.util.Scanner;
import Modelo.Tipo_vinculacion;
import Controlador.Tipo_vinculacionDAO;

public class PruebaInsertarTipo_vinculacion {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Tipo_vinculacion rol = new Tipo_vinculacion();
        Tipo_vinculacionDAO dao = new Tipo_vinculacionDAO();
        
        System.out.println("=== INSERTAR NUEVO TIPO DE DOCUMENTO ===");
        
       
        System.out.print("Ingrese el ID del tipo de vinculacion: ");
        rol.setId_tipo_vinculacion(sc.nextInt());
        sc.nextLine();
        
       
        System.out.print("Ingrese la descripcion del tipo de vinculacion: ");
        rol.setDescripcion_vinculacion (sc.nextLine());
        
        
        boolean resultado = dao.insertarTipo_Vinculacion(rol);
        
        if (resultado) {
            System.out.println("✅ El tipo de vinculacion se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar el rol.");
        }
        
        sc.close();
    }
}