package com.example.akoleih.search.presenter;

import com.example.akoleih.search.model.SearchMeal;
import com.example.akoleih.search.model.repository.SearchRepository;
import com.example.akoleih.search.model.repository.SearchType;
import com.example.akoleih.search.network.api.DataSourceCallback;
import com.example.akoleih.search.view.SearchView;

import java.util.Collections;
import java.util.List;

public class SearchPresenterImpl implements SearchPresenter {
    private SearchView searchView;
    private final SearchRepository repository;
    private boolean isSearchInProgress = false;

    public SearchPresenterImpl(SearchView searchView, SearchRepository repository) {
        this.searchView = searchView;
        this.repository = repository;
    }

    @Override
    public void search(String query, SearchType type) {
        if (isSearchInProgress) {
            return;
        }

        if (query == null || query.trim().isEmpty()) {
            searchView.showResults(Collections.emptyList());
            return;
        }

        isSearchInProgress = true;
        searchView.showLoading();

        repository.searchMeals(query.trim(), type, new DataSourceCallback<List<SearchMeal>>() {
            @Override
            public void onSuccess(List<SearchMeal> meals) {
                isSearchInProgress = false;
                searchView.hideLoading();
                searchView.showResults(meals != null ? meals : Collections.emptyList());
            }

            @Override
            public void onError(String message) {
                isSearchInProgress = false;
                searchView.hideLoading();
                searchView.showError(message);
                searchView.showResults(Collections.emptyList());
            }
        });
    }

    @Override
    public void onDestroy() {
        searchView = null;
    }
}