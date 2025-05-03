// DataSourceCallback.java
package com.example.akoleih.home.network.api;

public interface DataSourceCallback<T> {
    void onSuccess(T result);
    void onError(String message);
}