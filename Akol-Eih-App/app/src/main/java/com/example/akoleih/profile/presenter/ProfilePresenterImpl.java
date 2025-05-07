package com.example.akoleih.profile.presenter;

import android.net.Uri;

import com.example.akoleih.profile.model.repository.ProfileRepository;
import com.example.akoleih.profile.model.repository.ProfileRepositoryImpl;
import com.example.akoleih.profile.model.UserProfile;
import com.example.akoleih.profile.view.ProfileView;

public class ProfilePresenterImpl implements ProfilePresenter {
    private ProfileView view;
    private final ProfileRepository repo;

    public ProfilePresenterImpl() {
        this.repo = new ProfileRepositoryImpl();
    }

    @Override
    public void attachView(ProfileView view) { this.view = view; }

    @Override
    public void detachView() { this.view = null; }

    @Override
    public void loadUser() {
        UserProfile u = repo.getCurrentUserProfile();
        if (view != null && u != null) {
            view.showUserName(u.getName());
            view.showUserEmail(u.getEmail());
            view.showProfilePicture(u.getPhotoUrl());
        }
    }

    @Override
    public void signOut() {
        repo.signOut();
        if (view != null) view.navigateToLogin();
    }

    @Override
    public void changeProfilePicture(Uri imageUri) {
        repo.updateProfilePicture(imageUri, task -> {
            if (view != null) {
                if (task.isSuccessful()) view.showMessage("Profile picture updated");
                else view.showError("Failed to update picture");
                loadUser();
            }
        });
    }

    @Override
    public void changeDisplayName(String newName) {
        repo.updateDisplayName(newName, task -> {
            if (view != null) {
                if (task.isSuccessful()) view.showMessage("Name updated");
                else view.showError("Failed to update name");
                loadUser();
            }
        });
    }
}