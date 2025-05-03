// Meal.java
package com.example.akoleih.home.model;

import java.util.ArrayList;
import java.util.List;

public class Meal {
    private String idMeal;
    private String strMeal;
    private String strMealThumb;
    private String strInstructions;
    private String strArea;


    // Ingredients (1–20)
    private String strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5;
    private String strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10;
    private String strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15;
    private String strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20;

    // Measures (1–20)
    private String strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5;
    private String strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10;
    private String strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15;
    private String strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20;
    private String strYoutube;

    // Add getter
    public String getYoutubeUrl() {
        return strYoutube;
    }
    public String getIdMeal() {
        return idMeal;
    }

    public String getName() {
        return strMeal;
    }

    public String getThumbnail() {
        return strMealThumb;
    }

    public String getInstructions() {
        return strInstructions;
    }

    public String getArea() {
        return strArea;
    }


    // Add getters
    public String getStrMeal() { return strMeal; }
    public String getStrMealThumb() { return strMealThumb; }



    public String getIngredients() {
        StringBuilder ingredients = new StringBuilder();

        if (strIngredient1 != null && !strIngredient1.isEmpty()) ingredients.append(strIngredient1).append(": ").append(strMeasure1 != null ? strMeasure1 : "").append("\n");
        if (strIngredient2 != null && !strIngredient2.isEmpty()) ingredients.append(strIngredient2).append(": ").append(strMeasure2 != null ? strMeasure2 : "").append("\n");
        if (strIngredient3 != null && !strIngredient3.isEmpty()) ingredients.append(strIngredient3).append(": ").append(strMeasure3 != null ? strMeasure3 : "").append("\n");
        if (strIngredient4 != null && !strIngredient4.isEmpty()) ingredients.append(strIngredient4).append(": ").append(strMeasure4 != null ? strMeasure4 : "").append("\n");
        if (strIngredient5 != null && !strIngredient5.isEmpty()) ingredients.append(strIngredient5).append(": ").append(strMeasure5 != null ? strMeasure5 : "").append("\n");
        if (strIngredient6 != null && !strIngredient6.isEmpty()) ingredients.append(strIngredient6).append(": ").append(strMeasure6 != null ? strMeasure6 : "").append("\n");
        if (strIngredient7 != null && !strIngredient7.isEmpty()) ingredients.append(strIngredient7).append(": ").append(strMeasure7 != null ? strMeasure7 : "").append("\n");
        if (strIngredient8 != null && !strIngredient8.isEmpty()) ingredients.append(strIngredient8).append(": ").append(strMeasure8 != null ? strMeasure8 : "").append("\n");
        if (strIngredient9 != null && !strIngredient9.isEmpty()) ingredients.append(strIngredient9).append(": ").append(strMeasure9 != null ? strMeasure9 : "").append("\n");
        if (strIngredient10 != null && !strIngredient10.isEmpty()) ingredients.append(strIngredient10).append(": ").append(strMeasure10 != null ? strMeasure10 : "").append("\n");
        if (strIngredient11 != null && !strIngredient11.isEmpty()) ingredients.append(strIngredient11).append(": ").append(strMeasure11 != null ? strMeasure11 : "").append("\n");
        if (strIngredient12 != null && !strIngredient12.isEmpty()) ingredients.append(strIngredient12).append(": ").append(strMeasure12 != null ? strMeasure12 : "").append("\n");
        if (strIngredient13 != null && !strIngredient13.isEmpty()) ingredients.append(strIngredient13).append(": ").append(strMeasure13 != null ? strMeasure13 : "").append("\n");
        if (strIngredient14 != null && !strIngredient14.isEmpty()) ingredients.append(strIngredient14).append(": ").append(strMeasure14 != null ? strMeasure14 : "").append("\n");
        if (strIngredient15 != null && !strIngredient15.isEmpty()) ingredients.append(strIngredient15).append(": ").append(strMeasure15 != null ? strMeasure15 : "").append("\n");
        if (strIngredient16 != null && !strIngredient16.isEmpty()) ingredients.append(strIngredient16).append(": ").append(strMeasure16 != null ? strMeasure16 : "").append("\n");
        if (strIngredient17 != null && !strIngredient17.isEmpty()) ingredients.append(strIngredient17).append(": ").append(strMeasure17 != null ? strMeasure17 : "").append("\n");
        if (strIngredient18 != null && !strIngredient18.isEmpty()) ingredients.append(strIngredient18).append(": ").append(strMeasure18 != null ? strMeasure18 : "").append("\n");
        if (strIngredient19 != null && !strIngredient19.isEmpty()) ingredients.append(strIngredient19).append(": ").append(strMeasure19 != null ? strMeasure19 : "").append("\n");
        if (strIngredient20 != null && !strIngredient20.isEmpty()) ingredients.append(strIngredient20).append(": ").append(strMeasure20 != null ? strMeasure20 : "").append("\n");

        return ingredients.toString();
    }
    // Add this method to your Meal.java
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
