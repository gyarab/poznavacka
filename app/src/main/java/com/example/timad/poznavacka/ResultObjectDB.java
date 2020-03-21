package com.example.timad.poznavacka;

public class ResultObjectDB {
    private String authorsID;
    private String userID;
    private String result;

    public ResultObjectDB() {
    }

    public ResultObjectDB(String authorsID, String userID, String result) {
        this.authorsID = authorsID;
        this.userID = userID;
        this.result = result;
    }

    public String getAuthorsID() {
        return authorsID;
    }

    public void setAuthorsID(String authorsID) {
        this.authorsID = authorsID;
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
}
