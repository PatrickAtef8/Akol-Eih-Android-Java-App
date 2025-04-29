// Meal.java
package com.example.akoleih.home.model;

public class Meal {
    private String idMeal;
    private String strMeal;
    private String strMealThumb;
    private String strInstructions;
    private String strArea;
    private String strIngredient1, strIngredient2, strIngredient3; // Add up to 20
    private String strMeasure1, strMeasure2, strMeasure3; // Add up to 20

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

    public String getIngredients() {
        // Combine all ingredients and measures
        StringBuilder ingredients = new StringBuilder();
        if (strIngredient1 != null && !strIngredient1.isEmpty()) {
            ingredients.append(strIngredient1).append(": ").append(strMeasure1).append("\n");
        }
        if (strIngredient2 != null && !strIngredient2.isEmpty()) {
            ingredients.append(strIngredient2).append(": ").append(strMeasure2).append("\n");
        }
        // Continue for all ingredients...
        return ingredients.toString();
    }
}