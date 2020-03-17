package com.example.timad.poznavacka;

public class TestObject {
    private String name;
    private String testCode;
    private boolean started;
    private String databaseID;

    public TestObject() {
    }

    public TestObject(String name, String testCode, boolean started, String databaseID) {
        this.name = name;
        this.testCode = testCode;
        this.started = started;
        this.databaseID = databaseID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public String getDatabaseID() {
        return databaseID;
    }

    public void setDatabaseID(String databaseID) {
        this.databaseID = databaseID;
    }
}
