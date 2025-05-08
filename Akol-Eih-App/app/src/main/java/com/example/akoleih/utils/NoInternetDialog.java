package com.example.akoleih.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.airbnb.lottie.LottieAnimationView;
import com.example.akoleih.R;

public class NoInternetDialog {
    private static final String TAG = "NoInternetDialog";
    private static Dialog currentDialog = null;

    public static void show(@NonNull Context context, Runnable onRetry) {
        Log.d(TAG, "Attempting to show dialog");

        // Dismiss any existing dialog
        dismiss();

        try {
            Dialog dialog = new Dialog(context);
            currentDialog = dialog;
            View view = LayoutInflater.from(context).inflate(R.layout.no_internet_dialog, null);
            dialog.setContentView(view);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);

            LottieAnimationView lottieAnimation = view.findViewById(R.id.no_internet_animation);
            TextView message = view.findViewById(R.id.dialog_message);
            Button retryButton = view.findViewById(R.id.dialog_retry_button);
            Button closeButton = view.findViewById(R.id.dialog_close_button);

            // Configure Lottie animation
            try {
                lottieAnimation.setAnimation(R.raw.lottie);
                lottieAnimation.setScaleX(1.0f);
                lottieAnimation.setScaleY(1.0f);
                lottieAnimation.playAnimation();
                Log.d(TAG, "Lottie animation loaded and playing");
            } catch (Exception e) {
                Log.e(TAG, "Failed to load Lottie animation: " + e.getMessage());
                lottieAnimation.setVisibility(View.GONE);
                message.setText("No internet connection. Please check your network.");
            }

            retryButton.setOnClickListener(v -> {
                Log.d(TAG, "Retry button clicked");
                if (onRetry != null) {
                    onRetry.run();
                }
                dialog.dismiss();
                currentDialog = null;
            });

            closeButton.setOnClickListener(v -> {
                Log.d(TAG, "Close button clicked");
                dialog.dismiss();
                currentDialog = null;
            });

            dialog.setOnDismissListener(d -> {
                Log.d(TAG, "Dialog dismissed");
                currentDialog = null;
            });

            dialog.show();
            Log.d(TAG, "Dialog shown successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to show dialog: " + e.getMessage());
            currentDialog = null;
            try {
                android.widget.Toast.makeText(context, "No internet connection", android.widget.Toast.LENGTH_SHORT).show();
            } catch (Exception toastException) {
                Log.e(TAG, "Failed to show fallback Toast: " + toastException.getMessage());
            }
        }
    }

    public static void dismiss() {
        if (currentDialog != null && currentDialog.isShowing()) {
            try {
                currentDialog.dismiss();
                Log.d(TAG, "Existing dialog dismissed");
            } catch (Exception e) {
                Log.e(TAG, "Failed to dismiss dialog: " + e.getMessage());
            }
            currentDialog = null;
        }
    }
}