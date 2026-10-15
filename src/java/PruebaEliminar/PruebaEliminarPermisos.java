package PruebaEliminar;

import java.util.Scanner;
import Controlador.PermisosDAO;

public class PruebaEliminarPermisos {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        PermisosDAO dao = new PermisosDAO();

        System.out.println("Ingrese el ID del permiso que desea eliminar: ");
        int idPermiso = sc.nextInt();

        boolean eliminado = dao.eliminarPermiso(idPermiso);

        if (eliminado) {
            System.out.println("\nEl permiso con ID " + idPermiso + " se eliminó correctamente.");
        } else {
            System.out.println("\nNo se pudo eliminar el permiso. Puede que no exista.");
        }

        sc.close();
    }
}