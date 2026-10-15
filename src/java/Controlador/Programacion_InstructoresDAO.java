package Controlador;

import Conexion.Conexion;
import Modelo.ProgramacionInstructoresDTO;
import Modelo.Programacion_Instructores;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Programacion_InstructoresDAO {

    // ══════════════════════════════════════════════════════════════
    //  LISTAR — consulta completa con JOINs para la vista principal
    // ══════════════════════════════════════════════════════════════

    /**
     * Devuelve todas las programaciones con los nombres de ficha,
     * instructor, ambiente, trimestre y estado
     * obtenidos mediante JOIN para que el JSP/JS los inyecte
     * directamente en el HTML sin llamadas adicionales.
     */
    public List<ProgramacionInstructoresDTO> listarProgramaciones() {
        return listarProgramaciones(null, null, null);
    }

    /**
     * Lista las programaciones y permite limitar el resultado por las tres
     * relaciones principales de la pantalla. Un valor null significa que no
     * se aplica ese filtro.
     */
    public List<ProgramacionInstructoresDTO> listarProgramaciones(
            Integer instructorId, Integer fichaId, Integer trimestreId) {
        List<ProgramacionInstructoresDTO> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();

        if (con == null) {
            System.out.println("❌ No se pudo obtener conexión para listar programaciones.");
            return lista;
        }

        /*
         * JOIN explicado:
         *  pi   → programacion_Instructores  (tabla principal)
         *  f    → Ficha                      (código + programa)
         *  p    → Programas                  (nombre del programa de la ficha)
         *  u    → Usuarios                   (instructor: nombres + apellidos)
         *  a    → Ambientes                  (descripción del ambiente)
         *  t    → Trimestre                  (num_trimestre + descripción)
         *  e    → Estado                     (descripción del estado)
         *
         * Nota: Competencias ya NO se vincula directamente a
         *       programacion_Instructores. La cadena real es
         *       Programacion_Instructores → Ficha → Programas → Competencias,
         *       y un mismo programa tiene muchas competencias, así que no
         *       se puede resolver en una sola columna de esta consulta.
         */
        String sql =
            "SELECT " +
            "  pi.id_programacion_Instructores   AS id, " +
            "  pi.Observaciones                  AS observaciones, " +
            "  pi.fecha_inicial_Prog             AS fechaInicialProg, " +
            "  pi.fecha_fin_Prog                 AS fechaFinProg, " +
            "  pi.dias_Semana                    AS diasSemana, " +
            "  pi.hora_inicio                    AS horaInicio, " +
            "  pi.hora_fin                       AS horaFin, " +

            // Ficha
            "  f.id_ficha                        AS fichaId, " +
            "  f.codigo_ficha                    AS fichaNumero, " +
            "  COALESCE(p.nombre_programa, '')   AS fichaPrograma, " +

            // Instructor
            "  u.id_usuarios                     AS instructorId, " +
            "  CONCAT(u.nombres, ' ', u.apellidos) AS instructorNombre, " +

            // Ambiente
            "  a.id_ambientes                    AS ambienteId, " +
            "  a.descripcion_Ambiente             AS ambienteNombre, " +

            // Trimestre
            "  t.id_trimestre                    AS trimestreId, " +
            "  CONCAT('Trimestre ', t.num_trimestre, ' — ', t.descripcion) AS trimestreNombre, " +

            // Estado
            "  e.id_estado                        AS estadoId, " +
            "  e.descripcion_Estado               AS estadoNombre, " +

            // Actividad -> Resultado de aprendizaje -> Competencia
            "  ac.id_actividades                   AS actividadId, " +
            "  ac.nombre_Act                       AS actividadNombre, " +
            "  COALESCE(c.descripcion_Competencias, '') AS competenciaNombre " +

            "FROM programacion_instructores pi " +
            "INNER JOIN ficha    f  ON f.id_ficha              = pi.ficha_id_ficha " +
            "LEFT  JOIN programas p  ON p.idprogramas           = f.programas_idprogramas " +
            "INNER JOIN usuarios  u  ON u.id_usuarios           = pi.usuarios_id_usuarios " +
            "INNER JOIN ambientes a  ON a.id_ambientes          = pi.ambientes_id_ambientes " +
            "INNER JOIN trimestre t  ON t.id_trimestre          = pi.trimestre_id_trimestre " +
            "INNER JOIN estado    e  ON e.id_estado             = pi.estado_id_estado " +
            "INNER JOIN actividades ac ON ac.id_actividades      = pi.actividades_id_actividades " +
            "LEFT JOIN resultado_aprendizaje ra ON ra.id_resultado_aprendizaje = ac.resultado_aprendizaje_id_resultado_aprendizaje " +
            "LEFT JOIN competencias c ON c.id_competencias       = ra.competencias_id_competencias ";

        StringBuilder sqlFiltrado = new StringBuilder(sql);
        List<Integer> valoresFiltro = new ArrayList<>();
        agregarFiltro(sqlFiltrado, valoresFiltro, "pi.Usuarios_id_usuarios", instructorId);
        agregarFiltro(sqlFiltrado, valoresFiltro, "pi.Ficha_id_ficha", fichaId);
        agregarFiltro(sqlFiltrado, valoresFiltro, "pi.Trimestre_id_trimestre", trimestreId);
        sqlFiltrado.append(" ORDER BY t.num_trimestre, pi.fecha_inicial_Prog");

        try (PreparedStatement ps = con.prepareStatement(sqlFiltrado.toString())) {
            for (int i = 0; i < valoresFiltro.size(); i++) {
                ps.setInt(i + 1, valoresFiltro.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ProgramacionInstructoresDTO dto = new ProgramacionInstructoresDTO();

                dto.setId(rs.getInt("id"));
                dto.setObservaciones(rs.getString("observaciones"));
                dto.setFechaInicialProg(rs.getObject("fechaInicialProg", LocalDate.class));
                dto.setFechaFinProg(rs.getObject("fechaFinProg",    LocalDate.class));
                dto.setDiasSemana(rs.getString("diasSemana"));
                dto.setHoraInicio(rs.getObject("horaInicio", LocalTime.class));
                dto.setHoraFin(rs.getObject("horaFin",    LocalTime.class));

                dto.setFichaId(rs.getInt("fichaId"));
                dto.setFichaNumero(rs.getString("fichaNumero"));
                dto.setFichaPrograma(rs.getString("fichaPrograma"));

                dto.setInstructorId(rs.getInt("instructorId"));
                dto.setInstructorNombre(rs.getString("instructorNombre"));

                dto.setAmbienteId(rs.getInt("ambienteId"));
                dto.setAmbienteNombre(rs.getString("ambienteNombre"));

                dto.setTrimestreId(rs.getInt("trimestreId"));
                dto.setTrimestreNombre(rs.getString("trimestreNombre"));

                dto.setEstadoId(rs.getInt("estadoId"));
                dto.setEstadoNombre(rs.getString("estadoNombre"));

                dto.setActividadId(rs.getInt("actividadId"));
                dto.setActividadNombre(rs.getString("actividadNombre"));
                dto.setCompetenciaNombre(rs.getString("competenciaNombre"));

                lista.add(dto);
            }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al listar programaciones: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return lista;
    }

    private void agregarFiltro(StringBuilder sql, List<Integer> valores,
            String columna, Integer valor) {
        if (valor == null) {
            return;
        }
        sql.append(valores.isEmpty() ? " WHERE " : " AND ")
                .append(columna).append(" = ?");
        valores.add(valor);
    }

    // ══════════════════════════════════════════════════════════════
    //  INSERTAR
    // ══════════════════════════════════════════════════════════════

    /**
     * Verifica si existe una programación que entre en conflicto de horario
     * con la que se intenta guardar.
     *
     * Se considera conflicto cuando hay otra programación en el MISMO ambiente,
     * el MISMO trimestre (Trimestre_id_trimestre) y el MISMO día
     * (dias_Semana) cuyo rango [hora_inicio, hora_fin] se solapa con el rango
     * [horaInicio, horaFin] propuesto, y además los periodos de fechas
     * [fecha_inicial_Prog, fecha_fin_Prog] se superponen:
     *     nuevoInicio < existenteFin                   AND
     *     nuevoFin > existenteInicio                   AND
     *     existenteInicio <= nuevoFin                  AND
     *     existenteFin >= nuevoInicio
     *
     * Si la programación propuesta tiene fechas nulas no se aplica el filtro
     * de fechas (no se puede comprobar la superposición) y solo se evalúan
     * ambiente, trimestre, día y horario.
     *
     * @param idIgnorar  id de la programación que se está editando (0 en un alta)
     * @return una descripción legible del conflicto, o {@code null} si no hay
     *         ninguna programación que se solape
     */
    private String obtenerDescripcionConflicto(Programacion_Instructores prog, int idIgnorar) {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            return null;
        }

        // Las fechas de la tabla son NOT NULL, pero se protege el caso de que
        // la programación propuesta llegue sin fechas para no insertar NULL
        // en la comparación (NULL && ... nunca daría conflicto).
        boolean verificarFechas =
            prog.getFecha_inicial_Prog() != null && prog.getFecha_fin_Prog() != null;

        String sql =
            "SELECT CONCAT(u.nombres, ' ', u.apellidos) AS instructorNombre, " +
            "       a.descripcion_Ambiente AS ambienteNombre, " +
            "       pi.hora_inicio AS horaInicio, " +
            "       pi.hora_fin AS horaFin " +
            "FROM programacion_instructores pi " +
            "INNER JOIN usuarios u ON u.id_usuarios = pi.Usuarios_id_usuarios " +
            "INNER JOIN ambientes a ON a.id_ambientes = pi.Ambientes_id_ambientes " +
            "WHERE pi.Ambientes_id_ambientes = ? " +
            "  AND pi.Trimestre_id_trimestre = ? " +
            "  AND pi.dias_Semana = ? " +
            "  AND pi.hora_inicio < ? " +
            "  AND pi.hora_fin > ? " +
            (verificarFechas ? "  AND pi.fecha_inicial_Prog <= ? " +
                               "  AND pi.fecha_fin_Prog >= ? " : "") +
            "  AND pi.id_programacion_Instructores <> ? " +
            "LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int idx = 1;

            ps.setInt(idx++, prog.getAmbientes_id_ambientes());
            ps.setInt(idx++, prog.getTrimestre_id_trimestre());
            ps.setString(idx++, prog.getDias_Semana());
            ps.setObject(idx++, prog.getHora_fin());
            ps.setObject(idx++, prog.getHora_inicio());
            if (verificarFechas) {
                ps.setObject(idx++, prog.getFecha_fin_Prog());
                ps.setObject(idx++, prog.getFecha_inicial_Prog());
            }
            ps.setInt(idx, idIgnorar);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String instructor = rs.getString("instructorNombre");
                    String ambiente   = rs.getString("ambienteNombre");
                    LocalTime hIni    = rs.getObject("horaInicio", LocalTime.class);
                    LocalTime hFin    = rs.getObject("horaFin",    LocalTime.class);
                    return "El ambiente " + (ambiente != null ? ambiente : "") +
                        " ya está ocupado por el instructor " + (instructor != null ? instructor : "") +
                        " de " + hIni + " a " + hFin + ".";
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al verificar conflicto de horario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }
        return null;
    }

    public boolean InsertarProgramacion_Instructores(Programacion_Instructores prog) {
        boolean insertado = false;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();

        String descripcionConflicto = obtenerDescripcionConflicto(prog, 0);
        if (descripcionConflicto != null) {
            throw new ConflictoHorarioException(descripcionConflicto, null);
        }

        String sql =
            "INSERT INTO programacion_instructores " +
            "(Observaciones, fecha_inicial_Prog, fecha_fin_Prog, " +
            " dias_Semana, hora_inicio, hora_fin, Ficha_id_ficha, Usuarios_id_usuarios, " +
            " Ambientes_id_ambientes, Trimestre_id_trimestre, Estado_id_estado, Actividades_id_actividades) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, prog.getObservaciones());
            ps.setObject(2, prog.getFecha_inicial_Prog());
            ps.setObject(3, prog.getFecha_fin_Prog());
            ps.setString(4, prog.getDias_Semana());
            ps.setObject(5, prog.getHora_inicio());
            ps.setObject(6, prog.getHora_fin());
            ps.setInt(7,    prog.getFicha_id_ficha());
            ps.setInt(8,    prog.getUsuarios_id_usuarios());
            ps.setInt(9,    prog.getAmbientes_id_ambientes());
            ps.setInt(10,   prog.getTrimestre_id_trimestre());
            ps.setInt(11,   prog.getEstado_id_estado());
            ps.setInt(12,   prog.getActividades_id_actividades());
            ps.executeUpdate();
            insertado = true;
            System.out.println("✅ Programación insertada correctamente.");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("❌ Conflicto de horario al insertar: " + e.getMessage());
            throw new ConflictoHorarioException(
                "Ya existe una programación para ese ambiente, día y hora.", e);
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar programación: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException ignored) {}
        }

        return insertado;
    }

    // ══════════════════════════════════════════════════════════════
    //  CONSULTAR (por ID)
    // ══════════════════════════════════════════════════════════════

    public Programacion_Instructores consultaProgramacion_Instructores(int id) {
        Programacion_Instructores encontrado = null;
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();

        if (con == null) return null;

        String sql =
            "SELECT id_programacion_Instructores, Observaciones, fecha_inicial_Prog, " +
            "       fecha_fin_Prog, dias_Semana, hora_inicio, hora_fin, Ficha_id_ficha, " +
            "       Usuarios_id_usuarios, Ambientes_id_ambientes, " +
            "       Trimestre_id_trimestre, Estado_id_estado, Actividades_id_actividades " +
            "FROM programacion_instructores WHERE id_programacion_Instructores = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                encontrado = new Programacion_Instructores();
                encontrado.setId_programacion_Instructores(rs.getInt("id_programacion_Instructores"));
                encontrado.setObservaciones(rs.getString("Observaciones"));
                encontrado.setFecha_inicial_Prog(rs.getObject("fecha_inicial_Prog", LocalDate.class));
                encontrado.setFecha_fin_Prog(rs.getObject("fecha_fin_Prog",   LocalDate.class));
                encontrado.setDias_Semana(rs.getString("dias_Semana"));
                encontrado.setHora_inicio(rs.getObject("hora_inicio", LocalTime.class));
                encontrado.setHora_fin(rs.getObject("hora_fin",   LocalTime.class));
                encontrado.setFicha_id_ficha(rs.getInt("Ficha_id_ficha"));
                encontrado.setUsuarios_id_usuarios(rs.getInt("Usuarios_id_usuarios"));
                encontrado.setAmbientes_id_ambientes(rs.getInt("Ambientes_id_ambientes"));
                encontrado.setTrimestre_id_trimestre(rs.getInt("Trimestre_id_trimestre"));
                encontrado.setEstado_id_estado(rs.getInt("Estado_id_estado"));
                encontrado.setActividades_id_actividades(rs.getInt("Actividades_id_actividades"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al consultar programación: " + e.getMessage());
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }

        return encontrado;
    }

    // ══════════════════════════════════════════════════════════════
    //  ELIMINAR
    // ══════════════════════════════════════════════════════════════

    public boolean eliminarProgramacion_Instructores(int id) {
        String sql = "DELETE FROM programacion_instructores WHERE id_programacion_Instructores = ?";
        Conexion conexion = new Conexion();

        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar programación: " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  ACTUALIZAR
    // ══════════════════════════════════════════════════════════════

    public boolean actualizarProgramacion_Instructores(Programacion_Instructores prog) {
        String descripcionConflicto = obtenerDescripcionConflicto(prog, prog.getId_programacion_Instructores());
        if (descripcionConflicto != null) {
            throw new ConflictoHorarioException(descripcionConflicto, null);
        }

        String sql =
            "UPDATE programacion_instructores SET " +
            "  Observaciones = ?, fecha_inicial_Prog = ?, fecha_fin_Prog = ?, " +
            "  dias_Semana = ?, hora_inicio = ?, hora_fin = ?, Ficha_id_ficha = ?, " +
            "  Usuarios_id_usuarios = ?, Ambientes_id_ambientes = ?, " +
            "  Trimestre_id_trimestre = ?, Estado_id_estado = ?, Actividades_id_actividades = ? " +
            "WHERE id_programacion_Instructores = ?";

        Conexion conexion = new Conexion();

        try (Connection con = conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, prog.getObservaciones());
            ps.setObject(2, prog.getFecha_inicial_Prog());
            ps.setObject(3, prog.getFecha_fin_Prog());
            ps.setString(4, prog.getDias_Semana());
            ps.setObject(5, prog.getHora_inicio());
            ps.setObject(6, prog.getHora_fin());
            ps.setInt(7,    prog.getFicha_id_ficha());
            ps.setInt(8,    prog.getUsuarios_id_usuarios());
            ps.setInt(9,    prog.getAmbientes_id_ambientes());
            ps.setInt(10,   prog.getTrimestre_id_trimestre());
            ps.setInt(11,   prog.getEstado_id_estado());
            ps.setInt(12,   prog.getActividades_id_actividades());
            ps.setInt(13,   prog.getId_programacion_Instructores());

            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("✅ Programación actualizada correctamente.");
            return ok;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("❌ Conflicto de horario al actualizar: " + e.getMessage());
            throw new ConflictoHorarioException(
                "Ya existe una programación para ese ambiente, día y hora.", e);
        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar programación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}