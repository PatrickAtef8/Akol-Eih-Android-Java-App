package com.example.akoleih.search.view.searchview;

import com.example.akoleih.search.model.SearchMeal;

import java.util.List;

public interface SearchView {
    void showResults(List<SearchMeal> meals);
    void showError(String message);
    void showLoading();
    void hideLoading();
}