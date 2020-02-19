package com.example.timad.poznavacka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.activities.lists.ListsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import androidx.appcompat.app.AppCompatActivity;


public class WelcomeActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onStart() {
        super.onStart();
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUI(account);
            }
        }, SPLASH_TIME_OUT);

    }

    private void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            Intent intent0 = new Intent(WelcomeActivity.this, ListsActivity.class);
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
