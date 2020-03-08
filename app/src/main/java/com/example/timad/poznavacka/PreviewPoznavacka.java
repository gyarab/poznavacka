package com.example.timad.poznavacka;

public class PreviewPoznavacka {
    private String imageRecource;
    private String name;
    private String id;
    private String authorsName;
    private  String authorsUuid;

    public PreviewPoznavacka() {
    }

    public PreviewPoznavacka(String imageRecource, String name, String id, String authorsName, String authorsUuid) {
        this.imageRecource = imageRecource;
        this.name = name;
        this.id = id;
        this.authorsName = authorsName;
        this.authorsUuid = authorsUuid;
    }

    public String getImageRecource() {
        return imageRecource;
    }

    public void setImageRecource(String imageRecource) {
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

    public String getAuthorsUuid() {
        return authorsUuid;
    }

    public void setAuthorsUuid(String authorsUuid) {
        this.authorsUuid = authorsUuid;
    }
}
