package com.example.vetclinicapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class persistence extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
