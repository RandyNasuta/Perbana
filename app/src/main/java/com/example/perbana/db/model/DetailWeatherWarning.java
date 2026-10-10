package com.example.perbana.db.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "info", strict = false)
public class DetailWeatherWarning {
    @Element(name = "event", required = false)
    private String event;
    @Element(name = "effective", required = false)
    private String effective;
    @Element(name = "urgency", required = false)
    private String urgency;
    @Element(name = "severity", required = false)
    private String severity;
    @Element(name = "certainty", required = false)
    private String certainty;
    @Element(name = "expires", required = false)
    private String expires;
    @Element(name = "senderName", required = false)
    private String senderName;
    @Element(name = "headline", required = false)
    private String headline;
    @Element(name = "description", required = false)
    private String description;
    @Element(name = "web", required = false)
    private String web;
    @Element(name = "area", required = false)
    private Area area;

    public DetailWeatherWarning() {
    }

    public DetailWeatherWarning(String event, String urgency, String effective, String severity, String certainty, String expires, String senderName, String headline, String description, String web, Area area) {
        this.event = event;
        this.urgency = urgency;
        this.effective = effective;
        this.severity = severity;
        this.certainty = certainty;
        this.expires = expires;
        this.senderName = senderName;
        this.headline = headline;
        this.description = description;
        this.web = web;
        this.area = area;
    }

    public String getEvent() {
        return event;
    }

    public String getEffective() {
        return effective;
    }

    public String getUrgency() {
        if (urgency != null) {
            if (urgency.equals("Immediate")) {
                return "Segera";
            } else if (urgency.equals("Expected")) {
                return "Diharapkan";
            } else if (urgency.equals("Future")) {
                return "Akan Datang";
            } else if (urgency.equals("Past")) {
                return "Telah Berlalu";
            } else {
                return "Unknown";
            }
        }
        return urgency;
    }

    public String getSeverity() {
        if (severity != null) {
            if (severity.equals("Extreme")) {
                return "Ekstrem";
            } else if (severity.equals("Severe")) {
                return "Signifikan";
            } else if (severity.equals("Moderate")) {
                return "Sedang";
            } else if (severity.equals("Minor")) {
                return "Ringan";
            } else {
                return "Tidak Diketahui";
            }
        }
        return severity;
    }

    public String getCertainty() {
        if (certainty != null) {
            if (certainty.equals("Observed")) {
                return "Teramati";
            } else if (certainty.equals("Likely")) {
                return "Mungkin Terjadi";
            } else if (certainty.equals("Possible")) {
                return "Bisa Jadi";
            } else if (certainty.equals("Unlikely")) {
                return "Tidak Menjanjikan";
            } else {
                return "Tidak Diketahui";
            }
        }
        return certainty;
    }

    public String getExpires() {
        return expires;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getHeadline() {
        return headline;
    }

    public String getDescription() {
        return description;
    }

    public String getWeb() {
        return web;
    }

    public Area getArea() {
        return area;
    }

    @Override
    public String toString() {
        return "DetailWeatherWarning{" +
                "event='" + event + '\'' +
                ", urgency='" + urgency + '\'' +
                ", effective='" + effective + '\'' +
                ", severity='" + severity + '\'' +
                ", certainty='" + certainty + '\'' +
                ", expires='" + expires + '\'' +
                ", senderName='" + senderName + '\'' +
                ", headline='" + headline + '\'' +
                ", description='" + description + '\'' +
                ", web='" + web + '\'' +
                ", area=" + area +
                '}';
    }
}
