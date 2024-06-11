package com.example.whatwhy.Modelos;

public class Usuario {
    private String email, nombre, id;
    private int rol;

    public Usuario() {

    }

    public Usuario(String email, String nombre, String id, int rol) {
        this.email = email;
        this.nombre = nombre;
        this.id = id;
        this.rol = rol;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }
}
