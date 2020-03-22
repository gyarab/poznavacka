package com.example.timad.poznavacka;

public class PreviewResultObject {
    private String userID;
    private String result;
    private String databaseID;

    public PreviewResultObject() {
    }

    public PreviewResultObject(String userID, String result, String databaseID) {
        this.userID = userID;
        this.result = result;
        this.databaseID = databaseID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDatabaseID() {
        return databaseID;
    }

    public void setDatabaseID(String databaseID) {
        this.databaseID = databaseID;
    }
}
