package PruebaInsertar;

import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class PruebaInsertarUsuarios {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Usuarios usuario = new Usuarios();
        UsuariosDAO dao = new UsuariosDAO();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.println("=== INSERTAR NUEVO USUARIO ===");

        System.out.print("Ingrese los nombres del usuario: ");
        usuario.setNombres(sc.nextLine());

        System.out.print("Ingrese los apellidos del usuario: ");
        usuario.setApellidos(sc.nextLine());

        System.out.print("Ingrese la identificacion del usuario: ");
        usuario.setIdentificacion(sc.nextLine());

        
        LocalDate fechaNac = null;
        while (fechaNac == null) {
            System.out.print("Fecha de nacimiento (dd/MM/yyyy) o Enter para omitir: ");
            String entrada = sc.nextLine().trim();
            if (entrada.isEmpty()) break;
            try { fechaNac = LocalDate.parse(entrada, fmt); }
            catch (DateTimeParseException e) { System.out.println("  Formato incorrecto, intente de nuevo."); }
        }
        usuario.setFecha_Nacimiento(fechaNac);

        System.out.print("Ingrese el correo del usuario: ");
        usuario.setCorreo(sc.nextLine());

        System.out.print("Ingrese el telefono del usuario: ");
        usuario.setTelefono(sc.nextLine());

        System.out.print("Ingrese la direccion del usuario: ");
        usuario.setDireccion(sc.nextLine());

        System.out.print("Ingrese el username del usuario: ");
        usuario.setUsername(sc.nextLine());

        System.out.print("Ingrese el nivel educativo del usuario: ");
        usuario.setNivel_Educativo(sc.nextLine());

        System.out.print("Ingrese la profesion del usuario: ");
        usuario.setProfesion(sc.nextLine());

        System.out.print("Ingrese la clave del usuario: ");
        usuario.setClave(sc.nextLine());


        usuario.setActivo(pedirBooleano(sc, "¿Está activo? (S/N)"));

        System.out.print("Ingrese el ID del rol: ");
        usuario.setRoles_id_roles(Integer.parseInt(sc.nextLine().trim()));

        System.out.print("Ingrese el ID del tipo de documento: ");
        usuario.setTipo_Documento_id_tipo_Documento(Integer.parseInt(sc.nextLine().trim()));

        System.out.print("Ingrese el ID del tipo de vinculacion: ");
        usuario.setTipo_vinculacion_id_tipo_vinculacion(Integer.parseInt(sc.nextLine().trim()));

        boolean resultado = dao.insertarUsuarios(usuario);
        System.out.println(resultado
            ? "✅ El usuario se guardó correctamente."
            : "❌ No se pudo guardar el usuario.");

        sc.close();
    }

    private static boolean pedirBooleano(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje + ": ");
            String resp = sc.nextLine().trim().toUpperCase();
            if (resp.equals("S") || resp.equals("SI") || resp.equals("TRUE"))  return true;
            if (resp.equals("N") || resp.equals("NO") || resp.equals("FALSE")) return false;
            System.out.println("  Respuesta no válida. Use S/N, SI/NO o true/false.");
        }
    }
}