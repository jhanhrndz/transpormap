
package controller;

import connectionbd.ConnectionOracle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.Lugar_Denuncia;
import model.Lugar_DenunciaDAO;
import model.Usuario;

public class Lugar_DenunciaBO {
    private String mensaje;
    private Lugar_DenunciaDAO ludeDao = new Lugar_DenunciaDAO();

    public String addLugar_Denuncia(Lugar_Denuncia nodoLude) {
        try (Connection conn = ConnectionOracle.conectar()) {
            conn.setAutoCommit(false);
            mensaje = ludeDao.addLugar_Denuncia(conn, nodoLude);
            conn.commit();
        } catch (SQLException e) {
            mensaje += e.getMessage();
        }
        return mensaje;
    }

    public List<Lugar_Denuncia> getLugar_Denuncia() {
        List<Lugar_Denuncia> luDe = new ArrayList<>();
        try (Connection conn = ConnectionOracle.conectar()) {
            conn.setAutoCommit(false);
            luDe = ludeDao.getLugar_Denuncia(conn);
            conn.commit();
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(null, "Error en la conexion.\n" + e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        }
        return luDe;
    }

    public String deleteLugar_denuncia(Connection conn, int idDenu) {
        return mensaje;
    }

    public void setLugar_Denuncia() {

    }
}
