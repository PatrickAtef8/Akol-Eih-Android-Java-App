package com.example.akoleih.favorite.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.akoleih.R;
import com.example.akoleih.favorite.adapter.FavoriteAdapter;
import com.example.akoleih.favorite.db.AppDatabase;
import com.example.akoleih.favorite.model.FavoriteMeal;
import com.example.akoleih.favorite.presenter.FavoritePresenterImpl;
import com.example.akoleih.favorite.presenter.FavoriteView;
import com.example.akoleih.favorite.repository.FavoriteRepositoryImpl;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class FavoritesFragment extends Fragment implements FavoriteView {
    private FavoritePresenterImpl presenter;
    private FavoriteAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup c, Bundle b) {
        return inf.inflate(R.layout.fragment_favorites, c, false);
    }

    @Override
    public void onViewCreated(View v, @Nullable Bundle s) {
        RecyclerView rv = v.findViewById(R.id.rvFavorites);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FavoriteAdapter(meal -> presenter.deleteFavorite(meal));
        rv.setAdapter(adapter);

        presenter = new FavoritePresenterImpl(
                new FavoriteRepositoryImpl(AppDatabase.getInstance(requireContext()))
        );
        presenter.attachView(this);
        presenter.loadFavorites();
    }

    @Override public void showFavorites(List<FavoriteMeal> favorites) {
        adapter.setData(favorites);
    }

    @Override public void showEmpty() {
        adapter.setData(null);
        Snackbar.make(requireView(), "No favorites yet", Snackbar.LENGTH_SHORT).show();
    }

    @Override public void showError(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }
}
