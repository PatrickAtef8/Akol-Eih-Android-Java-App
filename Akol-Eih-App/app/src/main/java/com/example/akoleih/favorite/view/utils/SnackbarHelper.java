// SnackbarHelper.java
package com.example.akoleih.favorite.view.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import com.example.akoleih.R;

public class SnackbarHelper {

    public static Snackbar createCustomSnackbar(
            @NonNull Context context,
            @NonNull View anchorView,
            String message,
            String actionText,
            View.OnClickListener actionListener
    ) {
        // Create basic snackbar
        Snackbar snackbar = Snackbar.make(anchorView, "", Snackbar.LENGTH_LONG);

        // Configure custom view
        configureSnackbarView(context, snackbar);

        // Setup content
        setupSnackbarContent(context, snackbar, message, actionText, actionListener);

        // Add animations
        addSnackbarAnimations(context, snackbar);

        return snackbar;
    }

    @SuppressLint("RestrictedApi")
    private static void configureSnackbarView(Context context, Snackbar snackbar) {
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.removeAllViews();
        layout.setBackgroundColor(Color.TRANSPARENT);
        layout.setPadding(0, 0, 0, 0);

        View customView = LayoutInflater.from(context).inflate(R.layout.custom_snackbar, null);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int margin = context.getResources().getDimensionPixelSize(R.dimen.snackbar_margin);
        int snackbarWidth = displayMetrics.widthPixels - (2 * margin);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                snackbarWidth,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.snackbar_bottom_margin);
        customView.setLayoutParams(params);

        layout.addView(customView);
    }

    @SuppressLint("RestrictedApi")
    private static void setupSnackbarContent(
            Context context,
            Snackbar snackbar,
            String message,
            String actionText,
            View.OnClickListener actionListener
    ) {
        View customView = ((Snackbar.SnackbarLayout) snackbar.getView()).getChildAt(0);
        ImageView icon = customView.findViewById(R.id.icon);
        TextView text = customView.findViewById(R.id.text);
        Button action = customView.findViewById(R.id.action);

        icon.setImageResource(R.drawable.ic_delete_plan);
        text.setText(message);
        action.setText(actionText);
        action.setOnClickListener(actionListener);
    }

    @SuppressLint("RestrictedApi")
    private static void addSnackbarAnimations(Context context, Snackbar snackbar) {
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        Animation slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_right);
        layout.startAnimation(slideIn);

        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                Animation slideOut = AnimationUtils.loadAnimation(context, R.anim.slide_left);
                layout.startAnimation(slideOut);
            }
        });
    }
}