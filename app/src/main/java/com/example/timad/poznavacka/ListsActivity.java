package com.example.timad.poznavacka;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ListsActivity extends AppCompatActivity {

    Button new_list_button;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        init();

        /* RecyclerView
         * Potřeba předělat na načítání názvů poznávaček ze souborů */
        ArrayList<Zastupce> zastupceArr = new ArrayList<>();
        zastupceArr.add(new Zastupce("Line 1", "Line 2"));
        zastupceArr.add(new Zastupce("Line 3", "Line 4"));

        mRecyclerView = findViewById(R.id.recyclerViewL);
        mRecyclerView.setHasFixedSize(true);
        mLManager = new LinearLayoutManager(this);
        mAdapter = new RWAdapter(zastupceArr);

        mRecyclerView.setLayoutManager(mLManager);
        mRecyclerView.setAdapter(mAdapter);

        /*

        DELETE LIST
        CREATE LIST
        switche = jeden pro automatické vybrání obrázku (buď z wikipedie nebo první z vyhledávání)
                  pokud bude tvůrce listu chtít třídu nebo řád:
                        druhý pro automatické vybrání třídy a řádu (z wikipedie, mám nástřel v pythonu)


        IMPORT LIST
        SELECT LIST

         */





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
                        /*Intent intent1 = new Intent(ListsActivity.this, ListsActivity.class);
                        startActivity(intent1);*/
                        break;

                    case R.id.nav_test:
                        Intent intent3 = new Intent(ListsActivity.this, TestActivity.class);
                        startActivity(intent3);
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
        new_list_button = findViewById(R.id.buttonNewList);
    }


}
