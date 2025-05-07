// HomeView.java
package com.example.akoleih.home.view.homeview;

import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;

import java.util.List;

public interface HomeView {
    void showCategories(List<Category> categories);
    void showRandomMeal(Meal meal);
    void showMealsByCategory(List<Meal> meals);
    void showMealDetails(Meal meal);
    void showError(String message);
}
