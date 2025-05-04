package com.example.akoleih.profile.presenter;

import android.net.Uri;
import com.google.android.gms.tasks.OnCompleteListener;
import com.example.akoleih.profile.view.ProfileView;

public interface ProfilePresenter {
    void attachView(ProfileView view);
    void detachView();
    void loadUser();
    void signOut();
    void changeProfilePicture(Uri imageUri);
    void changeDisplayName(String newName);
}