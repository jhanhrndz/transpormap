
package model;

public class Tramo {
    private int idTramo;
    private String inicio_tramo;
    private String fin_tramo;
    private float longitud;
    private int carr_id;

    public Tramo() {
    }

    public Tramo(Tramo nodoTramo) {
        this.idTramo = nodoTramo.idTramo;
        this.longitud = nodoTramo.longitud;
        this.inicio_tramo = nodoTramo.inicio_tramo;
        this.fin_tramo = nodoTramo.fin_tramo;
    }

    public int getIdTramo() {
        return idTramo;
    }

    public void setIdTramo(int idTramo) {
        this.idTramo = idTramo;
    }

    public float getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    public String getInicio_tramo() {
        return inicio_tramo;
    }

    public void setInicio_tramo(String inicio_tramo) {
        this.inicio_tramo = inicio_tramo;
    }

    public String getFin_tramo() {
        return fin_tramo;
    }

    public void setFin_tramo(String fin_tramo) {
        this.fin_tramo = fin_tramo;
    }

    public int getCarr_id() {
        return carr_id;
    }

    public void setCarr_id(int carr_id) {
        this.carr_id = carr_id;
    }
}
