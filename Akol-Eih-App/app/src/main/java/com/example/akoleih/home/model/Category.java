// Category.java
package com.example.akoleih.home.model;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("idCategory")
    private String id;
    @SerializedName("strCategory")
    private String name;
    @SerializedName("strCategoryThumb")
    private String thumbnail;
    @SerializedName("strCategoryDescription")
    private String description;

    // Default constructor for frameworks like Gson
    public Category() {
    }

    // Optional convenience constructor
    public Category(String id, String name, String thumbnail, String description) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}