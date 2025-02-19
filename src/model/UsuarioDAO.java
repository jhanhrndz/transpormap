
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

public class UsuarioDAO {
    private String mensaje;
    
    public String addUsuario(Connection conn, Usuario nodoUsu){
        java.sql.Date fechaSQL = new java.sql.Date(nodoUsu.getUsu_Fechanac().getTime());
        PreparedStatement pst = null;
        String sql = "INSERT INTO USUARIO (USR_ID,USR_NOMBRE,USR_CORREO,USR_CONTRASEÑA,USR_FECHANAC,USR_APELLIDO,USR_TELEFONO,MUN_ID,ROL_ID) "
                + "VALUES (SEQ_1_1.NEXTVAL,?,?,?,?,?,?,?,?)";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, nodoUsu.getUsu_Nombre());
            pst.setString(2, nodoUsu.getUsu_Correo());
            pst.setString(3, nodoUsu.getUsu_Contrasena());
            pst.setDate(4, fechaSQL);
            pst.setString(5, nodoUsu.getUsu_Apellido());
            pst.setLong(6, nodoUsu.getUsu_telefono());
            pst.setInt(7, nodoUsu.getIdMun());
            pst.setInt(8, nodoUsu.getIdRol());
            mensaje = "Nuevo usuario guardado";
            pst.execute();
            pst.close();
        } catch (SQLException e) {
            mensaje = "Error en registro.\n"+e.getMessage();
            System.out.println(e);
        }
        return mensaje;  
    }
    
    public Usuario getUsuario(Connection conn, int idUsu){
        Usuario usuario = null;
        try {
            String sql = "SELECT * FROM USUARIO WHERE USR_ID = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, idUsu);
                try (ResultSet resultSet = pst.executeQuery()) {
                    if (resultSet.next()) {
                        usuario = new Usuario();
                        usuario.setIdUsuario(resultSet.getInt("USR_ID"));
                        usuario.setUsu_Nombre(resultSet.getString("USR_NOMBRE"));
                        usuario.setUsu_Correo(resultSet.getString("USR_CORREO"));
                        usuario.setUsu_Contrasena(resultSet.getString("USR_CONTRASEÑA"));
                        usuario.setUsu_Fechanac(resultSet.getDate("USR_FECHANAC"));
                        usuario.setUsu_Apellido(resultSet.getString("USR_APELLIDO"));
                        usuario.setUsu_telefono(resultSet.getLong("USR_TELEFONO"));
                        usuario.setIdMun(resultSet.getInt("MUN_ID"));
                        usuario.setIdRol(resultSet.getInt("MUN_ID"));
                    } 
                } catch (Exception e) {System.out.println("Error en "+e.getMessage());}
            } 
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(null, "Error en la carga de los datos.\n"+e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        }
        return usuario; 
    }
    
    public String deleteUsuario(Connection conn, int idDenu){
        return mensaje;  
    }
    
    public void setUsuario(){
         
    }
}
