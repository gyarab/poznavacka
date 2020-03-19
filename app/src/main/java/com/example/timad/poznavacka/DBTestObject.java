package com.example.timad.poznavacka;

public class DBTestObject {
    private String name;
    private String content;
    private String userID;
    private String previewImageUrl;
    private boolean started;
    private boolean finished;

    public DBTestObject() {
    }

    public DBTestObject(String name, String content, String userID, String previewImageUrl, boolean started, boolean finished) {
        this.name = name;
        this.content = content;
        this.userID = userID;
        this.previewImageUrl = previewImageUrl;
        this.started = started;
        this.finished = finished;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPreviewImageUrl() {
        return previewImageUrl;
    }

    public void setPreviewImageUrl(String previewImageUrl) {
        this.previewImageUrl = previewImageUrl;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
