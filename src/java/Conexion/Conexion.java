package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    public Connection getConexion() {
        String url = requerir("DB_URL");
        String user = requerir("DB_USER");
        String password = requerir("DB_PASSWORD");

        url = forzarTls(url);

        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Driver MySQL no encontrado.", e);
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo conectar a la base de datos.", e);
        }
    }

    private static String requerir(String nombre) {
        String valor = System.getenv(nombre);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Falta la variable de entorno " + nombre + ".");
        }
        return valor.trim();
    }

    private static String forzarTls(String url) {
        String resultado = url.replace("allowPublicKeyRetrieval=true", "allowPublicKeyRetrieval=false");
        if (resultado.contains("useSSL=false")) {
            resultado = resultado.replace("useSSL=false", "useSSL=true");
        } else if (!resultado.contains("useSSL=")) {
            resultado += (resultado.contains("?") ? "&" : "?") + "useSSL=true";
        }
        if (!resultado.contains("characterEncoding=")) {
            resultado += "&characterEncoding=UTF-8";
        }
        return resultado;
    }
}
