package com.example.timad.poznavacka;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;
import androidx.viewpager.widget.ViewPager;


public class ListsActivity extends AppCompatActivity {

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        init();

        /*

        DELETE LIST
        CREATE LIST
        switche = jeden pro automatické vybrání obrázku (buď z wikipedie nebo první z vyhledávání)
                  pokud bude tvůrce listu chtít třídu nebo řád:
                        druhý pro automatické vybrání třídy a řádu (z wikipedie, mám nástřel v pythonu)


        IMPORT LIST
        SELECT LIST

         */


        //fragments navigation
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_list_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_share_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_add_circle_black_24dp);






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

                    case R.id.nav_account:
                        Intent intent4 = new Intent(ListsActivity.this, AccountActivity.class);
                        startActivity(intent4);
                        break;

                }


                return false;
            }
        });
    }


    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyListsFragment());
        adapter.addFragment(new SharedListsFragment());
        adapter.addFragment(new CreateListFragment());
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber) {
        mViewPager.setCurrentItem(fragmentNumber);
    }


    private void init() {

    }


}
