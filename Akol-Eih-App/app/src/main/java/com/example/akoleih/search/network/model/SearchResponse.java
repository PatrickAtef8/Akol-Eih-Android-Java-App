package com.example.akoleih.search.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

public class SearchResponse {
    @SerializedName("meals")
    private List<MealResponse> meals;

    // Proper constructor with null check
    public SearchResponse(List<MealResponse> meals) {
        this.meals = meals != null ? meals : Collections.emptyList();
    }

    // Factory method for empty responses
    public static SearchResponse empty() {
        return new SearchResponse(Collections.emptyList());
    }

    public List<MealResponse> getMeals() {
        return meals;
    }

    public static class MealResponse {
        @SerializedName("idMeal")
        private String idMeal;
        @SerializedName("strMeal")
        private String strMeal;
        @SerializedName("strMealThumb")
        private String strMealThumb;

        // Getters
        public String getIdMeal() { return idMeal; }
        public String getStrMeal() { return strMeal; }
        public String getStrMealThumb() { return strMealThumb; }
    }
}