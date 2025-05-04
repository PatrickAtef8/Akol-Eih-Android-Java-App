package com.example.akoleih.profile.model;

import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;

public interface ProfileRepository {
    UserProfile getCurrentUserProfile();
    void signOut();
    void updateProfilePicture(Uri imageUri, OnCompleteListener<Void> listener);
    void updateDisplayName(String displayName, OnCompleteListener<Void> listener);
}