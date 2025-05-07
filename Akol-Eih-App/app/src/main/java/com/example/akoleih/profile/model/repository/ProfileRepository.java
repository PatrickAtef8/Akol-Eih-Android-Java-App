package com.example.akoleih.profile.model.repository;

import android.net.Uri;

import com.example.akoleih.profile.model.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;

public interface ProfileRepository {
    UserProfile getCurrentUserProfile();
    void signOut();
    void updateProfilePicture(Uri imageUri, OnCompleteListener<Void> listener);
    void updateDisplayName(String displayName, OnCompleteListener<Void> listener);
}