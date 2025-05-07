package com.example.akoleih.home.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Meal {
    @SerializedName("idMeal") private String idMeal;
    @SerializedName("strMeal") private String strMeal;
    @SerializedName("strMealThumb") private String strMealThumb;
    @SerializedName("strInstructions") private String strInstructions;
    @SerializedName("strArea") private String strArea;
    @SerializedName("strTags") private String strTags;
    @SerializedName("strTime") private String strTime;
    @SerializedName("strCategory") private String strCategory;

    private String strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5;
    private String strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10;
    private String strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15;
    private String strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20;
    private String strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5;
    private String strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10;
    private String strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15;
    private String strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20;
    @SerializedName("strYoutube") private String strYoutube;

    public String getYoutubeUrl() { return strYoutube; }
    public String getIdMeal() { return idMeal; }
    public String getName() { return strMeal; }
    public String getThumbnail() { return strMealThumb; }
    public String getInstructions() { return strInstructions; }
    public String getArea() { return strArea; }
    public String getTags() { return strTags != null ? strTags : ""; }
    public String getTime() { return strTime != null ? strTime : ""; }
    public String getCategory() { return strCategory != null ? strCategory : "General"; }

    public String getCalculatedTime() {
        if (!getTime().isEmpty()) return getTime() + " mins";

        int wordCount = getInstructions().split("\\s+").length;
        int stepCount = getInstructions().split("\\.").length;

        // Adjust weights for a lower and more logical scale
        double rawEstimate = (wordCount * 0.12) + (stepCount * 1.2);

        // Round to nearest logical cooking time bucket
        int[] timeBuckets = {15, 20, 25, 30, 60, 90, 120};
        int closestTime = timeBuckets[0];
        double minDiff = Math.abs(rawEstimate - closestTime);

        for (int time : timeBuckets) {
            double diff = Math.abs(rawEstimate - time);
            if (diff < minDiff) {
                minDiff = diff;
                closestTime = time;
            }
        }

        return closestTime + " mins";
    }



    public List<Ingredient> getIngredientList() {
        List<Ingredient> ingredients = new ArrayList<>();
        addIngredientIfValid(ingredients, strIngredient1, strMeasure1);
        addIngredientIfValid(ingredients, strIngredient2, strMeasure2);
        addIngredientIfValid(ingredients, strIngredient3, strMeasure3);
        addIngredientIfValid(ingredients, strIngredient4, strMeasure4);
        addIngredientIfValid(ingredients, strIngredient5, strMeasure5);
        addIngredientIfValid(ingredients, strIngredient6, strMeasure6);
        addIngredientIfValid(ingredients, strIngredient7, strMeasure7);
        addIngredientIfValid(ingredients, strIngredient8, strMeasure8);
        addIngredientIfValid(ingredients, strIngredient9, strMeasure9);
        addIngredientIfValid(ingredients, strIngredient10, strMeasure10);
        addIngredientIfValid(ingredients, strIngredient11, strMeasure11);
        addIngredientIfValid(ingredients, strIngredient12, strMeasure12);
        addIngredientIfValid(ingredients, strIngredient13, strMeasure13);
        addIngredientIfValid(ingredients, strIngredient14, strMeasure14);
        addIngredientIfValid(ingredients, strIngredient15, strMeasure15);
        addIngredientIfValid(ingredients, strIngredient16, strMeasure16);
        addIngredientIfValid(ingredients, strIngredient17, strMeasure17);
        addIngredientIfValid(ingredients, strIngredient18, strMeasure18);
        addIngredientIfValid(ingredients, strIngredient19, strMeasure19);
        addIngredientIfValid(ingredients, strIngredient20, strMeasure20);
        return ingredients;
    }

    private void addIngredientIfValid(List<Ingredient> list, String name, String measure) {
        if (name != null && !name.trim().isEmpty()) {
            String thumbnail = "https://www.themealdb.com/images/ingredients/" + name + "-Small.png";
            list.add(new Ingredient(name, measure != null ? measure : "", thumbnail));
        }
    }
}