package com.example.timad.poznavacka;

/** Polozka v seznamu poznavacek **/
public class PoznavackaInfo {
    private String name;
    private String id;

    public PoznavackaInfo(String name, String id){
        this.name = name;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }

}
