package com.example.timad.poznavacka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.timad.poznavacka.BottomNavigationViewHelper;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.activities.lists.ListsActivity;
import com.example.timad.poznavacka.activities.test.TestActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);



        /* Navigation bar*/
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_practice:
                        Intent intent0 = new Intent(AccountActivity.this, PracticeActivity.class);
                        startActivity(intent0);
                        break;

                    case R.id.nav_lists:
                        Intent intent1 = new Intent(AccountActivity.this, ListsActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.nav_test:
                        Intent intent3 = new Intent(AccountActivity.this, TestActivity.class);
                        startActivity(intent3);
                        break;

                    case R.id.nav_account:
                        /*Intent intent4 = new Intent(ListsActivity.this, AccountActivity.class);
                        startActivity(intent4);
                        break;*/

                }


                return false;
            }
        });

    }

}

