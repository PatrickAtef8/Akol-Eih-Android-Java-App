package com.example.akoleih.auth.model.callbacks;

public interface DataCallback<T> {
    void onSuccess(T data);
    void onFailure(String message);
}
