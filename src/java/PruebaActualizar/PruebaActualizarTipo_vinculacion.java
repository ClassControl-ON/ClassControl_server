package PruebaActualizar;

import Controlador.Tipo_vinculacionDAO;
import Modelo.Tipo_vinculacion;
import java.util.Scanner;

/**
 *
 * @author isabe
 */
public class PruebaActualizarTipo_vinculacion {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Tipo_vinculacionDAO dao = new Tipo_vinculacionDAO();

        System.out.println("=== MODIFICAR TIPO DE VINCULACION EXISTENTE ===");

        System.out.print("Ingrese el ID del tipo de vinculacion que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();   

        
        Tipo_vinculacion rolActualizar = new Tipo_vinculacion();
        rolActualizar.setId_tipo_vinculacion(idMod);

        System.out.print("Ingrese la nueva descripcion del tipo de vinculacion: ");
        rolActualizar.setDescripcion_vinculacion(sc.nextLine());

        
        boolean resultado = dao.actualizarTipo_vinculacion(rolActualizar);

        if (resultado) {
            System.out.println("✅ El tipo de vinculacion ha sido actualizado correctamente.");
        } else {
            System.out.println("❌ Error: No se encontró ningun tipo de vinculacion con el ID " + idMod);
        }

        sc.close();
    }
}