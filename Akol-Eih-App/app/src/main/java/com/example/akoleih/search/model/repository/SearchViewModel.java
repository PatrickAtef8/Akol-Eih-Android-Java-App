package com.example.akoleih.search.model.repository;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
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
    private final ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    public SearchViewModel(Application application) {
        super(application);
        SearchApiService apiService = RetrofitClient.getInstance().create(SearchApiService.class);
        SearchRemoteDataSource remote = new SearchRemoteDataSourceImpl(apiService);
        repository = new SearchRepositoryImpl(remote);
        connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check initial network state
        if (!NetworkUtil.isConnected(application)) {
            errorLive.postValue("NO_INTERNET");
            Log.e(TAG, "Initial network check: No internet connection");
        } else {
            Log.d(TAG, "Initial network check: Connected");
        }

        // Register network callback
        setupNetworkCallback();
    }

    private void setupNetworkCallback() {
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onLost(Network network) {
                if (!NetworkUtil.isConnected(getApplication())) {
                    errorLive.postValue("NO_INTERNET");
                    Log.e(TAG, "Network lost");
                }
            }

            @Override
            public void onAvailable(Network network) {
                Log.d(TAG, "Network available");
            }

            @Override
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                boolean hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                Log.d(TAG, "Network capabilities changed, hasInternet: " + hasInternet);
                if (!hasInternet) {
                    errorLive.postValue("NO_INTERNET");
                }
            }
        };

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        try {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
            Log.d(TAG, "Network callback registered");
        } catch (Exception e) {
            Log.e(TAG, "Failed to register network callback: " + e.getMessage());
        }
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
            Log.d(TAG, "Search skipped: Empty query");
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
                    errorMessage = "NO_INTERNET";
                }
                errorLive.postValue(errorMessage);
                Log.e(TAG, "Search failed: " + errorMessage);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
                Log.d(TAG, "Network callback unregistered");
            } catch (Exception e) {
                Log.e(TAG, "Failed to unregister network callback: " + e.getMessage());
            }
            networkCallback = null;
        }
    }
}