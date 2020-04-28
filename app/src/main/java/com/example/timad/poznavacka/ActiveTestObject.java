package com.example.timad.poznavacka;

/**
 * objekt aktivního testu
 */
public class ActiveTestObject {
   private String content;
   private String testCode;
   private String userID;
   private String testDBID;

    public ActiveTestObject() {
    }

    public ActiveTestObject(String content, String testCode, String userID, String testDBID) {
        this.content = content;
        this.testCode = testCode;
        this.userID = userID;
        this.testDBID = testDBID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTestDBID() {
        return testDBID;
    }

    public void setTestDBID(String testDBID) {
        this.testDBID = testDBID;
    }
}
