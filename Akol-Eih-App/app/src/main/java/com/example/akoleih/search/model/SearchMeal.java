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

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    // Getters

    /**
     * Returns the meal’s unique ID.
     * Used by the details fragment to fetch full meal info.
     */
    public String getIdMeal() {
        return id;
    }

    /**
     * Legacy getter for ID if needed elsewhere.
     */
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
