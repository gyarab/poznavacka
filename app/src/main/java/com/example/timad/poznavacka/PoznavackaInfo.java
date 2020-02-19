package com.example.timad.poznavacka;

/** Polozka v seznamu poznavacek **/
public class PoznavackaInfo {
    private String name;
    private String id;
    private String author;
    private String prewievImageLocation;
    private String prewievImageUrl;

    public PoznavackaInfo(String name, String id, String author){
        this.name = name;
        this.id = id;
        this.author = author;
    }

    public PoznavackaInfo(String name, String id, String author, String prewievImageLocation, String prewievImageUrl){
        this.name = name;
        this.id = id;
        this.author = author;
        this.prewievImageLocation = prewievImageLocation;
        this.prewievImageUrl = prewievImageUrl;
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

    public void setPrewievImageLocation(String prewievImageLocation){ this.prewievImageLocation = prewievImageLocation; }

    public String getPrewievImageLocation(){
        return prewievImageLocation;
    }

    public void setPrewievImageUrl(String prewievImageUrl){ this.prewievImageUrl = prewievImageUrl; }

    public String getPrewievImageUrl(){
        return prewievImageUrl;
    }
}
