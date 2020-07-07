package com.adamec.timotej.poznavacka;

/**
 * objekt výsledku zobrazený
 */
public class PreviewResultObject {
    private String userID;
    private String result;
    private String databaseID;
    private String userName;

    public PreviewResultObject() {
    }

    public PreviewResultObject(String userID, String result, String databaseID, String userName) {
        this.userID = userID;
        this.result = result;
        this.databaseID = databaseID;
        this.userName = userName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
