package com.example.timad.poznavacka;

public class PreviewTestObject {
    private String name;
    private boolean started;
    private String previewImgUrl;
    private String databaseID;
    private String userID;
    private String content;
    private boolean finished;
    private String activeTestID;

    public PreviewTestObject() {
    }

    public PreviewTestObject(String name, boolean started, String previewImgUrl, String databaseID, String userID, String content, boolean finished, String activeTestID) {
        this.name = name;
        this.started = started;
        this.previewImgUrl = previewImgUrl;
        this.databaseID = databaseID;
        this.userID = userID;
        this.content = content;
        this.finished = finished;
        this.activeTestID = activeTestID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public String getPreviewImgUrl() {
        return previewImgUrl;
    }

    public void setPreviewImgUrl(String previewImgUrl) {
        this.previewImgUrl = previewImgUrl;
    }

    public String getDatabaseID() {
        return databaseID;
    }

    public void setDatabaseID(String databaseID) {
        this.databaseID = databaseID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getActiveTestID() {
        return activeTestID;
    }

    public void setActiveTestID(String activeTestID) {
        this.activeTestID = activeTestID;
    }
}
