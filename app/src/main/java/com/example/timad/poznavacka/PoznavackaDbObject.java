package com.example.timad.poznavacka;

public class PoznavackaDbObject {
    private String name;
    private String userName;
    private String content;

    public PoznavackaDbObject(String name, String userName, String content) {
        this.name = name;
        this.userName = userName;
        this.content = content;
    }

    public PoznavackaDbObject() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
