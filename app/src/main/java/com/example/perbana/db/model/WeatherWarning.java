package com.example.perbana.db.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

@Root(name = "item", strict = false)
public class WeatherWarning implements Serializable {
    @Element(name = "title", required = false)
    private String title;
    @Element(name = "link", required = false)
    private String link;
    @Element(name = "description", required = false)
    private String description;
    @Element(name = "author", required = false)
    private String author;
    @Element(name = "pubDate", required = false)
    private String pubDate;
    @Element(name = "lastBuildDate", required = false)
    private String lastBuildDate;

    public WeatherWarning() {}

    public WeatherWarning(String title, String link, String description, String author, String pubDate, String lastBuildDate) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.author = author;
        this.pubDate = pubDate;
        this.lastBuildDate = lastBuildDate;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    @Override
    public String toString() {
        return "WeatherWarning{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", author='" + author + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", lastBuildDate='" + lastBuildDate + '\'' +
                '}';
    }
}
