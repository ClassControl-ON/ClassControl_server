package PruebaConsultar;

import java.util.List;
import java.util.Scanner;
import Modelo.Permisos;
import Controlador.Roles_has_PermisosDAO;

public class PruebaConsultarPermisosPorRol {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Roles_has_PermisosDAO dao = new Roles_has_PermisosDAO();

        System.out.println("=== CONSULTAR PERMISOS DE UN ROL ===");

        System.out.print("Ingrese el ID del rol: ");
        int idRol = sc.nextInt();

        List<Permisos> lista = dao.obtenerPermisosPorRol(idRol);

        if (!lista.isEmpty()) {
            System.out.println("\nPermisos del rol con ID " + idRol + ":");
            for (Permisos p : lista) {
                System.out.println("  - ID: " + p.getId_permisos() + " | " + p.getDescripcion_permisos());
            }
        } else {
            System.out.println("El rol con ID " + idRol + " no tiene permisos asignados.");
        }

        sc.close();
    }
}