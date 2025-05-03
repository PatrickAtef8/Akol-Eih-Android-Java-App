package com.example.akoleih.search.network.api;

public interface DataSourceCallback<T> {
    void onSuccess(T result);
    void onError(String message);
}