package com.example.akoleih.calendar.model.callbacks;


public interface DataCallback<T> {
    void onSuccess(T result);
    void onFailure(String error);
}