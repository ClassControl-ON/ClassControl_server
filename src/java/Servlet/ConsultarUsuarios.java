package Servlet;

import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@WebServlet("/ConsultarUsuarios")
public class ConsultarUsuarios extends ConsultarBaseServlet<Usuarios> {

    @Override
    protected String getTipo() { return "usuarios"; }

    @Override
    protected List<Usuarios> obtenerLista() {
        return new UsuariosDAO().listarUsuarios();
    }

    /**
     * Filtros seguros (vía PreparedStatement) leídos del request:
     *   ?ficha=N   → solo usuarios de esa ficha (aprendices)
     *   ?rol=N     → solo usuarios de ese rol
     *   ?q=texto   → búsqueda por nombre, apellido, documento, correo o username
     */
    @Override
    protected List<Usuarios> obtenerLista(HttpServletRequest request) throws Exception {
        Integer fichaId = Autorizacion.intParam(request, "ficha");
        Integer rolId = Autorizacion.intParam(request, "rol");
        String q = Autorizacion.textoSeguro(request.getParameter("q"), 80);
        return new UsuariosDAO().listarUsuarios(fichaId, rolId, q);
    }

    @Override
    protected String camposJson(Usuarios u) {
        // OJO: a propósito NO se incluye "clave" (ni siquiera el hash)
        // en la respuesta JSON — nunca debe viajar al cliente.
        return campoNum("id", u.getId_usuarios()) + ","
             + campoStr("nombres", u.getNombres()) + ","
             + campoStr("apellidos", u.getApellidos()) + ","
             + campoStr("identificacion", u.getIdentificacion()) + ","
             + campoFecha("fechaNacimiento", u.getFecha_Nacimiento()) + ","
             + campoStr("correo", u.getCorreo()) + ","
             + campoStr("telefono", u.getTelefono()) + ","
             + campoStr("direccion", u.getDireccion()) + ","
             + campoStr("username", u.getUsername()) + ","
             + campoStr("nivelEducativo", u.getNivel_Educativo()) + ","
             + campoStr("profesion", u.getProfesion()) + ","
             + campoBool("activo", u.isActivo()) + ","
             + campoFecha("fechaExpiracionContrasena", u.getFecha_ExpiracionContraseña()) + ","
             + campoNum("rolId", u.getRoles_id_roles()) + ","
             + campoNum("tipoDocumentoId", u.getTipo_Documento_id_tipo_Documento()) + ","
             + campoNum("tipoVinculacionId", u.getTipo_vinculacion_id_tipo_vinculacion()) + ","
             + campoNum("fichaId", u.getFicha_id_ficha());
    }
}
