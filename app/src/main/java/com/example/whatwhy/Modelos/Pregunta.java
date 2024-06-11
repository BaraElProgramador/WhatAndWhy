package com.example.whatwhy.Modelos;

import com.google.firebase.firestore.DocumentReference;
import java.util.ArrayList;

public class Pregunta {
    private String pregunta;
    private String id;
    private ArrayList<Respuestas> respuestas;
    private transient DocumentReference reference; //Campo transitorio

    public Pregunta() {
        this.pregunta = "";
        this.respuestas = new ArrayList<>();
    }

    public Pregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public Pregunta(String pregunta, ArrayList<Respuestas> respuestas) {
        this.pregunta = pregunta;
        this.respuestas = respuestas;
    }


    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public ArrayList<Respuestas> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(ArrayList<Respuestas> respuestas) {
        this.respuestas = respuestas;
    }

    public DocumentReference getReference() {
        return reference;
    }

    public void setReference(DocumentReference reference) {
        this.reference = reference;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
