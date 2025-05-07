package com.example.akoleih.search.model.network.api;

import com.example.akoleih.search.model.repository.SearchType;
import com.example.akoleih.search.model.network.model.SearchResponse;
import retrofit2.Call;

public interface SearchRemoteDataSource {
    void executeNetworkCall(Call<SearchResponse> call, DataSourceCallback<SearchResponse> callback);
    Call<SearchResponse> createSearchCall(String query, SearchType type);
}