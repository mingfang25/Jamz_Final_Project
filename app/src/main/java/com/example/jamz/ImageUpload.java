package com.example.jamz;

public class ImageUpload {
    public String username;
    public String name;
    public String url;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public ImageUpload(String name, String url, String username) {
        this.name = name;
        this.url = url;
        this.username = username;
    }

    public ImageUpload(){

    }
}
