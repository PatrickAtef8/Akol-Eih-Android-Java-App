package com.example.akoleih.search.model.network.api;

public interface DataSourceCallback<T> {
    void onSuccess(T result);
    void onError(String message);
}