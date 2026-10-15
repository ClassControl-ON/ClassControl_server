package PruebaActualizar;

import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class PruebaActualizarUsuario {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UsuariosDAO dao = new UsuariosDAO();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.println("=== MODIFICAR USUARIO EXISTENTE ===");

        System.out.print("Ingrese el ID del usuario que desea modificar: ");
        int idMod = Integer.parseInt(sc.nextLine().trim());

        Usuarios u = new Usuarios();
        u.setId_usuarios(idMod);

        System.out.print("Ingrese los nuevos nombres: ");
        u.setNombres(sc.nextLine());

        System.out.print("Ingrese los nuevos apellidos: ");
        u.setApellidos(sc.nextLine());

        System.out.print("Ingrese la nueva identificacion: ");
        u.setIdentificacion(sc.nextLine());

        LocalDate fechaNac = null;
        while (fechaNac == null) {
            System.out.print("Fecha de nacimiento (dd/MM/yyyy) o Enter para omitir: ");
            String entrada = sc.nextLine().trim();
            if (entrada.isEmpty()) break;
            try { fechaNac = LocalDate.parse(entrada, fmt); }
            catch (DateTimeParseException e) { System.out.println("  Formato incorrecto, intente de nuevo."); }
        }
        u.setFecha_Nacimiento(fechaNac);

        System.out.print("Ingrese el nuevo correo: ");
        u.setCorreo(sc.nextLine());

        System.out.print("Ingrese el nuevo telefono: ");
        u.setTelefono(sc.nextLine());

        System.out.print("Ingrese la nueva direccion: ");
        u.setDireccion(sc.nextLine());

        System.out.print("Ingrese el nuevo username: ");
        u.setUsername(sc.nextLine());

        System.out.print("Ingrese el nuevo nivel educativo: ");
        u.setNivel_Educativo(sc.nextLine());

        System.out.print("Ingrese la nueva profesion: ");
        u.setProfesion(sc.nextLine());

        System.out.print("Ingrese la nueva clave: ");
        u.setClave(sc.nextLine());

        System.out.print("¿El usuario está activo? (S/N): ");
        u.setActivo(sc.nextLine().trim().toUpperCase().equals("S"));

        System.out.print("Ingrese el nuevo ID del rol: ");
        u.setRoles_id_roles(Integer.parseInt(sc.nextLine().trim()));

        System.out.print("Ingrese el nuevo ID del tipo de documento: ");
        u.setTipo_Documento_id_tipo_Documento(Integer.parseInt(sc.nextLine().trim()));

        System.out.print("Ingrese el nuevo ID del tipo de vinculacion: ");
        u.setTipo_vinculacion_id_tipo_vinculacion(Integer.parseInt(sc.nextLine().trim()));

        boolean resultado = dao.actualizarUsuario(u);
        System.out.println(resultado
            ? "✅ El usuario ha sido actualizado correctamente."
            : "❌ Error: No se encontró ningún usuario con el ID " + idMod);

        sc.close();
    }
}