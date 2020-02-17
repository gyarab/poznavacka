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
    private String imageURL;

    //pridani azstupce
    public Zastupce(int parameters, Drawable image, String imageURL, ArrayList<String> args) {
        infoArr = new ArrayList<>();
        this.parameters = parameters;
        infoArr.addAll(args);
        this.image = image; // obrazek
        this.imageURL = imageURL;
    }

    //pridani classification
    public Zastupce(int parameters, ArrayList<String> args) { //parameters v konstruktoru nejsou potreba?
        infoArr = new ArrayList<>();
        this.parameters = parameters;
        infoArr.addAll(args);
    }

    //pokud se pridava jen jmeno zastupce
    public Zastupce(int parameters, String arg) { //parameters v konstruktoru nejsou potreba?
        infoArr = new ArrayList<>();
        this.parameters = parameters;
        infoArr.add(arg);
        //if needed to fill the infoArr with empty Strings
        for (int i = 1; i < parameters; i++) {
            infoArr.add("");
        }
    }

    //Pokud se pridava jen jmeno zastupce s obrazkem
    public Zastupce(int parameters, Drawable image, String imageURL, String arg) { //parameters v konstruktoru nejsou potreba?
        infoArr = new ArrayList<>();
        this.parameters = parameters;
        infoArr.add(arg);
        //if needed to fill the infoArr with empty Strings
        for (int i = 1; i < parameters; i++) {
            infoArr.add("");
        }
        this.image = image;
        this.imageURL = imageURL;
    }

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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(Drawable image) {
        this.image = image;
    }

    public String toString(){
        String s = Integer.toString(parameters) + ": ";
        for (int i = 0; i < parameters; i ++){
            s += infoArr.get(i) + ", ";
        }
        return s;
    }
}
