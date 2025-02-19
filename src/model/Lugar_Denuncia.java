
package model;

public class Lugar_Denuncia {
    private int lu_id;
    private float latutud;
    private float longitud;

    public Lugar_Denuncia() {
    }

    public Lugar_Denuncia(Lugar_Denuncia nodo) {
        this.lu_id = nodo.lu_id;
        this.latutud = nodo.latutud;
        this.longitud = nodo.longitud;
    }

    public int getLu_id() {
        return lu_id;
    }

    public void setLu_id(int lu_id) {
        this.lu_id = lu_id;
    }

    public float getLatutud() {
        return latutud;
    }

    public void setLatutud(float latutud) {
        this.latutud = latutud;
    }

    public float getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }
    
    
}
