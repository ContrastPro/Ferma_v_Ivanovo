package com.fermaivanovo.MyFarm.ReflectionClasses;

public class PostNews {

    private String image, title, description, date, code;

    public PostNews(){
    }

    public PostNews(String image, String title, String description, String date, String code) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.date = date;
        this.code = code;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
