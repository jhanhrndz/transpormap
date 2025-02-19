
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TramoDAO {
    private String mensaje;
    
    public String addTramo(Connection conn, Tramo nodoTramo){
        PreparedStatement pst = null;
        String sql = "INSERT INTO TRAMO (TRAMO_ID, TRAMO_INICIO,TRAMO_FIN,TRAMO_LONGITUD,CARR_ID) "
                + "VALUES (SEQ_3_1.NEXTVAL,?,?,?,?)";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, nodoTramo.getInicio_tramo());
            pst.setString(2, nodoTramo.getFin_tramo());
            pst.setFloat(3, nodoTramo.getLongitud());
            pst.setInt(4, nodoTramo.getCarr_id());
            mensaje = "Nuevo usuario guardado";
            pst.execute();
            pst.close();
        } catch (SQLException e) {
            mensaje = "Error en registro.\n"+e.getMessage();
            System.out.println(e);
        }
        return mensaje;  
    }
    
    public void getTramo(){
        
    }
    
    public String deleteTramo(Connection conn, int idDenu){
        return mensaje;  
    }
    
    public void listTramo(){
         
    }
}
