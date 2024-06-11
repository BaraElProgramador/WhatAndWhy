package com.example.whatwhy.Modelos;

public class Mensaje {
    private String id, mensaje, titulo, idUser, visto;

    public Mensaje() {
    }

    public Mensaje(String id, String mensaje, String titulo, String idUser, String visto) {
        this.id = id;
        this.mensaje = mensaje;
        this.titulo = titulo;
        this.idUser = idUser;
        this.visto = visto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getVisto() {
        return visto;
    }

    public void setVisto(String visto) {
        this.visto = visto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
