
package model;

public class Estado_Via {
    private int idES;
    private String es_nombre;

    public Estado_Via() {
    }

    public Estado_Via(Estado_Via eVia) {
        this.idES = eVia.idES;
        this.es_nombre = eVia.es_nombre;
    }

    public int getIdES() {
        return idES;
    }

    public void setIdES(int idES) {
        this.idES = idES;
    }

    public String getEs_nombre() {
        return es_nombre;
    }

    public void setEs_nombre(String es_nombre) {
        this.es_nombre = es_nombre;
    }
    
    
}
