package com.example.timad.poznavacka.activities.test;

public class PoznavackaDbObject {
    String name;
    String id;
    String content;

    public PoznavackaDbObject(String name, String id, String content) {
        this.name = name;
        this.id = id;
        this.content = content;
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
}
