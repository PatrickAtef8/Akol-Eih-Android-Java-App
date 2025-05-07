package com.example.akoleih.search.model.network.api;

import com.example.akoleih.search.model.repository.SearchType;
import com.example.akoleih.search.model.network.model.SearchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchRemoteDataSourceImpl implements SearchRemoteDataSource {
    private SearchApiService apiService;

    public SearchRemoteDataSourceImpl(SearchApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void executeNetworkCall(Call<SearchResponse> call, DataSourceCallback<SearchResponse> callback) {
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body() != null ?
                            response.body() :
                            SearchResponse.empty());
                } else {
                    callback.onError("Server error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                callback.onError(t.getMessage() != null ?
                        t.getMessage() :
                        "Network request failed");
            }
        });
    }

    public Call<SearchResponse> createSearchCall(String query, SearchType type) {
        String processedQuery = query.trim().toLowerCase();
        switch (type) {
            case CATEGORY: return apiService.searchByCategory(processedQuery);
            case AREA: return apiService.searchByArea(processedQuery);
            case INGREDIENT: return apiService.searchByIngredient(processedQuery);
            default: return apiService.searchByName(processedQuery);
        }
    }
}