package com.example.timad.poznavacka;

public class DBTestObject {
    private String name;
    private String classification;
    private String content;
    private String userID;
    private String previewImageUrl;
    private boolean started;
    private boolean finished;
    private String activeTestID;
    private String testCode;
    private boolean resultsEmpty;

    public DBTestObject() {
    }

    public DBTestObject(String name, String classification, String content, String userID, String previewImageUrl, boolean started, boolean finished, String activeTestID, String testCode, boolean resultsEmpty) {
        this.name = name;
        this.classification = classification;
        this.content = content;
        this.userID = userID;
        this.previewImageUrl = previewImageUrl;
        this.started = started;
        this.finished = finished;
        this.activeTestID = activeTestID;
        this.testCode = testCode;
        this.resultsEmpty = resultsEmpty;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
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

    public String getActiveTestID() {
        return activeTestID;
    }

    public void setActiveTestID(String activeTestID) {
        this.activeTestID = activeTestID;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public boolean isResultsEmpty() {
        return resultsEmpty;
    }

    public void setResultsEmpty(boolean resultsEmpty) {
        this.resultsEmpty = resultsEmpty;
    }
}
