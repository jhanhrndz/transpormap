package connectionbd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConnectionOracle {
    private static Connection conn;
    private static String url = System.getenv("TRANSPORMAP_LEGACY_ORACLE_URL");
    private static String user = System.getenv("TRANSPORMAP_LEGACY_ORACLE_USER");
    private static String pass = System.getenv("TRANSPORMAP_LEGACY_ORACLE_PASSWORD");
    private static String driver = "oracle.jdbc.OracleDriver";

    public ConnectionOracle() {
        conn = null;
    }
    
    public static Connection conectar(){
        try {
            if (url == null || url.isBlank() || user == null || user.isBlank() || pass == null || pass.isBlank()) {
                throw new SQLException("La conexión Oracle heredada no está configurada.");
            }
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, pass);
            if (conn != null) {
                System.out.println("Conexion exitosa!");
            }else{
                System.out.println("Conexion erronea!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudo abrir la conexión Oracle heredada.");
            System.err.println("No se pudo abrir la conexión Oracle heredada.");
        }
        return conn;
    }
    
    public void desconectar(){
        try {
            conn.close();
        } catch (Exception e) {
            System.out.println("No se pudo desconectar!\n"+ e.getMessage());
        }
    }
    
}
