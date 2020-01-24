package com.example.timad.poznavacka;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Položka v poznávačce
 **/
public class Zastupce {
    private static String TAG = "Zastupce";

    private ArrayList<String> infoArr;
    private int parameters;
    private Drawable image; // obrazek

    public Zastupce(int parameters, Drawable image, ArrayList<String> args) {
        infoArr = new ArrayList<>();
        this.parameters = parameters;
        infoArr.addAll(args);
        this.image = image; // obrazek
    }

    //pridani 1. nebo regulerniho zastupce
    public Zastupce(int parameters, ArrayList<String> args) { //parameters v konstruktoru nejsou potreba?
        infoArr = new ArrayList<>();
        this.parameters = parameters;
        infoArr.addAll(args);
    }

/*    //Pokud se pridava jen jmeno zastupce
    public Zastupce(int parameters, String arg) { //parameters v konstruktoru nejsou potreba?
        infoArr = new ArrayList<>();
        this.parameters = parameters;
        infoArr.add(arg);
        //if needed to fill the infoArr with empty Strings
        *//*for (int i = 1; i < parameters; i++) {
            infoArr.add("");
        }*//*
    }*/

    public String getParameter(int pos) {
        Log.d(TAG, "v infoArr - " + infoArr.toString() + "  position wanted is - " + pos);
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
