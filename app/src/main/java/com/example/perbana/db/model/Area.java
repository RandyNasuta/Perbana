package com.example.perbana.db.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "area", strict = false)
public class Area {
    @Element(name = "areaDesc", required = false)
    private String areaDesc;

    public Area() {
    }

    public Area(String areaDesc) {
        this.areaDesc = areaDesc;
    }

    public String getAreaDesc() {
        return areaDesc;
    }

    @Override
    public String toString() {
        return "Area{" +
                "areaDesc='" + areaDesc + '\'' +
                '}';
    }
}
