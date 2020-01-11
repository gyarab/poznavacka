package com.example.timad.poznavacka;

import android.graphics.drawable.Drawable;
import android.media.Image;

import java.util.ArrayList;

/** Položka v poznávačce **/
public class Zastupce {
    private ArrayList<String> infoArr;
    private int parameters;
    private Drawable image; // obrazek

    public Zastupce(int parameters, Drawable image, String... args) {
        infoArr = new ArrayList<>();
        this.parameters = parameters;
        for (int i = 0; i < parameters; i++) {
            infoArr.add(args[i]);
        }
        this.image = image; // obrazek
    }

    public Zastupce(int parameters, String... args) {
        infoArr = new ArrayList<>();
        this.parameters = parameters;
        for (int i = 0; i < parameters; i++) {
            infoArr.add(args[i]);
        }
    }

    public String getParameter(int pos) {
        return infoArr.get(pos);
    }

    public void setParameter(String info, int pos) {
        infoArr.set(pos, info);
    }

    //obrazek
    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

}
