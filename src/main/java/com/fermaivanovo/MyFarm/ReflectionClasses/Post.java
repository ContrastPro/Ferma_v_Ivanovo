package com.fermaivanovo.MyFarm.ReflectionClasses;

public class Post {

    private String title, content, date;
    private float rating;

    public Post(){}

    public Post(String title, String content, String date, float rating) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
