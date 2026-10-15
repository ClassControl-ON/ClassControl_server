package PruebaInsertar;

import java.util.Scanner;
import Modelo.Permisos;
import Controlador.PermisosDAO;

public class PruebaInsertarPermisos {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Permisos permiso = new Permisos();
        PermisosDAO dao = new PermisosDAO();

        System.out.println("=== INSERTAR NUEVO PERMISO ===");

        System.out.print("Ingrese el ID del permiso: ");
        permiso.setId_permisos(sc.nextInt());
        sc.nextLine();

        System.out.print("Ingrese la descripcion del permiso: ");
        permiso.setDescripcion_permisos(sc.nextLine());

        boolean resultado = dao.insertarPermiso(permiso);

        if (resultado) {
            System.out.println("✅ El permiso se guardó correctamente en la base de datos.");
        } else {
            System.out.println("❌ No se pudo guardar el permiso.");
        }

        sc.close();
    }
}