
package model;

public class Municipio {

    private int idMun;
    private String nun_nombre;

    public Municipio() {
    }

    public Municipio(int idMun, String nun_nombre) {
        this.idMun = idMun;
        this.nun_nombre = nun_nombre;
    }

    public int getIdMun() {
        return idMun;
    }

    public void setIdMun(int idMun) {
        this.idMun = idMun;
    }

    public String getNun_nombre() {
        return nun_nombre;
    }

    public void setNun_nombre(String nun_nombre) {
        this.nun_nombre = nun_nombre;
    }
    
    
    
}
