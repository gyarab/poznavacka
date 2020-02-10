package com.example.timad.poznavacka;

public class PrewiewPoznavacka {
    private int imageRecource;
    private String name;
    private String id;

    public PrewiewPoznavacka(int imageRecource, String name, String id) {
        this.imageRecource = imageRecource;
        this.name = name;
        this.id = id;
    }

    public PrewiewPoznavacka() {
    }

    int getImageRecource() {
        return imageRecource;
    }

    public void setImageRecource(int imageRecource) {
        this.imageRecource = imageRecource;
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
}
