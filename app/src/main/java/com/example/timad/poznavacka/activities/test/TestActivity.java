package com.example.timad.poznavacka.activities.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.timad.poznavacka.LockableViewPager;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.SectionsPageAdapter;
import com.example.timad.poznavacka.activities.AccountActivity;
import com.example.timad.poznavacka.activities.practice.PracticeActivity;
import com.example.timad.poznavacka.activities.lists.MyListsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {

    public static String TAG = "TestActivity";

    protected SectionsPageAdapter mSectionsPageAdapter;
    public static LockableViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //fragments navigation
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);


        //navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        //BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
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
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        break;

                    case R.id.nav_lists:
                        Intent intent1 = new Intent(TestActivity.this, MyListsActivity.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        break;

                    /*case R.id.nav_test:
                        /*Intent intent3 = new Intent(MainActivity.this, ActivityThree.class);
                        startActivity(intent3);*
                        break;*/

                    case R.id.nav_account:
                        Intent intent4 = new Intent(TestActivity.this, AccountActivity.class);
                        startActivity(intent4);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;


                }


                return false;
            }
        });

    }


    private void setupViewPager(LockableViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new TestPINFragment());
        adapter.addFragment(new TestWaitFragment());
        viewPager.setAdapter(adapter);
        viewPager.setSwipeable(false);
    }

    public void setViewPager(int fragmentNumber) {
        mViewPager.setCurrentItem(fragmentNumber);
    }

}

