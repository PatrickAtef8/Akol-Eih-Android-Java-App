// FavoriteView.java
package com.example.akoleih.favorite.presenter;

import com.example.akoleih.favorite.model.FavoriteMeal;
import java.util.List;

public interface FavoriteView {
    void showFavorites(List<FavoriteMeal> favorites);
    void showEmptyState();
    void showError(String message);
    void showUndoOption();
    void restoreFavorite(FavoriteMeal meal, int position);
}