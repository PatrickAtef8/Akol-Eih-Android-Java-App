package com.example.akoleih.search.model.repository;

import com.example.akoleih.search.network.api.DataSourceCallback;
import com.example.akoleih.search.network.api.SearchRemoteDataSource;
import com.example.akoleih.search.network.model.SearchResponse;
import com.example.akoleih.search.model.SearchMeal;
import retrofit2.Call;

import java.util.ArrayList;
import java.util.List;

public class SearchRepositoryImpl implements SearchRepository {
    private final SearchRemoteDataSource remoteDataSource;

    public SearchRepositoryImpl(SearchRemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
    }

    @Override
    public void searchMeals(String query, SearchType type, DataSourceCallback<List<SearchMeal>> callback) {
        String q = (query == null ? "" : query.trim());
        if (q.isEmpty()) {
            callback.onError("Search query cannot be empty");
            return;
        }
        if (type == null) {
            callback.onError("Search type must be provided");
            return;
        }

        Call<SearchResponse> call = remoteDataSource.createSearchCall(q.toLowerCase(), type);
        remoteDataSource.executeNetworkCall(call, new DataSourceCallback<SearchResponse>() {
            @Override
            public void onSuccess(SearchResponse response) {
                callback.onSuccess(convertToDomainModels(response));
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    private List<SearchMeal> convertToDomainModels(SearchResponse response) {
        List<SearchMeal> meals = new ArrayList<>();
        if (response != null && response.getMeals() != null) {
            for (SearchResponse.MealResponse meal : response.getMeals()) {
                meals.add(new SearchMeal(
                        meal.getIdMeal(),
                        meal.getStrMeal(),
                        meal.getStrMealThumb()
                ));
            }
        }
        return meals;
    }
}
