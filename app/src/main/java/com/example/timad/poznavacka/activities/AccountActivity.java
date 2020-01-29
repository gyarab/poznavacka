package com.example.timad.poznavacka.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.timad.poznavacka.BottomNavigationViewHelper;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.activities.lists.ListsActivity;
import com.example.timad.poznavacka.activities.test.TestActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";

    private ViewPager mViewPager;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // asks for perrmission to read and write to external storage
        writToExternalStorage();

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
    public void writToExternalStorage(){
        if (ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    AccountActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                builder.setTitle("request Perrmission");
                builder.setMessage("you should enable this perrmission so we can write in to your external storage");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(AccountActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_STORAGE);
                    }
                });
                builder.setPositiveButton("grant permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(AccountActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_STORAGE);
                    }
                });
                builder.show();
            } else {
                ActivityCompat.requestPermissions(AccountActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResutlts) {
        if(requestCode==MY_PERMISSIONS_REQUEST_READ_STORAGE){
                if (grantResutlts.length > 0
                        && grantResutlts[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "not granted perrmission to read and write to external storage", Toast.LENGTH_LONG).show();
                }
            }
        }
}
