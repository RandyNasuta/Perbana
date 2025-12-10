package com.example.perbana.model;

public class MainMenu {
    private int drawableId;
    private int stringId;

    public MainMenu(int drawableId, int stringId) {
        this.drawableId = drawableId;
        this.stringId = stringId;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public int getStringId() {
        return stringId;
    }

    public void setStringId(int stringId) {
        this.stringId = stringId;
    }
}
