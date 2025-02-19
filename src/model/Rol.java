
package model;

public class Rol {
    private int idRol;
    private String idNombre;

    public Rol() {
    }

    public Rol(int idRol, String idNombre) {
        this.idRol = idRol;
        this.idNombre = idNombre;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getIdNombre() {
        return idNombre;
    }

    public void setIdNombre(String idNombre) {
        this.idNombre = idNombre;
    }
    
    
}
