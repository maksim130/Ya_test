package com.example.nested.entity;

public class News {

    private String id;
    private String date;
    private String headline;
    private String image;
    private String source;
    private String summary;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public News(String id, String date, String headline, String image, String source, String summary, String url) {
        this.id = id;
        this.date = date;
        this.headline = headline;
        this.image = image;
        this.source = source;
        this.summary = summary;
        this.url = url;
    }
}
