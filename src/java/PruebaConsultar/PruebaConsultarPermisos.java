package PruebaConsultar;

import Modelo.Permisos;
import Controlador.PermisosDAO;

public class PruebaConsultarPermisos {
    public static void main(String[] args) {
        PermisosDAO dao = new PermisosDAO();

        int idPermiso = 1;

        Permisos permiso = dao.consultarPermiso(idPermiso);

        if (permiso != null) {
            System.out.println("=== Permiso encontrado ===");
            System.out.println("ID: " + permiso.getId_permisos());
            System.out.println("Descripcion: " + permiso.getDescripcion_permisos());
        } else {
            System.out.println("No se encontró el permiso con ID: " + idPermiso);
        }
    }
}