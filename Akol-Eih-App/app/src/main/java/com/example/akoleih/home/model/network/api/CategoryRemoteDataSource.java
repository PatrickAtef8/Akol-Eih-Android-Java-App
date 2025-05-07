
// CategoryRemoteDataSource.java
package com.example.akoleih.home.model.network.api;

import com.example.akoleih.home.model.network.model.CategoriesResponse;

public interface CategoryRemoteDataSource {
    void fetchCategories(DataSourceCallback<CategoriesResponse> callback);
}
