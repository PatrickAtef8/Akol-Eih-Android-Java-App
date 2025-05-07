package com.example.akoleih.utils;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import com.example.akoleih.R;
import com.example.akoleih.auth.view.LoginActivity;

public class CustomLoginDialog {

    public static void show(Context context) {
        // Inflate custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_custom_login, null);

        // Create AlertDialog with custom theme
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.CustomDialogTheme)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Initialize buttons
        dialogView.findViewById(R.id.dialog_login_button).setOnClickListener(v -> {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.dialog_cancel_button).setOnClickListener(v -> dialog.dismiss());

        // Show dialog
        dialog.show();
    }
}