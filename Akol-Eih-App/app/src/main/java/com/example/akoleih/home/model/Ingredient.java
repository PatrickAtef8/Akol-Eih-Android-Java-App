package com.example.akoleih.home.model;

public class Ingredient {
    private String name;
    private String measure;
    private String thumbnail;

    public Ingredient(String name, String measure, String thumbnail) {
        this.name = name;
        this.measure = measure;
        this.thumbnail = thumbnail;
    }

    // Getters and setters
    public String getName() { return name; }
    public String getMeasure() { return measure; }
    public String getThumbnail() { return thumbnail; }
}