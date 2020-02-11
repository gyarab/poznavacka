package com.example.timad.poznavacka;

public class PreviewPoznavacka {
    private int imageRecource;
    private String name;
    private String id;
    private String authorsName;

    public PreviewPoznavacka() {
    }

    public PreviewPoznavacka(int imageRecource, String name, String id, String authorsName) {
        this.imageRecource = imageRecource;
        this.name = name;
        this.id = id;
        this.authorsName = authorsName;
    }

    public int getImageRecource() {
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

    public String getAuthorsName() {
        return authorsName;
    }

    public void setAuthorsName(String authorsName) {
        this.authorsName = authorsName;
    }
}
