package com.example.timad.poznavacka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.activities.lists.ListsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;


public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        int SPLASH_TIME_OUT = 700;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUI(user);
            }
        }, SPLASH_TIME_OUT);

    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            Intent intent0 = new Intent(WelcomeActivity.this, AuthenticationActivity.class);
            startActivity(intent0);
            finish();
        } else {
            Intent intent0 = new Intent(WelcomeActivity.this, ListsActivity.class);
            startActivity(intent0);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

    }
}
