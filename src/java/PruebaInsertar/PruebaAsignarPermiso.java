package PruebaInsertar;

import java.util.Scanner;
import Modelo.Roles_has_Permisos;
import Controlador.Roles_has_PermisosDAO;

public class PruebaAsignarPermiso {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Roles_has_Permisos rhp = new Roles_has_Permisos();
        Roles_has_PermisosDAO dao = new Roles_has_PermisosDAO();

        System.out.println("=== ASIGNAR PERMISO A ROL ===");

        System.out.print("Ingrese el ID del rol: ");
        rhp.setRoles_id_roles(sc.nextInt());

        System.out.print("Ingrese el ID del permiso: ");
        rhp.setPermisos_id_permisos(sc.nextInt());

        if (dao.existeRelacion(rhp.getRoles_id_roles(), rhp.getPermisos_id_permisos())) {
            System.out.println("⚠️ Ese rol ya tiene asignado ese permiso.");
        } else {
            boolean resultado = dao.asignarPermiso(rhp);
            if (resultado) {
                System.out.println("✅ Permiso asignado correctamente al rol.");
            } else {
                System.out.println("❌ No se pudo asignar el permiso.");
            }
        }

        sc.close();
    }
}