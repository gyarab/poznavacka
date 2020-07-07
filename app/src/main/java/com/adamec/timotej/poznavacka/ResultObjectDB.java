package com.adamec.timotej.poznavacka;

/**
 * objek výsledku v databázi
 */
public class ResultObjectDB {
    private String userID;
    private String result;
    private String userName;


    public ResultObjectDB() {
    }

    public ResultObjectDB(String userID, String result, String userName) {
        this.userID = userID;
        this.result = result;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
