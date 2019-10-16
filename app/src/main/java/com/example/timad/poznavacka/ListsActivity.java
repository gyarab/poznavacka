package com.example.timad.poznavacka;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class ListsActivity extends AppCompatActivity {

    Button new_list_button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        init();
    }


    private void init() {
        new_list_button = findViewById(R.id.buttonNewList);
    }


}
