
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class EventoDAO {
     private String mensaje;
    
    public String addEvento(Connection conn, Evento nodoEvt){

        return mensaje;  
    }
    
    public Evento getUsuario(Connection conn, int idEvt){
        Evento evento = null;
        try {
            String sql = "SELECT * FROM EVENTO WHERE EVNT_ID = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, idEvt);
                try (ResultSet resultSet = pst.executeQuery()) {
                    if (resultSet.next()) {
                        evento = new Evento();
                        evento.setIdEvento(resultSet.getInt("EVNT_ID"));
                        evento.setEvent_nombre(resultSet.getString("EVNT_NOMBRE"));
                    } 
                } catch (Exception e) {System.out.println("Error en "+e.getMessage());}
            } 
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(null, "Error en la carga de los datos.\n"+e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        }
        return evento; 
    }
    
    public String deleteUsuario(Connection conn, int idDenu){
        return mensaje;  
    }
    
    public void listUsuario(){
         
    }
}
