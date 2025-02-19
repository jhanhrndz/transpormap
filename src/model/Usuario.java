
package model;

import java.util.Date;

public class Usuario {
    private int idUsuario;
    private String usu_Contrasena;
    private String usu_Correo;
    private String usu_Nombre;
    private String usu_Apellido;
    private Date usu_Fechanac;
    private long usu_telefono;
    private int idRol;
    private int idMun;

    public Usuario() {
    }

    public Usuario(Usuario nodo) {
        this.idUsuario = nodo.idUsuario;
        this.usu_Contrasena = nodo.usu_Contrasena;
        this.usu_Correo = nodo.usu_Correo;
        this.usu_Nombre = nodo.usu_Nombre;
        this.usu_Apellido = nodo.usu_Apellido;
        this.usu_Fechanac = nodo.usu_Fechanac;
        this.usu_telefono = nodo.usu_telefono;
        this.idRol = nodo.idRol;
        this.idMun = nodo.idMun;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getUsu_Contrasena() {
        return usu_Contrasena;
    }

    public void setUsu_Contrasena(String usu_Contrasena) {
        this.usu_Contrasena = usu_Contrasena;
    }

    public String getUsu_Correo() {
        return usu_Correo;
    }

    public void setUsu_Correo(String usu_Correo) {
        this.usu_Correo = usu_Correo;
    }

    public String getUsu_Nombre() {
        return usu_Nombre;
    }

    public void setUsu_Nombre(String usu_Nombre) {
        this.usu_Nombre = usu_Nombre;
    }

    public String getUsu_Apellido() {
        return usu_Apellido;
    }

    public void setUsu_Apellido(String usu_Apellido) {
        this.usu_Apellido = usu_Apellido;
    }

    public long getUsu_telefono() {
        return usu_telefono;
    }

    public void setUsu_telefono(Long usu_telefono) {
        this.usu_telefono = usu_telefono;
    }
    
    public int getIdRol() {
        return idRol;
    }

    public int getIdMun() {
        return idMun;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public void setIdMun(int idMun) {
        this.idMun = idMun;
    }

    public Date getUsu_Fechanac() {
        return usu_Fechanac;
    }

    public void setUsu_Fechanac(Date usu_Fechanac) {
        this.usu_Fechanac = usu_Fechanac;
    }
    
}
