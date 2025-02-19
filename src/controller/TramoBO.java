
package controller;

import connectionbd.ConnectionOracle;
import java.sql.Connection;
import java.sql.SQLException;
import model.Tramo;
import model.TramoDAO;

public class TramoBO {
     private String mensaje;
    private TramoDAO tramoDAO = new TramoDAO();

    public String addTramo(Tramo nodoTramo) {
        try (Connection conn = ConnectionOracle.conectar()) {
            conn.setAutoCommit(false);
            mensaje = tramoDAO.addTramo(conn, nodoTramo);
            conn.commit();
        } catch (SQLException e) {
            mensaje += e.getMessage();
        }
        return mensaje;
    }

    public void getTramo(int idUsu) {
        
    }

    public String deleteTramo(Connection conn, int idDenu) {
        return mensaje;
    }

    public void setTramo() {

    }
}
