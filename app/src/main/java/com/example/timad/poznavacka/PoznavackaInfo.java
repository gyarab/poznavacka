package com.example.timad.poznavacka;

/**
 *  Polozka v seznamu poznavacek
 */
public class PoznavackaInfo {
    private String name;
    private String id;
    private String author;
    private String authorsID;
    private String prewievImageLocation;
    private String prewievImageUrl;
    private String languageURL;
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
    public PoznavackaInfo(String name, String id, String author, String authorsID, String prewievImageLocation, String prewievImageUrl, String languageURL, boolean uploaded) {
        this.name = name;
        this.id = id;
        this.author = author;
        this.authorsID = authorsID;
        this.prewievImageLocation = prewievImageLocation;
        this.prewievImageUrl = prewievImageUrl;
        this.languageURL = languageURL;
        this.uploaded = uploaded;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }

    public String getAuthor(){
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
}
