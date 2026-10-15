package Servlet;

import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;

/**
 * Registra un usuario desde el panel de administración (Gestión de Usuarios).
 *
 * A diferencia de {@link RegistrarUsuario} (formulario público de
 * autorregistro), este servlet SÍ permite asignar cualquier rol —
 * incluido Administrador — porque solo es alcanzable desde una sesión
 * ya autenticada (RegistroBaseServlet exige sesión activa antes de
 * llamar a guardar()) y la página que lo consume (Gestion_Usuarios.jsp)
 * ya está restringida a usuarios con rol Administrador.
 */
@WebServlet("/RegistrarUsuarioAdmin")
public class RegistrarUsuarioAdmin extends RegistroBaseServlet {

    @Override
    protected String getTipo() { return "usuario"; }

    @Override
    protected boolean guardar(HttpServletRequest request) throws Exception {
        Usuarios usuario = new Usuarios();
        usuario.setNombres(texto(request, "nombres"));
        usuario.setApellidos(texto(request, "apellidos"));
        usuario.setIdentificacion(texto(request, "identificacion"));
        usuario.setCorreo(texto(request, "correo"));
        usuario.setTelefono(texto(request, "telefono"));
        usuario.setDireccion(textoOpcional(request, "direccion"));
        usuario.setUsername(texto(request, "username"));
        usuario.setNivel_Educativo(textoOpcional(request, "nivel_Educativo"));
        usuario.setProfesion(textoOpcional(request, "profesion"));

        // La clave nunca se persiste en texto plano.
        String claveEnTextoPlano = texto(request, "clave");
        usuario.setClave(BCrypt.hashpw(claveEnTextoPlano, BCrypt.gensalt(12)));

        String activoStr = request.getParameter("activo");
        usuario.setActivo(activoStr == null || activoStr.trim().isEmpty()
                || "true".equalsIgnoreCase(activoStr.trim())
                || "on".equalsIgnoreCase(activoStr.trim()));

        usuario.setRoles_id_roles(entero(request, "rol"));
        usuario.setTipo_Documento_id_tipo_Documento(entero(request, "tipoDoc"));

        String tipoVinculacionStr = request.getParameter("tipoVinculacion");
        usuario.setTipo_vinculacion_id_tipo_vinculacion(
                (tipoVinculacionStr == null || tipoVinculacionStr.trim().isEmpty())
                        ? 1 : Integer.parseInt(tipoVinculacionStr.trim()));

        // Ficha del aprendiz (opcional; solo aplica al rol Aprendiz).
        String fichaStr = request.getParameter("ficha");
        if (fichaStr != null && !fichaStr.trim().isEmpty()) {
            usuario.setFicha_id_ficha(Integer.parseInt(fichaStr.trim()));
        }

        String fechaNacStr = request.getParameter("fecha_Nacimiento");
        if (fechaNacStr != null && !fechaNacStr.trim().isEmpty()) {
            usuario.setFecha_Nacimiento(LocalDate.parse(fechaNacStr.trim()));
        }

        return new UsuariosDAO().insertarUsuarios(usuario);
    }
}
