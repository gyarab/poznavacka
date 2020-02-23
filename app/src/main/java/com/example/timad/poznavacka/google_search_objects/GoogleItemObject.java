package com.example.timad.poznavacka.google_search_objects;

public class GoogleItemObject {
    private String kind;
    private String title;
    private String htmlTitle;
    private String link;
    private String displayLink;
    private String snippet;
    private String htmlSnippet;
    private String cacheId;
    private String formattedUrl;
    private String htmlFormattedUrl;
    //private ArrayList<GoogleImagesInfo> pagemap = new ArrayList<>();
    private GoogleImagesInfo pagemap;

    public String getKind() {
        return kind;
    }

    public String getTitle() {
        return title;
    }

    public String getHtmlTitle() {
        return htmlTitle;
    }

    public String getLink() {
        return link;
    }

    public String getDisplayLink() {
        return displayLink;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getHtmlSnippet() {
        return htmlSnippet;
    }

    public String getCacheId() {
        return cacheId;
    }

    public String getFormattedUrl() {
        return formattedUrl;
    }

    public String getHtmlFormattedUrl() {
        return htmlFormattedUrl;
    }

    public GoogleImagesInfo getPagemap() {
        return pagemap;
    }
}
