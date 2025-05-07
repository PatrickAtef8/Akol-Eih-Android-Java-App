package com.example.akoleih.favorite.view.fragment;

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
import com.example.akoleih.favorite.view.adapter.FavoriteAdapter;
import com.example.akoleih.favorite.model.FavoriteMeal;
import com.example.akoleih.favorite.model.repository.SyncFavoriteRepositoryImpl;
import com.example.akoleih.favorite.presenter.FavoritePresenter;
import com.example.akoleih.favorite.presenter.FavoritePresenterImpl;
import com.example.akoleih.favorite.presenter.FavoriteView;
import com.example.akoleih.favorite.view.listener.OnMealClickListener;
import com.example.akoleih.home.view.fragments.thirdfragment.HomeMealDetailsThirdFragment;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;

public class FavoritesFragment extends Fragment implements FavoriteView, OnMealClickListener {
    private FavoritePresenter presenter;
    private FavoriteAdapter adapter;
    private View emptyStateView;
    private RecyclerView rvFavorites;
    private Snackbar mSnackbar;
    private FavoriteMeal removedMeal;
    private int lastRemovedPosition;

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
        adapter = new FavoriteAdapter(this::onItemRemoved);
        adapter.setMealClickListener(this);
        rvFavorites.setAdapter(adapter);

        presenter = new FavoritePresenterImpl(new SyncFavoriteRepositoryImpl(requireContext()));
        presenter.attachView(this, getViewLifecycleOwner());
    }
    private void addSnackbarAnimations(Snackbar snackbar) {
        @SuppressLint("RestrictedApi")
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        if (getContext() == null || !isAdded()) return;

        Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_right);
        layout.startAnimation(slideIn);

        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (getContext() != null && isAdded()) {
                    Animation slideOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left);
                    layout.startAnimation(slideOut);
                }
            }
        });
    }
    @SuppressLint("RestrictedApi")
    private void configureSnackbarView(Snackbar snackbar) {
        if (getContext() == null || !isAdded()) return;

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.removeAllViews();
        layout.setBackgroundColor(Color.TRANSPARENT);
        layout.setPadding(0, 0, 0, 0);

        View customView = LayoutInflater.from(getContext()).inflate(R.layout.custom_snackbar, null);

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
    private void showCustomSnackbar() {
        if (getView() == null || !isAdded()) return;

        mSnackbar = Snackbar.make(getView(), "", Snackbar.LENGTH_LONG);
        configureSnackbarView(mSnackbar);
        setupSnackbarContent(mSnackbar);
        addSnackbarAnimations(mSnackbar);
        mSnackbar.show();
    }
    private void setupSnackbarContent(Snackbar snackbar) {
        if (getContext() == null || !isAdded()) return;

        @SuppressLint("RestrictedApi") View customView = ((Snackbar.SnackbarLayout) snackbar.getView()).getChildAt(0);
        ImageView icon = customView.findViewById(R.id.icon);
        TextView text = customView.findViewById(R.id.text);
        Button action = customView.findViewById(R.id.action);

        icon.setImageResource(R.drawable.ic_delete_plan);
        text.setText(getString(R.string.favorite_deleted));
        action.setText(getString(R.string.undo));

        action.setOnClickListener(v -> {
            undoDelete();
            snackbar.dismiss();
        });
    }

    private void onItemRemoved(FavoriteMeal meal, int position) {
        removedMeal = meal;
        lastRemovedPosition = position;
        presenter.deleteFavorite(meal);
        adapter.removeItem(position);
        updateEmptyState();
        showCustomSnackbar();
    }

    // ... (keep all your existing snackbar methods unchanged)

    private void undoDelete() {
        if (removedMeal != null && isAdded()) {
            presenter.addFavorite(removedMeal);
            adapter.restoreItem(removedMeal, lastRemovedPosition);
            rvFavorites.scrollToPosition(lastRemovedPosition);
            updateEmptyState();
        }
    }

    private void updateEmptyState() {
        if (emptyStateView != null && rvFavorites != null) {
            emptyStateView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            rvFavorites.setVisibility(adapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void showFavorites(List<FavoriteMeal> favorites) {
        if (adapter != null) {
            adapter.setData(favorites);
            updateEmptyState();
        }
    }

    @Override
    public void showEmpty() {
        if (adapter != null) {
            adapter.setData(null);
            updateEmptyState();
        }
    }

    @Override
    public void showError(String message) {
        if (getView() != null && isAdded()) {
            Snackbar errorSnackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_LONG);
            errorSnackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.error));
            errorSnackbar.show();
        }
    }

    @Override
    public void onMealClick(FavoriteMeal meal) {
        if (isAdded()) {
            HomeMealDetailsThirdFragment fragment = HomeMealDetailsThirdFragment.newInstance(meal.getIdMeal());
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        if (mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
            mSnackbar = null;
        }
        if (presenter != null) {
            presenter.detachView();
        }
        if (rvFavorites != null) {
            rvFavorites.setAdapter(null);
        }
        super.onDestroyView();
    }
}