package com.example.whatwhy.Modelos;

public class Reporte {
    private String userID, projectID, tipo, moreInfo, estado;

    public Reporte() {
    }

    public Reporte(String userID, String projectID, String motivo, String moreInfo, String estado) {
        this.userID = userID;
        this.projectID = projectID;
        this.tipo = motivo;
        this.moreInfo = moreInfo;
        this.estado = estado;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getMotivo() {
        return tipo;
    }

    public void setMotivo(String motivo) {
        this.tipo = motivo;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
