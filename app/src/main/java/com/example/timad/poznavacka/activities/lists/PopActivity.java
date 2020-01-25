package com.example.timad.poznavacka.activities.lists;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.example.timad.poznavacka.R;

import java.util.ArrayList;
import java.util.Collections;

public class PopActivity extends Activity {

    private final String TAG = "PopActivity";

    Button DONEButton;

    static ArrayList<String> userScientificClassification;
    static ArrayList<String> reversedUserScientificClassification;

    static int userParametersCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        DONEButton = findViewById(R.id.PopDONEButton);
        userScientificClassification = new ArrayList<>();

        userParametersCount = 3; //REPLACE with number of params selected by user

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.8));

        //DONEButton
        DONEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //getting the scientific classification selected by the user
        //needs to be added from top to bottom
        userScientificClassification.add("Kmen");  //REPLACE with classific selected by user
        userScientificClassification.add("Čeleď");

        reversedUserScientificClassification = userScientificClassification;
        Collections.reverse(reversedUserScientificClassification);

    }


}
