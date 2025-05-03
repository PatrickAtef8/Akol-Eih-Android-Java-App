package com.example.akoleih.search.presenter;

import com.example.akoleih.search.model.repository.SearchType;

interface SearchPresenter {
    void search(String query, SearchType type);
    void onDestroy();
}