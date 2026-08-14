package com.example.perbana.db.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "alert", strict = false)
public class Cap {
    @Element(name = "identifier", required = false)
    private String identifier;
    @Element(name = "sender", required = false)
    private String sender;
    @Element(name = "status", required = false)
    private String status;
    @Element(name = "info", required = false)
    private DetailWeatherWarning info;

    public Cap() {
    }

    public Cap(String identifier, String sender, String status, DetailWeatherWarning info) {
        this.identifier = identifier;
        this.sender = sender;
        this.status = status;
        this.info = info;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getSender() {
        return sender;
    }

    public String getStatus() {
        return status;
    }

    public DetailWeatherWarning getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "Cap{" +
                "identifier='" + identifier + '\'' +
                ", sender='" + sender + '\'' +
                ", status='" + status + '\'' +
                ", info=" + info +
                '}';
    }
}
