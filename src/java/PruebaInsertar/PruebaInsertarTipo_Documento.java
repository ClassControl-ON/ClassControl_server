package PruebaInsertar;

import java.util.Scanner;
import Modelo.Tipo_Documento;
import Controlador.Tipo_DocumentoDAO;

public class PruebaInsertarTipo_Documento {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Tipo_Documento rol = new Tipo_Documento();
        Tipo_DocumentoDAO dao = new Tipo_DocumentoDAO();
        
        System.out.println("=== INSERTAR NUEVO TIPO DE DOCUMENTO ===");
        
       
        System.out.print("Ingrese el ID del tipo de documento: ");
        rol.setId_tipo_Documento(sc.nextInt());
        sc.nextLine();
        
       
        System.out.print("Ingrese la descripcion del tipo de documento: ");
        rol.setDescripcion_Tipo_Doc (sc.nextLine());
        
        
        boolean resultado = dao.insertarTipo_Documento(rol);
        
        if (resultado) {
            System.out.println("✅ El tipo de documento se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar el rol.");
        }
        
        sc.close();
    }
}