package com.example.android.newsapp;

import java.util.Date;

public class News {
    private String mTitle;
    private String mSection;
    private String mAuthor;
    private String mUrl;
    private Date mPublishDate;

    public News() {

    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setSection(String section) {
        mSection = section;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setPublishDate(Date publishDate) {
        mPublishDate = publishDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSection() {
        return mSection;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getUrl() {
        return mUrl;
    }

    public Date getPublishDate() {
        return mPublishDate;
    }
}
