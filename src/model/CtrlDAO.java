
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CtrlDAO {
    public int buscarID(Connection conn, String nombreTabla, String nomVar, String nombreColumna, String valorABuscar) throws SQLException{
        int idEncontrado = -1; 
        String sql = "SELECT "+nomVar+" FROM " + nombreTabla + " WHERE " + nombreColumna + " = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, valorABuscar);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    idEncontrado = resultSet.getInt(nomVar);
                }
            }
        }
        return idEncontrado;
    }
    
    public int buscarIDfloat(Connection conn, String nombreTabla, String nomVar, String nombreColumna, float valorABuscar) throws SQLException{
        int idEncontrado = -1; 
        String sql = "SELECT "+nomVar+" FROM " + nombreTabla + " WHERE " + nombreColumna + " = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setFloat(1, valorABuscar);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    idEncontrado = resultSet.getInt(nomVar);
                }
            }
        }
        return idEncontrado;
    }
    
    public int buscarIDtramo(Connection conn, String nombreColumna1, String nombreColumna2, String valorABuscar1, String valorABuscar2) throws SQLException{
        int idEncontrado = -1; 
        String sql = "SELECT TRAMO_ID FROM TRAMO WHERE" + nombreColumna1 + " = ? AND "+nombreColumna2+ " = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, valorABuscar1);
            statement.setString(2, valorABuscar2);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    idEncontrado = resultSet.getInt("TRAMO_ID");
                }
            }
        }
        return idEncontrado;
    }
}
