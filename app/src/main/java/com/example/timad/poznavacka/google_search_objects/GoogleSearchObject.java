package com.example.timad.poznavacka.google_search_objects;

import java.util.ArrayList;

public class GoogleSearchObject {
    private String kind;
    Url UrlObject;
    Queries QueriesObject;
    SearchInformation SearchInformationObject;
    Context ContextObject;
    ArrayList<GoogleItemObject> items = new ArrayList<>();

    public ArrayList<GoogleItemObject> getItems() {
        return items;
    }

    public void setItems(ArrayList<GoogleItemObject> items) {
        this.items = items;
    }


    // Getter Methods

    public String getKind() {
        return kind;
    }

    public Url getUrl() {
        return UrlObject;
    }

    public Queries getQueries() {
        return QueriesObject;
    }

    public Context getContext() {
        return ContextObject;
    }

    public SearchInformation getSearchInformation() {
        return SearchInformationObject;
    }

    // Setter Methods

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setUrl(Url urlObject) {
        this.UrlObject = urlObject;
    }

    public void setQueries(Queries queriesObject) {
        this.QueriesObject = queriesObject;
    }

    public void setContext(Context contextObject) {
        this.ContextObject = contextObject;
    }

    public void setSearchInformation(SearchInformation searchInformationObject) {
        this.SearchInformationObject = searchInformationObject;
    }
}

