package PruebaEliminar;

import java.util.Scanner;
import Modelo.Roles_has_Permisos;
import Controlador.Roles_has_PermisosDAO;

public class PruebaQuitarPermiso {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Roles_has_Permisos rhp = new Roles_has_Permisos();
        Roles_has_PermisosDAO dao = new Roles_has_PermisosDAO();

        System.out.println("=== QUITAR PERMISO DE UN ROL ===");

        System.out.print("Ingrese el ID del rol: ");
        rhp.setRoles_id_roles(sc.nextInt());

        System.out.print("Ingrese el ID del permiso a quitar: ");
        rhp.setPermisos_id_permisos(sc.nextInt());

        boolean resultado = dao.quitarPermiso(rhp);

        if (resultado) {
            System.out.println("✅ El permiso fue quitado del rol correctamente.");
        } else {
            System.out.println("❌ No se pudo quitar el permiso. Puede que la relación no exista.");
        }

        sc.close();
    }
}