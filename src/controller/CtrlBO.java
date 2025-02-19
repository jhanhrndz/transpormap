
package controller;

import connectionbd.ConnectionOracle;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import javax.swing.JOptionPane;
import model.CtrlDAO;

public class CtrlBO {
    private CtrlDAO ctrl = new CtrlDAO();
    
    public int buscarID( String nombreTabla, String nomID, String nombreColumna, String valorABuscar){
        int id = -1;
        Connection conn = ConnectionOracle.conectar();
        try {
            id = ctrl.buscarID(conn, nombreTabla, nomID, nombreColumna, valorABuscar);
        } catch (SQLException e) {
            //JOptionPane.showMessageDialog(null, "Error en la transaccion\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
        } finally{
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                //JOptionPane.showMessageDialog(null, "Error en la transaccion\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println(e);
            }
        }
        return id;
    }
    
    public int buscarIDfloat( String nombreTabla, String nomID, String nombreColumna, float valorABuscar){
        int id = -1;
        Connection conn = ConnectionOracle.conectar();
        try {
            id = ctrl.buscarIDfloat(conn, nombreTabla, nomID, nombreColumna, valorABuscar);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en la transaccion\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally{
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error en la transaccion\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return id;
    }
    
    public int buscarIDTramo(String nombreColumna1, String nombreColumna2, String valorABuscar1, String valorString2){
        int id = -1;
        Connection conn = ConnectionOracle.conectar();
        try {
            id = ctrl.buscarIDtramo(conn, nombreColumna1, nombreColumna2, valorABuscar1, valorString2);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en la transaccion\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally{
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error en la transaccion\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return id;
    }
    
    public static long getIDs(long minValue, long maxValue) {
        UUID uniqueID = UUID.randomUUID();
        long uniqueNumber = uniqueID.getMostSignificantBits() & Long.MAX_VALUE;
        return (uniqueNumber % (maxValue - minValue + 1)) + minValue;
    }
}
