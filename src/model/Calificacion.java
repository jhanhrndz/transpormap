
package model;

import java.util.Date;

public class Calificacion {
    private int idCalificacion;
    private String cali_discripcion;
    private int cali_valor;
    private Date cali_fecha;

    public Calificacion() {
    }

    public int getIdCalificacion() {
        return idCalificacion;
    }

    public void setIdCalificacion(int idCalificacion) {
        this.idCalificacion = idCalificacion;
    }

    public String getCali_discripcion() {
        return cali_discripcion;
    }

    public void setCali_discripcion(String cali_discripcion) {
        this.cali_discripcion = cali_discripcion;
    }

    public int getCali_valor() {
        return cali_valor;
    }

    public void setCali_valor(int cali_valor) {
        this.cali_valor = cali_valor;
    }

    public Date getCali_fecha() {
        return cali_fecha;
    }

    public void setCali_fecha(Date cali_fecha) {
        this.cali_fecha = cali_fecha;
    }
    
    
    
}
