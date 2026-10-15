package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    public Connection getConexion() {
        Connection con = null;
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://switchback.proxy.rlwy.net:12083/railway?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "PyzGmfeJAtUkQEYDuKDjGjiOHuOJolDe";

        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Conexión establecida correctamente.");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("❌ Error SQL: " + e.getMessage());
        }
        return con;
    }
}