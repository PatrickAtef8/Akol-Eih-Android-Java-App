package com.example.akoleih.utils;

public class SearchValidator {
    private static final String[] VALID_AREAS = {
            "American", "British", "Canadian", "Chinese", "Croatian",
            "Dutch", "Egyptian", "French", "Greek", "Indian",
            "Irish", "Italian", "Jamaican", "Japanese", "Kenyan",
            "Malaysian", "Mexican", "Moroccan", "Polish", "Portuguese",
            "Russian", "Spanish", "Thai", "Tunisian", "Turkish", "Vietnamese"
    };

    private static final String[] VALID_CATEGORIES = {
            "Beef", "Breakfast", "Chicken", "Dessert", "Goat",
            "Lamb", "Miscellaneous", "Pasta", "Pork", "Seafood",
            "Side", "Starter", "Vegan", "Vegetarian"
    };

    public static boolean isValidArea(String area) {
        if (area == null) return false;
        for (String valid : VALID_AREAS) {
            if (valid.equalsIgnoreCase(area.trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidCategory(String category) {
        if (category == null) return false;
        for (String valid : VALID_CATEGORIES) {
            if (valid.equalsIgnoreCase(category.trim())) {
                return true;
            }
        }
        return false;
    }

    public static String[] getValidAreas() {
        return VALID_AREAS;
    }

    public static String[] getValidCategories() {
        return VALID_CATEGORIES;
    }
}