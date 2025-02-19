
package model;

public class Departamento {
    private int idDep;
    private String dep_nombre;

    public Departamento() {
    }

    public Departamento(int idDep, String dep_nombre) {
        this.idDep = idDep;
        this.dep_nombre = dep_nombre;
    }

    public int getIdDep() {
        return idDep;
    }

    public void setIdDep(int idDep) {
        this.idDep = idDep;
    }

    public String getDep_nombre() {
        return dep_nombre;
    }

    public void setDep_nombre(String dep_nombre) {
        this.dep_nombre = dep_nombre;
    }
    
    
}
