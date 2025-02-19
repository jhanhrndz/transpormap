
package model;

import java.util.Date;

public class Denuncia {
    private int idDen;
    private boolean den_estado;
    private String den_descrpcion;
    private Date den_fecha;
    private int tramo_id;
    private int es_id;
    private int lu_id;
    private int usr_id;
    private int evnt_id;

    public Denuncia() {
    }

    public Denuncia(Denuncia nodoDenu) {
        this.idDen = nodoDenu.getIdDen();
        this.den_descrpcion = nodoDenu.getDen_descrpcion();
        this.den_fecha = nodoDenu.getDen_fecha();
        this.tramo_id = nodoDenu.getTramo_id();
        this.es_id = nodoDenu.getEs_id();
        this.lu_id = nodoDenu.getLu_id();
        this.usr_id = nodoDenu.getUsr_id();
        this.evnt_id = nodoDenu.getEvnt_id();
    }

    public int getIdDen() {
        return idDen;
    }

    public void setIdDen(int idDen) {
        this.idDen = idDen;
    }

    public boolean isDen_estado() {
        return den_estado;
    }

    public void setDen_estado(boolean den_estado) {
        this.den_estado = den_estado;
    }

    public String getDen_descrpcion() {
        return den_descrpcion;
    }

    public void setDen_descrpcion(String den_descrpcion) {
        this.den_descrpcion = den_descrpcion;
    }

    public Date getDen_fecha() {
        return den_fecha;
    }

    public void setDen_fecha(Date den_fecha) {
        this.den_fecha = den_fecha;
    }

    public int getTramo_id() {
        return tramo_id;
    }

    public void setTramo_id(int tramo_id) {
        this.tramo_id = tramo_id;
    }

    public int getEs_id() {
        return es_id;
    }

    public void setEs_id(int es_id) {
        this.es_id = es_id;
    }

    public int getLu_id() {
        return lu_id;
    }

    public void setLu_id(int lu_id) {
        this.lu_id = lu_id;
    }

    public int getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(int usr_id) {
        this.usr_id = usr_id;
    }

    public int getEvnt_id() {
        return evnt_id;
    }

    public void setEvnt_id(int evnt_id) {
        this.evnt_id = evnt_id;
    }
    
    
}
