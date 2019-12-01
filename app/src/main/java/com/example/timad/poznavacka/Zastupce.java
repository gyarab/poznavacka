package com.example.timad.poznavacka;

/** Položka v poznávačce **/
public class Zastupce {
    private String zastupce;
    private String druh;
    private String kmen;
    // obrazek

    public Zastupce(String zastupce, String druh, String kmen) { //+img
        this.zastupce = zastupce;
        this.druh = druh;
        this.kmen = kmen;
        // obrazek
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
        return druh;
    }

    public void setKmen(String kmen) {
        this.kmen = kmen;
    }

    //obrazek
}
