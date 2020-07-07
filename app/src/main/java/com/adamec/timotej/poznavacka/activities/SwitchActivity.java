package com.adamec.timotej.poznavacka.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.adamec.timotej.poznavacka.R;
import com.adamec.timotej.poznavacka.activities.practice.PracticeActivity;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;


public class SwitchActivity extends AppCompatActivity {

    Switch switch1;
    Switch switch2;
    Switch switch3;
    Switch switch4;
    Switch switch5;
    Button go_btn;
    StorageReference storage;

    int SPLASH_TIME_OUT = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);
        init();
        final ArrayList<Integer> listToPass = new ArrayList<>();


        go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //getting user options applied
                        if (switch1.isChecked() || switch2.isChecked() || switch3.isChecked() || switch4.isChecked()) {
                            if (switch1.isChecked()) {
                                for (int i = 0; i <= 9; i++) {
                                    listToPass.add(i);
                                }
                            }

                            if (switch2.isChecked()) {
                                for (int i = 10; i <= 29; i++) {
                                    listToPass.add(i);
                                }
                            }

                            if (switch3.isChecked()) {
                                for (int i = 30; i <= 56; i++) {
                                    listToPass.add(i);
                                }
                            }

                            if (switch4.isChecked()) {
                                for (int i = 57; i <= 82; i++) {
                                    listToPass.add(i);
                                }
                            }
                            if (switch5.isChecked()) {
                                for (int i = 83; i <= 109; i++) {
                                    listToPass.add(i);
                                }
                            }

                            //PracticeActivity.sNenauceniZastupci = listToPass;
                            Intent homeIntent = new Intent(SwitchActivity.this, PracticeActivity.class);
                            startActivity(homeIntent);
                            finish();
                        } else {

                        }
                    }
                }, SPLASH_TIME_OUT);
            }
        });
    }


    public void playAudioWithDelay(final MediaPlayer MP, long DELAY) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MP.start();
            }
        }, DELAY);
    }

    private void init() {
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        switch4 = findViewById(R.id.switch4);
        switch5 = findViewById(R.id.switch5);
        go_btn = findViewById(R.id.go_btn);
    }

    private void setSwitchesTo(boolean checked) {
        switch1.setChecked(checked);
        switch2.setChecked(checked);
        switch3.setChecked(checked);
        switch4.setChecked(checked);
        switch5.setChecked(checked);
    }
}

