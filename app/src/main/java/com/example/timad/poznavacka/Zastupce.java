package com.example.timad.poznavacka;

import android.graphics.drawable.Drawable;

/** Položka v poznávačce **/
public class Zastupce {
    private String zastupce;
    private String druh;
    private String kmen;
    private Drawable image; // obrazek

    public Zastupce(String zastupce, String druh, String kmen/*, Drawable image*/) { //+img
        this.zastupce = zastupce;
        this.druh = druh;
        this.kmen = kmen;
        //this.image = image; // obrazek
    }

    public String getZastupce(){
        return zastupce;
    }

    public void setZastupce(String zastupce) {
        this.zastupce = zastupce;
    }

    public String getDruh(){
        return druh;
    }

    public void setDruh(String druh) {
        this.druh = druh;
    }

    public String getKmen(){
        return kmen;
    }

    public void setKmen(String kmen) {
        this.kmen = kmen;
    }

    //obrazek
    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

}
