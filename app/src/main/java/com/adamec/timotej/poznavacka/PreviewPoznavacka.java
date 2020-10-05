package com.adamec.timotej.poznavacka;

/**
 * objekt poznavakca zobrazeny
 */
public class PreviewPoznavacka {
    private String imageRecource;
    private String name;
    private String id;
    private String authorsName;
    private String authorsUuid;
    private String languageURL;
    private int representativesCount;

    public PreviewPoznavacka() {
    }

    public PreviewPoznavacka(String imageResource, String name, String id, String authorsName, String authorsUuid, String languageURL, int representativesCount) {
        this.imageRecource = imageResource;
        this.name = name;
        this.id = id;
        this.authorsName = authorsName;
        this.authorsUuid = authorsUuid;
        this.languageURL = languageURL;
        this.representativesCount = representativesCount;
    }

    public int getRepresentativesCount() {
        return representativesCount;
    }

    public void setRepresentativesCount(int representativesCount) {
        this.representativesCount = representativesCount;
    }

    public String getLanguageURL() {
        return languageURL;
    }

    public void setLanguageURL(String languageURL) {
        this.languageURL = languageURL;
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
