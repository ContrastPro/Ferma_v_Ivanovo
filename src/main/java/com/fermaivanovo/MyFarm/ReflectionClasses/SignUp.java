package com.fermaivanovo.MyFarm.ReflectionClasses;

public class SignUp {

    private String login, email, image, type;

    public SignUp(){}

    public SignUp(String login, String email, String image, String type) {
        this.login = login;
        this.email = email;
        this.image = image;
        this.type = type;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
