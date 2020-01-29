package com.example.timad.poznavacka;

/** Polozka v seznamu poznavacek **/
public class PoznavackaInfo {
    private String name;
    private String info;

    public PoznavackaInfo(String name, String info){
        this.name = name;
        this.info = info;
    }

    public String getName(){
        return name;
    }

    public String getInfo(){
        return info;
    }

}
