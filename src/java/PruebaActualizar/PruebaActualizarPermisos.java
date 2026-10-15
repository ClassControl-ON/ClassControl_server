package PruebaActualizar;

import java.util.Scanner;
import Modelo.Permisos;
import Controlador.PermisosDAO;

public class PruebaActualizarPermisos {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        PermisosDAO dao = new PermisosDAO();

        System.out.println("=== MODIFICAR PERMISO EXISTENTE ===");

        System.out.print("Ingrese el ID del permiso que desea modificar: ");
        int idMod = sc.nextInt();
        sc.nextLine();

        Permisos permiso = new Permisos();
        permiso.setId_permisos(idMod);

        System.out.print("Ingrese la nueva descripcion del permiso: ");
        permiso.setDescripcion_permisos(sc.nextLine());

        boolean resultado = dao.actualizarPermiso(permiso);

        if (resultado) {
            System.out.println("✅ El permiso ha sido actualizado correctamente.");
        } else {
            System.out.println("❌ Error: No se encontró ningún permiso con el ID " + idMod);
        }

        sc.close();
    }
}