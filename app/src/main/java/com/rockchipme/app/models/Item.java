package com.rockchipme.app.models;

/**
 * Created by Android on 2/9/2017.
 */
public class Item {

    String name = "", image = "";
    Double price = 0.00;

    public Item(){
    }

    public Item(String name, String image, Double price){
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public Double getPrice() {
        return price;
    }
}
