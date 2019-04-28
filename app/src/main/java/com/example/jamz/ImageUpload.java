package com.example.jamz;

public class ImageUpload {
    public String username;
    public String name;
    public String url;
    public String type;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public ImageUpload(String name, String url, String username, String type) {
        this.name = name;
        this.url = url;
        this.username = username;
        this.type = type;
    }

    public ImageUpload(){

    }
}
