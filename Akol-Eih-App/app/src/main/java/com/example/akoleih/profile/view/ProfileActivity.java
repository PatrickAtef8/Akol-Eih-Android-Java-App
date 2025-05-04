package com.example.akoleih.profile.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.akoleih.R;
import com.example.akoleih.auth.view.LoginActivity;
import com.example.akoleih.profile.presenter.ProfilePresenter;
import com.example.akoleih.profile.presenter.ProfilePresenterImpl;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity implements ProfileView {
    private static final String PREFS_NAME = "profile_prefs";
    private static final String KEY_PROFILE_URI = "profile_uri";

    private ProfilePresenter presenter;
    private ShapeableImageView ivProfile;
    private TextView tvName, tvEmail;
    private MaterialButton btnEditName, btnLogout;
    private SharedPreferences prefs;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    // Display picked image
                    Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.ic_profile)
                            .into(ivProfile);
                    // Persist locally
                    prefs.edit().putString(KEY_PROFILE_URI, uri.toString()).apply();
                    // Notify presenter to upload/persist remotely if desired
                    presenter.changeProfilePicture(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefs      = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        ivProfile   = findViewById(R.id.iv_profile);
        tvName      = findViewById(R.id.tv_profile_name);
        tvEmail     = findViewById(R.id.tv_profile_email);
        btnEditName = findViewById(R.id.btn_edit_name);
        btnLogout   = findViewById(R.id.btn_logout);

        presenter = new ProfilePresenterImpl();
        presenter.attachView(this);
        presenter.loadUser();

        // Reload saved picture
        String saved = prefs.getString(KEY_PROFILE_URI, null);
        if (saved != null) {
            Glide.with(this)
                    .load(Uri.parse(saved))
                    .placeholder(R.drawable.ic_profile)
                    .into(ivProfile);
        }

        ivProfile.setOnClickListener(v -> showPictureOptions());
        btnEditName .setOnClickListener(v -> showNameEditDialog());
        btnLogout   .setOnClickListener(v -> new MaterialAlertDialogBuilder(this, R.style.Theme_MyApp_Dialog_Alert)
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to logout?")
                .setNegativeButton("Cancel", (d, i) -> d.dismiss())
                .setPositiveButton("Logout", (d, i) -> presenter.signOut())
                .show()
        );
    }

    private void showPictureOptions() {
        String saved = prefs.getString(KEY_PROFILE_URI, null);
        String[] options = saved == null
                ? new String[]{ "Add Photo" }
                : new String[]{ "Change Photo", "Remove Photo" };

        new MaterialAlertDialogBuilder(this, R.style.Theme_MyApp_Dialog_Alert)
                .setTitle("Profile Picture")
                .setItems(options, (dialog, which) -> {
                    if (saved == null || which == 0) {
                        // Add or Change
                        pickImageLauncher.launch("image/*");
                    } else {
                        // Remove
                        ivProfile.setImageResource(R.drawable.ic_profile);
                        prefs.edit().remove(KEY_PROFILE_URI).apply();
                        // (Optional) if you have a remote delete API:
                        // presenter.removeProfilePicture();
                    }
                })
                .show();
    }

    private void showNameEditDialog() {
        EditText input = new EditText(this);
        input.setText(tvName.getText());
        new MaterialAlertDialogBuilder(this, R.style.Theme_MyApp_Dialog_Alert)
                .setTitle("Edit Name")
                .setView(input)
                .setPositiveButton("Save", (d, w) ->
                        presenter.changeDisplayName(input.getText().toString()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    // === ProfileView callbacks ===

    @Override
    public void showUserName(String name) {
        tvName.setText(name != null ? name : "Unknown");
    }

    @Override
    public void showUserEmail(String email) {
        tvEmail.setText(email != null ? email : "—");
    }

    @Override
    public void showProfilePicture(String url) {
        if (url != null && !url.isEmpty()) {
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.ic_profile)
                    .into(ivProfile);
            prefs.edit().putString(KEY_PROFILE_URI, url).apply();
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(ivProfile, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String error) {
        Snackbar.make(ivProfile, error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
