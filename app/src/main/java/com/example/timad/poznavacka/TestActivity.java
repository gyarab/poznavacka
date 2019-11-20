package com.example.timad.poznavacka;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {

    Button enterPinButton;
    EditText pinInput;
    String PIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        init();

        enterPinButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                PIN = String.valueOf(pinInput.getText());

                /*

                proceed to testing

                 */
            }
        });


        //navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_practice:
                        Intent intent0 = new Intent(TestActivity.this, PracticeActivity.class);
                        startActivity(intent0);
                        break;

                    case R.id.nav_lists:
                        Intent intent1 = new Intent(TestActivity.this, ListsActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.nav_test:
                        /*Intent intent3 = new Intent(MainActivity.this, ActivityThree.class);
                        startActivity(intent3);*/
                        break;

                    case R.id.nav_share:
                        /*Intent intent2 = new Intent(MainActivity.this, ActivityTwo.class);
                        startActivity(intent2);*/
                        break;


                }


                return false;
            }
        });

    }

    private void init() {
        enterPinButton = findViewById(R.id.enter_pin_button);
        pinInput = findViewById(R.id.pin_input);
    }
}
