package com.example.whatwhy.Modelos;

public class Resultado {
    private String userID, projectID;
    private int score;

    public Resultado() {
    }

    public Resultado(String userID, String projectId, int score) {
        this.userID = userID;
        this.projectID = projectId;
        this.score = score;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
