package Servlet;

import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Actualiza un usuario existente.
 *
 * Reglas especiales respecto al patrón genérico:
 *  - La clave es OPCIONAL: si el campo "clave" llega vacío, se conserva
 *    el hash ya almacenado (nunca se sobreescribe con texto vacío).
 *  - Si llega una clave nueva, se hashea con BCrypt antes de guardar
 *    (igual que en RegistrarUsuario), lo que también renueva la fecha
 *    de expiración de la contraseña (ver Modelo.Usuarios#setClave).
 *  - Los campos no enviados por el formulario conservan su valor actual
 *    (se parte del registro existente en BD, no de un objeto vacío).
 */
@WebServlet("/ActualizarUsuario")
public class ActualizarUsuario extends ActualizarBaseServlet {

    @Override
    protected String getTipo() { return "usuario"; }

    @Override
    protected boolean actualizar(HttpServletRequest request) {
        int id = entero(request, "id");

        UsuariosDAO dao = new UsuariosDAO();
        Usuarios usuario = dao.consultaUsuarios(id);
        if (usuario == null) {
            throw new IllegalArgumentException("No existe un usuario con id " + id);
        }

        usuario.setNombres(texto(request, "nombres"));
        usuario.setApellidos(texto(request, "apellidos"));
        usuario.setIdentificacion(texto(request, "identificacion"));
        usuario.setCorreo(texto(request, "correo"));
        usuario.setTelefono(texto(request, "telefono"));
        usuario.setDireccion(textoOpcional(request, "direccion"));
        usuario.setUsername(texto(request, "username"));
        usuario.setNivel_Educativo(textoOpcional(request, "nivel_Educativo"));
        usuario.setProfesion(textoOpcional(request, "profesion"));
        usuario.setRoles_id_roles(entero(request, "rol"));
        usuario.setTipo_Documento_id_tipo_Documento(entero(request, "tipoDoc"));
        usuario.setTipo_vinculacion_id_tipo_vinculacion(
                enteroOpcional(request, "tipoVinculacion", usuario.getTipo_vinculacion_id_tipo_vinculacion()));

        // Ficha del aprendiz: solo se toca si el formulario la envía
        // (vacío = sin ficha asignada).
        String fichaStr = request.getParameter("ficha");
        if (fichaStr != null) {
            usuario.setFicha_id_ficha(fichaStr.trim().isEmpty()
                    ? null : Integer.valueOf(fichaStr.trim()));
        }

        String fechaNacStr = request.getParameter("fecha_Nacimiento");
        if (fechaNacStr != null && !fechaNacStr.trim().isEmpty()) {
            usuario.setFecha_Nacimiento(java.time.LocalDate.parse(fechaNacStr.trim()));
        }

        // "activo" solo se toca si el formulario lo envía explícitamente
        String activoStr = request.getParameter("activo");
        if (activoStr != null && !activoStr.trim().isEmpty()) {
            usuario.setActivo(Boolean.parseBoolean(activoStr.trim()) || "on".equalsIgnoreCase(activoStr.trim()));
        }

        // Clave: solo se actualiza si el usuario escribió una nueva
        String claveNueva = request.getParameter("clave");
        if (claveNueva != null && !claveNueva.trim().isEmpty()) {
            usuario.setClave(BCrypt.hashpw(claveNueva.trim(), BCrypt.gensalt(12)));
        }

        return dao.actualizarUsuario(usuario);
    }
}
