package com.example.akoleih.favorite.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.akoleih.R;
import com.example.akoleih.favorite.adapter.FavoriteAdapter;
import com.example.akoleih.favorite.db.AppDatabase;
import com.example.akoleih.favorite.model.FavoriteMeal;
import com.example.akoleih.favorite.model.repository.FavoriteRepositoryImpl;
import com.example.akoleih.favorite.presenter.FavoritePresenterImpl;
import com.example.akoleih.favorite.presenter.FavoriteView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class FavoritesFragment extends Fragment implements FavoriteView {

    private FavoritePresenterImpl presenter;
    private FavoriteAdapter adapter;
    private View emptyStateView;
    private RecyclerView rvFavorites;
    private FavoriteMeal meal;
    private int lastRemovedPosition = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupRecyclerView();
        setupPresenter();
        loadInitialData();
    }

    private void initializeViews(View view) {
        rvFavorites = view.findViewById(R.id.rvFavorites);
        emptyStateView = view.findViewById(R.id.emptyStateView);
    }

    private void setupRecyclerView() {
        rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new FavoriteAdapter(this::showUndoSnackbar);
        rvFavorites.setAdapter(adapter);
    }

    private void setupPresenter() {
        presenter = new FavoritePresenterImpl(
                new FavoriteRepositoryImpl(AppDatabase.getInstance(requireContext()))
        );
        presenter.attachView(this);
    }

    private void loadInitialData() {
        presenter.loadFavorites();
    }

    private void showUndoSnackbar(FavoriteMeal meal, int position) {
        this.meal = meal;
        this.lastRemovedPosition = position;
        adapter.removeItem(position);
        showCustomSnackbar();
        checkEmptyState();
    }

    private void showCustomSnackbar() {
        Snackbar snackbar = Snackbar.make(requireView(), "", Snackbar.LENGTH_LONG);
        configureSnackbarView(snackbar);
        setupSnackbarContent(snackbar);
        addSnackbarAnimations(snackbar);
        snackbar.show();
    }

    @SuppressLint("RestrictedApi")
    private void configureSnackbarView(Snackbar snackbar) {
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.removeAllViews();
        layout.setBackgroundColor(Color.TRANSPARENT);
        layout.setPadding(0, 0, 0, 0);

        View customView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_snackbar, null);

        // Set full width with margins
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int margin = getResources().getDimensionPixelSize(R.dimen.snackbar_margin);
        int snackbarWidth = displayMetrics.widthPixels - (2 * margin);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                snackbarWidth,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.snackbar_bottom_margin);
        customView.setLayoutParams(params);

        layout.addView(customView);
    }

    private void setupSnackbarContent(Snackbar snackbar) {
        @SuppressLint("RestrictedApi") View customView = ((Snackbar.SnackbarLayout) snackbar.getView()).getChildAt(0);
        ImageView icon = customView.findViewById(R.id.icon);
        TextView text = customView.findViewById(R.id.text);
        Button action = customView.findViewById(R.id.action);

        icon.setImageResource(R.drawable.ic_delete);
        text.setText(getString(R.string.favorite_deleted));
        action.setText(getString(R.string.undo));

        action.setOnClickListener(v -> {
            if (lastRemovedPosition != -1) {
                adapter.restoreItem(meal, lastRemovedPosition);
                rvFavorites.smoothScrollToPosition(lastRemovedPosition);
                checkEmptyState();
            }
            snackbar.dismiss();
        });
    }

    private void addSnackbarAnimations(Snackbar snackbar) {
        @SuppressLint("RestrictedApi")
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        Animation slideIn = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_right);
        layout.startAnimation(slideIn);

        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                Animation slideOut = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_left);
                layout.startAnimation(slideOut);

                // only delete if the Snackbar timed out or was swiped away
                if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_SWIPE) {
                    presenter.deleteFavorite(meal);
                    checkEmptyState();
                }
            }
        });
    }

    private void checkEmptyState() {
        if (adapter.getItemCount() == 0) {
            showEmptyState();
        } else {
            showFavoritesList();
        }
    }

    @Override
    public void showFavorites(List<FavoriteMeal> favorites) {
        if (favorites == null || favorites.isEmpty()) {
            showEmptyState();
        } else {
            showFavoritesList(favorites);
        }
    }

    private void showFavoritesList() {
        rvFavorites.setVisibility(View.VISIBLE);
        emptyStateView.setVisibility(View.GONE);
    }

    private void showFavoritesList(List<FavoriteMeal> favorites) {
        adapter.setData(favorites);
        showFavoritesList();
    }

    @Override
    public void showEmpty() {
        showEmptyState();
    }

    private void showEmptyState() {
        rvFavorites.setVisibility(View.GONE);
        emptyStateView.setVisibility(View.VISIBLE);
        adapter.setData(null);
    }

    @Override
    public void showError(String message) {
        Snackbar errorSnackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG);
        errorSnackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.error));
        errorSnackbar.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
        rvFavorites.setAdapter(null);
    }
}
