package com.example.whatwhy.Modelos;

import java.io.Serializable;
import java.util.ArrayList;

public class Proyecto implements Serializable {
    private String id;
    private String nombre;
    private String userID;
    private ArrayList<Pregunta> tests;
    private String tema;

    public Proyecto(){
        this.id = "";
        this.nombre = "";
        this.userID = "";
        this.tests = new ArrayList<>();
        this.tema = "Default";
    }

    public Proyecto(String nombre, String usuario){
        this.id = "";
        this.nombre = nombre;
        this.userID = usuario;
        this.tests = new ArrayList<>();
        this.tema = "Default";
    }

    public Proyecto(String id,String nombre, String usuario){
        this.id = id;
        this.nombre = nombre;
        this.userID = usuario;
        this.tests = new ArrayList<>();
        this.tema = "Default";
    }

    public Proyecto(String id, String nombre, String userID, ArrayList<Pregunta> tests, String tema) {
        this.id = id;
        this.nombre = nombre;
        this.userID = userID;
        this.tests = tests;
        this.tema = tema;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<Pregunta> getTests() {
        return tests;
    }

    public void setTests(ArrayList<Pregunta> tests) {
        this.tests = tests;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    @Override
    public String toString() {
        return "Proyecto:\n" +
                "\nnombre='" + nombre +
                "\nusuario='" + userID;
    }
}
