package com.example.whatwhy.Modelos;

import java.io.Serializable;

public class Favorito implements Serializable {
    private String userId;
    private String projectId;

    public Favorito() {
        userId = "";
        projectId = "";
    }

    public Favorito(String userId, String projectId) {
        this.userId = userId;
        this.projectId = projectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "Favorito{" +
                "userId='" + userId + '\'' +
                ", projectId='" + projectId + '\'' +
                '}';
    }
}
