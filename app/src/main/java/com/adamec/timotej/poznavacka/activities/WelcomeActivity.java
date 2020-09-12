package com.adamec.timotej.poznavacka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adamec.timotej.poznavacka.R;
import com.adamec.timotej.poznavacka.activities.lists.MyListsActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

import timber.log.Timber;


public class WelcomeActivity extends AppCompatActivity {
    int SPLASH_TIME_OUT = 100;
    private ImageView welcomeLogo;
    private ProgressBar welcomeProgressBar;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.plant(new Timber.DebugTree());
        setContentView(R.layout.activity_welcome);
        Timber.d("WelcomeActivity: onCreate()");

        /*welcomeLogo = findViewById(R.id.welcome_logo);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcomeLogo.setTranslationY(-100);
            }
        }, 10);*/
        welcomeProgressBar = findViewById(R.id.welcome_progress_bar);
        welcomeProgressBar.setVisibility(View.VISIBLE);
        welcomeProgressBar.startAnimation(AnimationUtils.loadAnimation(getApplication(), android.R.anim.fade_in));

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        updateUI(user);
    }

    private void initializeMyLists() {
        Timber.d("WelcomeActivity: initializeMyLists()");
        MyListsActivity.initialized = true;
        MyListsActivity.init(getApplication());
    }

    private void updateUI(FirebaseUser user) {
        Timber.d("WelcomeActivity: updateUI()");
        if (user == null) {
            Timber.d("WelcomeActivity: updateUI(), user = null");
            Intent intent0 = new Intent(WelcomeActivity.this, AuthenticationActivity.class);
            startActivity(intent0);
            overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
            finish();

        } else {
            Timber.d("WelcomeActivity: updateUI(), user != null");
            MobileAds.initialize(getApplication());
            Intent intent0 = new Intent(WelcomeActivity.this, MyListsActivity.class);
            startActivity(intent0);
            overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
            finish();
            /*MobileAds.initialize(getApplication(), new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    initializeMyLists();
                    //loadNativeAd();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Timber.d("WelcomeActivity: updateUI(), user != null, postDelayed -> run()");
                            Intent intent0 = new Intent(WelcomeActivity.this, MyListsActivity.class);
                            startActivity(intent0);
                            overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                            finish();
                        }
                    }, 0); //Splash time
                }

            });*/
        }
    }
}

