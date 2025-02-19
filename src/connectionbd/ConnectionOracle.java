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
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, pass);
            if (conn != null) {
                System.out.println("Conexion exitosa!");
            }else{
                System.out.println("Conexion erronea!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en conexion. "+e.getMessage()+ e);
            System.out.println(""+e);
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
