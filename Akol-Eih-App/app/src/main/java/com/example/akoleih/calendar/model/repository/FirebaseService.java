package com.example.akoleih.calendar.model.repository;


import com.example.akoleih.calendar.model.db.CalendarMeal;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public interface FirebaseService {
    String getCurrentUserId();
    Task<Void> saveMeal(String uid, String documentId, CalendarMeal meal);
    Task<Void> deleteMeal(String uid, String documentId);
    Task<QuerySnapshot> getCalendarMeals(String uid);
}