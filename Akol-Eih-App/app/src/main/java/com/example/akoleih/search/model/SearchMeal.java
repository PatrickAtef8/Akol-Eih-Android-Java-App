package com.example.akoleih.search.model;

public class SearchMeal {
    private String id;
    private String name;
    private String thumbnail;

    public SearchMeal(String id, String name, String thumbnail) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getThumbnail() { return thumbnail; }
}