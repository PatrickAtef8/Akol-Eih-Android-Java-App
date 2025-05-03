// HomePresenter.java
package com.example.akoleih.home.presenter;

public interface HomePresenter {
    void getCategories();
    void getRandomMeal();
    void getMealsByCategory(String category);
    void getMealDetails(String mealId);
    void onDestroy();
}