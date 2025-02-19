
package model;

public class Categoria {
    private int idCategoria;
    private String cat_nombre;

    public Categoria() {
    }

    public Categoria(int idCategoria, String cat_nombre) {
        this.idCategoria = idCategoria;
        this.cat_nombre = cat_nombre;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getCat_nombre() {
        return cat_nombre;
    }

    public void setCat_nombre(String cat_nombre) {
        this.cat_nombre = cat_nombre;
    }
    
    
}
