package com.example.timad.poznavacka;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class ListsActivity extends AppCompatActivity {

    Button new_list_button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        init();


        //navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_practice:
                        Intent intent0 = new Intent(ListsActivity.this, PracticeActivity.class);
                        startActivity(intent0);
                        break;

                    case R.id.nav_lists:
                        Intent intent1 = new Intent(ListsActivity.this, ListsActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.nav_share:
                        /*Intent intent2 = new Intent(MainActivity.this, ActivityTwo.class);
                        startActivity(intent2);*/
                        break;

                    case R.id.nav_contact:
                        /*Intent intent3 = new Intent(MainActivity.this, ActivityThree.class);
                        startActivity(intent3);*/
                        break;

                }


                return false;
            }
        });
    }


    private void init() {
        new_list_button = findViewById(R.id.buttonNewList);
    }


}
