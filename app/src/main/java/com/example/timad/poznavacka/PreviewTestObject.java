package com.example.timad.poznavacka;

public class PreviewTestObject {
    private String name;
    private boolean started;
    private String previewImgUrl;
    private String databaseID;
    private String userID;
    private String content;

    public PreviewTestObject() {
    }

    public PreviewTestObject(String name, boolean started, String previewImgUrl, String databaseID, String userID, String content) {
        this.name = name;
        this.started = started;
        this.previewImgUrl = previewImgUrl;
        this.databaseID = databaseID;
        this.userID = userID;
        this.content = content;
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

    @Override
    public String toString() {
        return "PreviewTestObject{" +
                "name='" + name + '\'' +
                ", started=" + started +
                ", previewImgUrl='" + previewImgUrl + '\'' +
                ", databaseID='" + databaseID + '\'' +
                ", userID='" + userID + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
