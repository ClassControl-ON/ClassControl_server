package Controlador;

import Conexion.Conexion;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Gestiona los tokens de restablecimiento de contraseña.
 *
 * El token se guarda en la base de datos SOLO como hash SHA-256 para que un
 * robo de la BD no permita adivinar enlaces válidos, y se permite usar una
 * sola vez dentro de un plazo de expiración.
 */
public class RestablecerTokenDAO {

    private static final SecureRandom AZAR = new SecureRandom();
    private static final String CARACTERES =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /** Genera un token aleatorio seguro (por defecto 32 bytes -> 64 hex). */
    public String generarToken() {
        return generarToken(32);
    }

    public String generarToken(int numBytes) {
        byte[] b = new byte[numBytes];
        AZAR.nextBytes(b);
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte by : b) {
            sb.append(String.format("%02x", by));
        }
        return sb.toString();
    }

    /** Hash SHA-256 hexadecimal del token. */
    public String hash(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(d.length * 2);
            for (byte by : d) {
                sb.append(String.format("%02x", by));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    /**
     * Guarda un token nuevo para el usuario y devuelve su fecha de expiración.
     * Los tokens anteriores del mismo usuario se marcan como usados para
     * invalidarlos cuando se hace una nueva solicitud.
     */
    public Timestamp guardar(Integer idUsuario, String token, int minutosValidez) {
        String hash = hash(token);
        LocalDateTime expira = LocalDateTime.now().plusMinutes(minutosValidez);
        String sqlInsert = "INSERT INTO restablecer_token "
                + "(Usuarios_id_usuarios, token_hash, expira_en, usado) "
                + "VALUES (?, ?, ?, 0)";
        String sqlInvalidar = "UPDATE restablecer_token SET usado = 1 "
                + "WHERE Usuarios_id_usuarios = ?";
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            return null;
        }
        try {
            con.setAutoCommit(false);
            try (PreparedStatement inv = con.prepareStatement(sqlInvalidar)) {
                inv.setInt(1, idUsuario);
                inv.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                ps.setInt(1, idUsuario);
                ps.setString(2, hash);
                ps.setTimestamp(3, Timestamp.valueOf(expira));
                ps.executeUpdate();
            }
            con.commit();
            return Timestamp.valueOf(expira);
        } catch (SQLException e) {
            System.out.println("RestablecerTokenDAO - error al guardar token: " + e.getMessage());
            try { con.rollback(); } catch (SQLException ex) { /* nada */ }
            return null;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException e) { /* nada */ }
            try { con.close(); } catch (SQLException e) { /* nada */ }
        }
    }

    /**
     * Valida un token (existente, no usado y sin expirar) SIN consumirlo.
     * Devuelve el id de usuario o null si no es válido.
     * Útil para el GET que muestra el formulario sin gastar el token.
     */
    public Integer validar(String token) {
        return buscarYMarcar(token, false);
    }

    /**
     * Valida un token y, si es válido, lo marca como usado (un solo uso).
     * Devuelve el id de usuario o null si no es válido. Se usa en el POST,
     * cuando realmente se guarda la nueva contraseña.
     */
    public Integer validarYUso(String token) {
        return buscarYMarcar(token, true);
    }

    private Integer buscarYMarcar(String token, boolean marcarUsado) {
        String hash = hash(token);
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            return null;
        }
        Integer idUsuario = null;
        try {
            String sqlSelect = "SELECT Usuarios_id_usuarios FROM restablecer_token "
                    + "WHERE token_hash = ? AND usado = 0 AND expira_en > NOW() "
                    + "ORDER BY id_token DESC LIMIT 1";
            try (PreparedStatement ps = con.prepareStatement(sqlSelect)) {
                ps.setString(1, hash);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idUsuario = rs.getInt("Usuarios_id_usuarios");
                    }
                }
            }
            if (idUsuario != null && marcarUsado) {
                String sqlUsar = "UPDATE restablecer_token SET usado = 1 WHERE token_hash = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlUsar)) {
                    ps.setString(1, hash);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("RestablecerTokenDAO - error al validar token: " + e.getMessage());
            idUsuario = null;
        } finally {
            try { con.close(); } catch (SQLException e) { /* nada */ }
        }
        return idUsuario;
    }

    /** Limpia tokens expirados (evita que la tabla crezca sin límite). */
    public void limpiarExpirados() {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConexion();
        if (con == null) {
            return;
        }
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM restablecer_token WHERE expira_en <= NOW()")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("RestablecerTokenDAO - error al limpiar token expirados: " + e.getMessage());
        } finally {
            try { con.close(); } catch (SQLException e) { /* nada */ }
        }
    }
}
