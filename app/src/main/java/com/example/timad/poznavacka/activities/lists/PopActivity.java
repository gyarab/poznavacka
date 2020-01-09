package com.example.timad.poznavacka.activities.lists;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.timad.poznavacka.R;

import java.util.ArrayList;

public class PopActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private final String TAG = "PopActivity";

    Spinner languageSpinner;
    Button DONEButton;

    static String languageURL;
    static ArrayList<String> userScientificClassification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        languageSpinner = findViewById(R.id.languageSpinner);
        DONEButton = findViewById(R.id.PopDONEButton);

        userScientificClassification = new ArrayList<>();
        languageURL = "cs";

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

        //spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.language_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
        languageSpinner.getBackground().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        languageSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //getting the scientific classification selected by the user
        userScientificClassification.add("Čeleď");
        userScientificClassification.add("Kmen");

        //getting the language of wiki
        Object selectedLanguageSpinnerItem = parent.getItemAtPosition(position);
        String languageString = selectedLanguageSpinnerItem.toString();

        switch (languageString) {
            case "English":
                languageURL = "en";
                break;
            case "Czech":
                languageURL = "cs";
                break;
            case "French":
                languageURL = "fr";
                break;
            case "German":
                languageURL = "de";
                break;
            case "Spanish":
                languageURL = "es";
                break;
            case "Japanese":
                languageURL = "ja";
                break;
            case "Russian":
                languageURL = "ru";
                break;
            case "Italian":
                languageURL = "it";
                break;
            case "Chinese":
                languageURL = "zh";
                break;
            case "Portuguese":
                languageURL = "pt";
                break;
            case "Arabic":
                languageURL = "ar";
                break;
            case "Persian":
                languageURL = "fa";
                break;
            case "Polish":
                languageURL = "pl";
                break;
            case "Dutch":
                languageURL = "nl";
                break;
            case "Indonesian":
                languageURL = "id";
                break;
            case "Ukrainian":
                languageURL = "uk";
                break;
            case "Hebrew":
                languageURL = "he";
                break;
            case "Swedish":
                languageURL = "sv";
                break;
            case "Korean":
                languageURL = "ko";
                break;
            case "Vietnamese":
                languageURL = "vi";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
