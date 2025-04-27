package com.example.akoleih.home.contract;

import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;
import java.util.List;

public interface HomeContract {
    interface View {
        void showCategories(List<Category> categories);
        void showRandomMeal(Meal meal);
        void showError(String message);
    }

    interface Presenter {
        void getCategories();
        void getRandomMeal();
        void onDestroy();
    }
}