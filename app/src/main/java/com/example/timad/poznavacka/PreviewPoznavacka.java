package com.example.timad.poznavacka;

public class PreviewPoznavacka {
    private int imageRecource;
    private String name;
    private String id;

    public PreviewPoznavacka(int imageRecource, String name, String id) {
        this.imageRecource = imageRecource;
        this.name = name;
        this.id = id;
    }

    public PreviewPoznavacka() {
    }

    public Integer getImageRecource() {
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
