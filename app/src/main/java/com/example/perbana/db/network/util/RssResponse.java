package com.example.perbana.db.network.util;

import com.example.perbana.db.model.WeatherWarning;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "rss", strict = false)
public class RssResponse {
    @Element(name = "channel")
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }

    @Root(strict = false)
    public static class Channel {
        @ElementList(inline = true, name = "item")
        private List<WeatherWarning> itemList;

        public List<WeatherWarning> getItemList() {
            return itemList;
        }
    }
}
