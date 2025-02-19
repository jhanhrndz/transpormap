
package model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class DenunciaDAO {
    private String mensaje;
    
    public String addDenuncia(Connection conn, Denuncia nodoDenu){
        java.sql.Date fechaSQL = new java.sql.Date(nodoDenu.getDen_fecha().getTime());
        PreparedStatement pst = null;
        String sql = "INSERT INTO DENUNCIA (DEN_ID,DEN_FECHA,DEN_DESCRIPCION,TRAMO_ID,ES_ID,LU_ID,USR_ID,EVNT_ID) "
                + "VALUES (SEQ_9_1.NEXTVAL,?,?,?,?,?,?,?)";
        try {
            pst = conn.prepareStatement(sql);
            pst.setDate(1, fechaSQL);
            pst.setString(2, nodoDenu.getDen_descrpcion());
            pst.setInt(3, nodoDenu.getTramo_id());
            pst.setInt(4, nodoDenu.getEs_id());
            pst.setInt(5, nodoDenu.getLu_id());
            pst.setInt(6, nodoDenu.getUsr_id());
            pst.setInt(7, nodoDenu.getEvnt_id());
            mensaje = "Nuevo usuario guardado";
            pst.execute();
            pst.close();
        } catch (Exception e) {
            System.out.println(e);
            mensaje = "Error en registro.\n"+e.getMessage();
        }
        return mensaje;  
    }
    
    public String setDenuncia(Connection conn, Denuncia nodoDenu){
        PreparedStatement pst = null;
        String sql = "UPDATE DENUNCIA SET DEN_FECHA = ?, DEN_DESCRIPCION = ?, TRAMO_ID = ?, ES_ID = ?,LU_ID = ?, USR_ID = ?, EVNT_ID = ?"
                + "WHERE DEN_ID = ?";
        try {
            pst = conn.prepareStatement(sql);
            pst.setDate(1, (Date) nodoDenu.getDen_fecha());
            pst.setString(2, nodoDenu.getDen_descrpcion());
            pst.setInt(3, nodoDenu.getTramo_id());
            pst.setInt(4, nodoDenu.getEs_id());
            pst.setInt(5, nodoDenu.getLu_id());
            pst.setInt(6, nodoDenu.getUsr_id());
            pst.setInt(7, nodoDenu.getEvnt_id());
            pst.setInt(8, nodoDenu.getIdDen());
            mensaje = "Actualizado correctamente";
            pst.execute();
            pst.close();
        } catch (Exception e) {
            mensaje = "Error en registro.\n"+e.getMessage();
        }
        return mensaje;  
    }
    
    public String deleteDenuncia(Connection conn, int idDenu){
        PreparedStatement pst = null;
        String sql = "DELETE FROM DENUNCIA WHERE DEN_ID = ?";
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, idDenu);
            mensaje = "Denuncia eliminada";
            pst.execute();
            pst.close();
        } catch (Exception e) {
            mensaje = "Error en registro.\n"+e.getMessage();
        }
        return mensaje;  
    }
    
    public List<Denuncia> getDenuncia(Connection conn) {
        List<Denuncia> denun = new ArrayList<>();
        try {
            String sql = "SELECT * FROM DENUNCIA";
            try (PreparedStatement pst = conn.prepareStatement(sql); ResultSet resultSet = pst.executeQuery()) {
                while (resultSet.next()) {
                    Denuncia nodoLuDe = new Denuncia();
                    nodoLuDe.setIdDen(resultSet.getInt("DEN_ID"));
                    nodoLuDe.setDen_fecha(resultSet.getDate("DEN_FECHA"));
                    nodoLuDe.setDen_descrpcion(resultSet.getString("DEN_DESCRIPCION"));
                    nodoLuDe.setTramo_id(resultSet.getInt("TRAMO_ID"));
                    nodoLuDe.setEs_id(resultSet.getInt("ES_ID"));
                    nodoLuDe.setLu_id(resultSet.getInt("LU_ID"));
                    nodoLuDe.setUsr_id(resultSet.getInt("USR_ID"));
                    nodoLuDe.setEvnt_id(resultSet.getInt("EVNT_ID"));
                    denun.add(nodoLuDe);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(null, "Error en la carga de los datos.\n" + e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        }
        return denun;
    }
}
