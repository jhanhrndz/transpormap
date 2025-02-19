package controller;

import connectionbd.ConnectionOracle;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Denuncia;
import model.DenunciaDAO;

public class DenunciaBO {

    private DenunciaDAO denuDaoo = new DenunciaDAO();
    private String mensaje = "";

    public String addDenuncia(Denuncia denu) {
        try (Connection conn = ConnectionOracle.conectar()) {
            conn.setAutoCommit(false);
            mensaje = denuDaoo.addDenuncia(conn, denu);
            conn.commit();
        } catch (SQLException e) {
            mensaje += e.getMessage();
            System.out.println(e.getMessage());
        }
        return mensaje;
    }

    public String setDenuncia(Denuncia denu) {
        try (Connection conn = ConnectionOracle.conectar()) {
            conn.setAutoCommit(false);
            mensaje = denuDaoo.setDenuncia(conn, denu);
            conn.commit();
        } catch (SQLException e) {
            mensaje += e.getMessage();
        }
        return mensaje;
    }

    public String deleteDenuncia(int idDenu) {
        try (Connection conn = ConnectionOracle.conectar()) {
            conn.setAutoCommit(false);
            mensaje = denuDaoo.deleteDenuncia(conn, idDenu);
            conn.commit();
        } catch (SQLException e) {
            mensaje += e.getMessage();
        }
        return mensaje;
    }

    public List<Denuncia> getDenuncia() {
        List<Denuncia> luDe = new ArrayList<>();
        try (Connection conn = ConnectionOracle.conectar()) {
            conn.setAutoCommit(false);
            luDe = denuDaoo.getDenuncia(conn);
            conn.commit();
        } catch (SQLException e) {
            mensaje += e.getMessage();
        }
        return luDe;
    }
}
