package com.example.mycontactsapplication.Models;

public class UserModel {
    String userId,name,imageUri;

    public UserModel() {
    }

    public UserModel(String userId, String name, String imageUri) {
        this.userId = userId;
        this.name = name;
        this.imageUri = imageUri;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
