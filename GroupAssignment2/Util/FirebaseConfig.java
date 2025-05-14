package com.example.GroupAssignment2.Util;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConfig extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Firebase offline capabilities for better user experience
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}