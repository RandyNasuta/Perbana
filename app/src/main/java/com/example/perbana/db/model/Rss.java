package com.example.perbana.db.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.List;

@Root(name = "rss", strict = false)
public class Rss implements Serializable {
    @Element(name = "channel")
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }

    @Root(strict = false)
    public static class Channel {
        @Element(name = "lastBuildDate", required = false)
        private String lastBuildDate;

        @ElementList(inline = true, name = "item")
        private List<WeatherWarning> itemList;

        public String getLastBuildDate() {
            return lastBuildDate;
        }

        public List<WeatherWarning> getItemList() {
            return itemList;
        }
    }
}
