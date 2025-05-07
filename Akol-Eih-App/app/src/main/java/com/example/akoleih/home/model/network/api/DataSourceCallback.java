// DataSourceCallback.java
package com.example.akoleih.home.model.network.api;

public interface DataSourceCallback<T> {
    void onSuccess(T result);
    void onError(String message);
}