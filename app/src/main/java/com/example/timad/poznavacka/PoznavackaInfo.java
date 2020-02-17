package com.example.timad.poznavacka;

/** Polozka v seznamu poznavacek **/
public class PoznavackaInfo {
    private String name;
    private String id;
    private String author;

    public PoznavackaInfo(String name, String id, String author){
        this.name = name;
        this.id = id;
        this.author = author;
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

}
