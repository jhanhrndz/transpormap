package controller;

import connectionbd.ConnectionOracle;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import model.Usuario;
import model.UsuarioDAO;

public class UsuarioBO {

    private String mensaje;
    private UsuarioDAO usuDao = new UsuarioDAO();

    public String addUsuario(Usuario nodoUsu) {
        try (Connection conn = ConnectionOracle.conectar()) {
            conn.setAutoCommit(false);
            mensaje = usuDao.addUsuario(conn, nodoUsu);
            conn.commit();
        } catch (SQLException e) {
            mensaje += e.getMessage();
        }
        return mensaje;
    }

    public Usuario getUsuario(int idUsu) {
        Usuario usuario = null;
        try (Connection conn = ConnectionOracle.conectar()) {
            conn.setAutoCommit(false);
            usuario = usuDao.getUsuario(conn, idUsu);
            conn.commit();
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(null, "Error en la conexion.\n" + e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        }
        return usuario;
    }

    public String deleteUsuario(Connection conn, int idDenu) {
        return mensaje;
    }

    public void setUsuario() {

    }
}
