package com.fermaivanovo.MyFarm.ReflectionClasses;

public class PostGoods {

    String image;
    String title;
    String availability;
    String price;
    String description;
    String composition;
    String term;
    String code;

    public PostGoods() {
    }

    public PostGoods(String image, String title, String availability, String price, String description, String composition, String term, String code) {
        this.image = image;
        this.title = title;
        this.availability = availability;
        this.price = price;
        this.description = description;
        this.composition = composition;
        this.term = term;
        this.code = code;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getAvailability() {
        return availability;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getComposition() {
        return composition;
    }

    public String getTerm() {
        return term;
    }

    public String getCode() {
        return code;
    }
}
