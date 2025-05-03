
// CategoryRemoteDataSource.java
package com.example.akoleih.home.network.api;

import com.example.akoleih.home.network.model.CategoriesResponse;

public interface CategoryRemoteDataSource {
    void fetchCategories(DataSourceCallback<CategoriesResponse> callback);
}
