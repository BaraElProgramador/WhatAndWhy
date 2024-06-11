package com.example.whatwhy.Modelos;

public class Respuestas {
    private String texto;
    private boolean correcta;

    public Respuestas() {
        this.texto = "";
        this.correcta = false;
    }

    public Respuestas(String texto, boolean correcto) {
        this.texto = texto;
        this.correcta = correcto;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public boolean isCorrecta() {
        return correcta;
    }

    public void setCorrecta(boolean correcta) {
        this.correcta = correcta;
    }
}
