
package model;

public class Evento {
    private int idEvento;
    private String event_nombre;

    public Evento() {
    }

    public Evento(Evento evt) {
        this.idEvento = evt.idEvento;
        this.event_nombre = evt.event_nombre;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public String getEvent_nombre() {
        return event_nombre;
    }

    public void setEvent_nombre(String event_nombre) {
        this.event_nombre = event_nombre;
    }
    
    
}
