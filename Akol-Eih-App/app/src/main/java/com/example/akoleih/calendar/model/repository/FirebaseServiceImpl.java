package com.example.akoleih.calendar.model.repository;


import com.example.akoleih.calendar.model.db.CalendarMeal;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseServiceImpl implements FirebaseService {
    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;

    public FirebaseServiceImpl() {
        this.firestore = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    @Override
    public String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }

    @Override
    public Task<Void> saveMeal(String uid, String documentId, CalendarMeal meal) {
        return firestore.collection("users").document(uid)
                .collection("calendar").document(documentId)
                .set(meal);
    }

    @Override
    public Task<Void> deleteMeal(String uid, String documentId) {
        return firestore.collection("users").document(uid)
                .collection("calendar").document(documentId)
                .delete();
    }

    @Override
    public Task<QuerySnapshot> getCalendarMeals(String uid) {
        return firestore.collection("users").document(uid)
                .collection("calendar").get();
    }
}