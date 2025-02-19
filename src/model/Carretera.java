
package model;

public class Carretera {
    private int idCarretera;
    private String nombre;
    private int logitud;
    private int inicioCarretera;
    private int finCarretera;

    public Carretera() {
    }

    public Carretera(int idCarretera, String nombre, int logitud, int inicioCarretera, int finCarretera) {
        this.idCarretera = idCarretera;
        this.nombre = nombre;
        this.logitud = logitud;
        this.inicioCarretera = inicioCarretera;
        this.finCarretera = finCarretera;
    }

    public int getIdCarretera() {
        return idCarretera;
    }

    public void setIdCarretera(int idCarretera) {
        this.idCarretera = idCarretera;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getLogitud() {
        return logitud;
    }

    public void setLogitud(int logitud) {
        this.logitud = logitud;
    }

    public int getInicioCarretera() {
        return inicioCarretera;
    }

    public void setInicioCarretera(int inicioCarretera) {
        this.inicioCarretera = inicioCarretera;
    }

    public int getFinCarretera() {
        return finCarretera;
    }

    public void setFinCarretera(int finCarretera) {
        this.finCarretera = finCarretera;
    }
    
}
