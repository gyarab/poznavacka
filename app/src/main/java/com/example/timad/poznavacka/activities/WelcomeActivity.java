package com.example.timad.poznavacka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.activities.lists.MyListsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;


public class WelcomeActivity extends AppCompatActivity {
    int SPLASH_TIME_OUT = 300;

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        updateUI(user);

    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent0 = new Intent(WelcomeActivity.this, AuthenticationActivity.class);
                    startActivity(intent0);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent0 = new Intent(WelcomeActivity.this, MyListsActivity.class);
                    startActivity(intent0);
                    finish();
                    overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                }
            }, SPLASH_TIME_OUT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

    }
}
