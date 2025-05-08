// FavoritesFragment.java
package com.example.akoleih.favorite.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.akoleih.R;
import com.example.akoleih.favorite.view.adapter.FavoriteAdapter;
import com.example.akoleih.favorite.model.FavoriteMeal;
import com.example.akoleih.favorite.model.repository.SyncFavoriteRepositoryImpl;
import com.example.akoleih.favorite.presenter.FavoritePresenter;
import com.example.akoleih.favorite.presenter.FavoritePresenterImpl;
import com.example.akoleih.favorite.presenter.FavoriteView;
import com.example.akoleih.favorite.view.listener.OnMealClickListener;
import com.example.akoleih.favorite.view.navigation.FavoriteNavigator;
import com.example.akoleih.favorite.view.utils.SnackbarHelper;
import com.example.akoleih.home.view.fragments.thirdfragment.HomeMealDetailsThirdFragment;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;

public class FavoritesFragment extends Fragment implements FavoriteView, OnMealClickListener, FavoriteNavigator {
    private FavoritePresenter presenter;
    private FavoriteAdapter adapter;
    private View emptyStateView;
    private RecyclerView rvFavorites;
    private Snackbar mSnackbar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFavorites = view.findViewById(R.id.rvFavorites);
        emptyStateView = view.findViewById(R.id.emptyStateView);

        rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new FavoriteAdapter((meal, position) -> {
            presenter.deleteFavorite(meal);
            adapter.removeItem(position);
            updateEmptyState();
        });
        adapter.setMealClickListener(this);
        rvFavorites.setAdapter(adapter);

        presenter = new FavoritePresenterImpl(
                new SyncFavoriteRepositoryImpl(requireContext()),
                this
        );
        presenter.attachView(this, getViewLifecycleOwner());
    }

    @Override
    public void showFavorites(List<FavoriteMeal> favorites) {
        if (adapter != null) {
            adapter.setData(favorites);
            updateEmptyState();
        }
    }

    @Override
    public void showEmptyState() {
        if (adapter != null) {
            adapter.setData(null);
            updateEmptyState();
        }
    }

    @Override
    public void showError(String message) {
        if (getView() != null && isAdded()) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void showUndoOption() {
        if (getView() == null || !isAdded()) return;

        mSnackbar = SnackbarHelper.createCustomSnackbar(
                requireContext(),
                getView(),
                getString(R.string.favorite_deleted),
                getString(R.string.undo),
                v -> {
                    presenter.undoLastDelete();
                    if (mSnackbar != null) {
                        mSnackbar.dismiss();
                    }
                }
        );
        mSnackbar.show();
    }

    @Override
    public void restoreFavorite(FavoriteMeal meal, int position) {
        if (adapter != null) {
            adapter.restoreItem(meal, position);
            rvFavorites.scrollToPosition(position);
            updateEmptyState();
        }
    }

    @Override
    public void navigateToMealDetails(String mealId) {
        if (isAdded()) {
            HomeMealDetailsThirdFragment fragment = HomeMealDetailsThirdFragment.newInstance(mealId);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void updateEmptyState() {
        if (emptyStateView != null && rvFavorites != null) {
            emptyStateView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            rvFavorites.setVisibility(adapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onMealClick(FavoriteMeal meal) {
        presenter.onMealClicked(meal);
    }

    @Override
    public void onDestroyView() {
        if (mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
        }
        if (presenter != null) {
            presenter.detachView();
        }
        super.onDestroyView();
    }
}