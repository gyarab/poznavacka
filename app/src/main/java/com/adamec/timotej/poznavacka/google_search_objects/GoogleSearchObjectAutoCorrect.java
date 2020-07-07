package com.adamec.timotej.poznavacka.google_search_objects;

public class GoogleSearchObjectAutoCorrect {
    private String kind;
    private Url url;
    private Queries queries;
    private SearchInformation searchInformation;
    private Spelling spelling;

    @Override
    public String toString() {
        return "GoogleSearchObjectAutoCorrect{" +
                "kind='" + kind + '\'' +
                ", url=" + url +
                ", queries=" + queries +
                ", searchInformation=" + searchInformation +
                ", spelling=" + spelling +
                '}';
    }

    public Spelling getSpelling() {
        return spelling;
    }
}
