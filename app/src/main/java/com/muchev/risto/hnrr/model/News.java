package com.muchev.risto.hnrr.model;

/**
 * Created by Risto on 5/30/2016.
 */
public class News {
    private String title;
    private String link;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return this.title;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof News){
            return this.title.equals(((News) o).getTitle());
        }
        return false;
    }
}