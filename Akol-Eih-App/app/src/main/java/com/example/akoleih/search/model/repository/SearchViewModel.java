package com.example.akoleih.search.model.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.akoleih.home.model.network.RetrofitClient;
import com.example.akoleih.search.model.SearchMeal;
import com.example.akoleih.search.model.network.api.DataSourceCallback;
import com.example.akoleih.search.model.network.api.SearchApiService;
import com.example.akoleih.search.model.network.api.SearchRemoteDataSource;
import com.example.akoleih.search.model.network.api.SearchRemoteDataSourceImpl;
import com.example.akoleih.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private static final String TAG = "SearchViewModel";
    private final SearchRepository repository;
    private final MutableLiveData<List<SearchMeal>> searchResultsLive = new MutableLiveData<>();
    private final MutableLiveData<String> errorLive = new MutableLiveData<>();

    public SearchViewModel(Application application) {
        super(application);
        SearchApiService apiService = RetrofitClient.getInstance().create(SearchApiService.class);
        SearchRemoteDataSource remote = new SearchRemoteDataSourceImpl(apiService);
        repository = new SearchRepositoryImpl(remote);
    }

    public LiveData<List<SearchMeal>> getSearchResultsLive() {
        return searchResultsLive;
    }

    public LiveData<String> getErrorLive() {
        return errorLive;
    }

    public void search(String query, SearchType type) {
        if (query == null || query.trim().isEmpty()) {
            searchResultsLive.postValue(new ArrayList<>());
            return;
        }

        if (!NetworkUtil.isConnected(getApplication())) {
            errorLive.postValue("NO_INTERNET");
            Log.e(TAG, "Search failed: No internet connection");
            return;
        }

        repository.searchMeals(query, type, new DataSourceCallback<List<SearchMeal>>() {
            @Override
            public void onSuccess(List<SearchMeal> meals) {
                searchResultsLive.postValue(meals != null ? meals : new ArrayList<>());
                Log.d(TAG, "Search successful, fetched " + (meals != null ? meals.size() : 0) + " meals");
            }

            @Override
            public void onError(String error) {
                String errorMessage = error;
                if (error != null && (error.contains("Unable to resolve host") ||
                        error.contains("network") ||
                        error.contains("timeout") ||
                        error.contains("SSL") ||
                        error.contains("connection"))) {
                    errorMessage = "NO_INTERNET";
                } else if (error == null) {
                    errorMessage = "NO_INTERNET"; // Handle null errors as network issues
                }
                errorLive.postValue(errorMessage);
                Log.e(TAG, "Search failed: " + errorMessage);
            }
        });
    }
}