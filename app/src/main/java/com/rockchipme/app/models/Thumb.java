package com.rockchipme.app.models;

/**
 * Created by Android on 03/02/2017.
 */
public class Thumb {

    boolean selected = false;
    String image = "";

    public Thumb(){
    }

    public Thumb(String image, boolean selected){
        this.image = image;
        this.selected = selected;
    }

    public String getImage() {
        return image;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
