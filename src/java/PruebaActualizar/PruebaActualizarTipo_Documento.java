package PruebaActualizar;

import Controlador.Tipo_DocumentoDAO;
import Modelo.Tipo_Documento;
import java.util.Scanner;

/**
 *
 * @author isabe
 */
public class PruebaActualizarTipo_Documento {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Tipo_DocumentoDAO dao = new Tipo_DocumentoDAO();

        System.out.println("=== MODIFICAR TIPO DE DOCUMENTO EXISTENTE ===");

        System.out.print("Ingrese el ID del tipo de documento que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();   

        
        Tipo_Documento rolActualizar = new Tipo_Documento();
        rolActualizar.setId_tipo_Documento(idMod);

        System.out.print("Ingrese la nueva descripcion del tipo de documento: ");
        rolActualizar.setDescripcion_Tipo_Doc(sc.nextLine());

        
        boolean resultado = dao.actualizarTipo_Documento(rolActualizar);

        if (resultado) {
            System.out.println("✅ El tipo de documento ha sido actualizado correctamente.");
        } else {
            System.out.println("❌ Error: No se encontró ningun tipo de documento con el ID " + idMod);
        }

        sc.close();
    }
}