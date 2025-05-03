package com.example.akoleih.search.model.repository;

import com.example.akoleih.search.network.api.DataSourceCallback;
import com.example.akoleih.search.model.SearchMeal;
import java.util.List;

public interface SearchRepository {
    void searchMeals(String query, SearchType type, DataSourceCallback<List<SearchMeal>> callback);
}