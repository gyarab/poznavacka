package com.example.timad.poznavacka.activities.test;

public class PoznavackaDbObject {
    String name;
    String id;
    String content;
    String authorsName;

    public PoznavackaDbObject(String name, String id, String content, String authorsName) {
        this.name = name;
        this.id = id;
        this.content = content;
        this.authorsName = authorsName;
    }

    public PoznavackaDbObject() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorsName() {
        return authorsName;
    }

    public void setAuthorsName(String authorsName) {
        this.authorsName = authorsName;
    }
}
