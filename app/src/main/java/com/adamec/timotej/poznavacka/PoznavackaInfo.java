package com.adamec.timotej.poznavacka;

/**
 *  Polozka v seznamu poznavacek
 */
public class PoznavackaInfo {
    private String name;
    private String lowerCaseName;
    private String id;
    private String author;
    private String authorsID;
    private String prewievImageLocation;
    private String prewievImageUrl;
    private String languageURL;
    private int representativesCount;
    private boolean uploaded;

    /**
     * Konstruktor pro vytvoření poznávačky
     * @param name
     * @param id
     * @param author
     * @param authorsID
     * @param prewievImageLocation
     * @param prewievImageUrl
     * @param languageURL
     * @param uploaded
     */
    public PoznavackaInfo(String name, String id, String author, String authorsID, String prewievImageLocation, String prewievImageUrl, String languageURL, int representativesCount, boolean uploaded) {
        this.name = name;
        this.lowerCaseName = name.toLowerCase();
        this.id = id;
        this.author = author;
        this.authorsID = authorsID;
        this.prewievImageLocation = prewievImageLocation;
        this.prewievImageUrl = prewievImageUrl;
        this.languageURL = languageURL;
        this.representativesCount = representativesCount;
        this.uploaded = uploaded;
    }

    public int getRepresentativesCount() {
        return representativesCount;
    }

    public void setRepresentativesCount(int representativesCount) {
        this.representativesCount = representativesCount;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorsID(){
        return authorsID;
    }

    public void setPrewievImageLocation(String prewievImageLocation){ this.prewievImageLocation = prewievImageLocation; }

    public String getPrewievImageLocation(){
        return prewievImageLocation;
    }

    public void setPrewievImageUrl(String prewievImageUrl){ this.prewievImageUrl = prewievImageUrl; }

    public String getPrewievImageUrl(){
        return prewievImageUrl;
    }

    public String getLanguageURL() {
        return languageURL;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLowerCaseName() {
        return lowerCaseName;
    }

    public void setLowerCaseName(String lowerCaseName) {
        this.lowerCaseName = lowerCaseName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAuthorsID(String authorsID) {
        this.authorsID = authorsID;
    }

    public void setLanguageURL(String languageURL) {
        this.languageURL = languageURL;
    }
}
