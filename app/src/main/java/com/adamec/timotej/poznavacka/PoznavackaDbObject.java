package com.adamec.timotej.poznavacka;

public class PoznavackaDbObject {
    private String name;
    private String lowerCaseName;
    private String id;
    private String content;
    private String classification;
    private String authorsName;
    private String authorsID;
    private String headImageUrl;
    private String headImagePath;
    private String languageURL;
    private int representativesCount;
    private long timeUploaded;

    public PoznavackaDbObject(String name, String id, String classification, String content, String authorsName, String authorsID, String headImageUrl, String headImagePath, String languageURL, int representativesCount, long timeUploaded) {
        this.name = name;
        this.lowerCaseName = name.toLowerCase();
        this.id = id;
        this.classification = classification;
        this.content = content;
        this.authorsName = authorsName;
        this.authorsID = authorsID;
        this.headImageUrl = headImageUrl;
        this.headImagePath = headImagePath;
        this.languageURL = languageURL;
        this.representativesCount = representativesCount;
        this.timeUploaded = timeUploaded;
    }

    public int getRepresentativesCount() {
        return representativesCount;
    }

    public void setRepresentativesCount(int representativesCount) {
        this.representativesCount = representativesCount;
    }

    public String getLowerCaseName() {
        return lowerCaseName;
    }

    public void setLowerCaseName(String lowerCaseName) {
        this.lowerCaseName = lowerCaseName;
    }

    public long getTimeUploaded() {
        return timeUploaded;
    }

    public void setTimeUploaded(long timeUploaded) {
        this.timeUploaded = timeUploaded;
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

    public String getAuthorsID() {
        return authorsID;
    }

    public void setAuthorsID(String authorsID) {
        this.authorsID = authorsID;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public String getHeadImagePath() {
        return headImagePath;
    }

    public void setHeadImagePath(String headImagePath) {
        this.headImagePath = headImagePath;
    }

    public String getLanguageURL() {
        return languageURL;
    }

    public void setLanguageURL(String languageURL) {
        this.languageURL = languageURL;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }
}
