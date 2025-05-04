package com.example.akoleih.profile.model;

import android.net.Uri;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileRepositoryImpl implements ProfileRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_pictures");

    @Override
    public UserProfile getCurrentUserProfile() {
        FirebaseUser fb = auth.getCurrentUser();
        if (fb == null) return null;
        UserProfile up = new UserProfile();
        up.setId(fb.getUid());
        up.setName(fb.getDisplayName());
        up.setEmail(fb.getEmail());
        up.setPhotoUrl(fb.getPhotoUrl() != null ? fb.getPhotoUrl().toString() : null);
        return up;
    }

    @Override
    public void signOut() {
        auth.signOut();
    }

    @Override
    public void updateProfilePicture(Uri imageUri, OnCompleteListener<Void> listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;
        StorageReference picRef = storageRef.child(user.getUid() + ".jpg");
        picRef.putFile(imageUri)
                .continueWithTask(task -> picRef.getDownloadUrl())
                .addOnSuccessListener(uri -> {
                    UserProfileChangeRequest req = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri)
                            .build();
                    user.updateProfile(req).addOnCompleteListener(listener);
                });
    }

    @Override
    public void updateDisplayName(String displayName, OnCompleteListener<Void> listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;
        UserProfileChangeRequest req = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();
        user.updateProfile(req).addOnCompleteListener(listener);
    }
}