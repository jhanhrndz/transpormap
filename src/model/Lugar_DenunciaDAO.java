package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Lugar_DenunciaDAO {

    private String mensaje;

    public String addLugar_Denuncia(Connection conn, Lugar_Denuncia nodoLuDe) {
        PreparedStatement pst = null;
        String sql = "INSERT INTO  LUGAR_DENUNCIA (LU_ID,LU_LATITUD,LU_LONGITUD) "
                + "VALUES (SEQ_3_1.NEXTVAL,?,?)";
        try {
            pst = conn.prepareStatement(sql);
            pst.setFloat(1, nodoLuDe.getLatutud());
            pst.setFloat(2, nodoLuDe.getLongitud());
            mensaje = "Nuevo usuario guardado";
            pst.execute();
            pst.close();
        } catch (SQLException e) {
            mensaje = "Error en registro.\n" + e.getMessage();
            System.out.println(e);
        }
        return mensaje;
    }

    public List<Lugar_Denuncia> getLugar_Denuncia(Connection conn) {
        List<Lugar_Denuncia> luDe = new ArrayList<>();
        try {
            String sql = "SELECT * FROM LUGAR_DENUNCIA";
            try (PreparedStatement pst = conn.prepareStatement(sql); ResultSet resultSet = pst.executeQuery()) {
                while (resultSet.next()) {
                    Lugar_Denuncia nodoLuDe = new Lugar_Denuncia();
                    nodoLuDe.setLu_id(resultSet.getInt("LU_ID"));
                    nodoLuDe.setLatutud(resultSet.getFloat("LU_LATITUD"));
                    nodoLuDe.setLongitud(resultSet.getFloat("LU_LONGITUD"));
                    luDe.add(nodoLuDe);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(null, "Error en la carga de los datos.\n" + e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        }
        return luDe;
    }

    public String deleteLugar_denuncia(Connection conn, int idDenu) {
        return mensaje;
    }

    public void setLugar_Denuncia() {

    }
}
