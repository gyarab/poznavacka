package com.example.timad.poznavacka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.activities.lists.MyListsActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;


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
        setContentView(R.layout.activity_welcome);

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
        MyListsActivity.initialized = true;
        MyListsActivity.init(getApplication());
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {

            Intent intent0 = new Intent(WelcomeActivity.this, AuthenticationActivity.class);
            startActivity(intent0);
            overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
            finish();

        } else {
            MobileAds.initialize(getApplication(), new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    initializeMyLists();
                    //loadNativeAd();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent0 = new Intent(WelcomeActivity.this, MyListsActivity.class);
                            startActivity(intent0);
                            overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                            finish();
                        }
                    }, SPLASH_TIME_OUT);
                }
            });
        }
    }
}

