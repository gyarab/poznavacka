package com.adamec.timotej.poznavacka.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adamec.timotej.poznavacka.R;
import com.adamec.timotej.poznavacka.activities.lists.MyListsActivity;
import com.adamec.timotej.poznavacka.activities.practice.PracticeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";
    TextView signedInAs;
    Button signOutButton;
    Button changeLanButton;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_account);
        signedInAs = findViewById(R.id.textview_signed_in_as);
        signOutButton = findViewById(R.id.button_sign_out);
        changeLanButton = findViewById(R.id.changeLanButt);
        final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplication());


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView_Bar);
        //BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        if (acct != null) {
            signedInAs.setText(getString(R.string.signed_in_as) + " " + user.getDisplayName());
        } else {
            signedInAs.setText(R.string.user_not_signed_in);
        }

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });



        /* Navigation bar*/
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_practice:
                        Intent intent0 = new Intent(AccountActivity.this, PracticeActivity.class);
                        startActivity(intent0);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        break;

                    case R.id.nav_lists:
                        Intent intent1 = new Intent(AccountActivity.this, MyListsActivity.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        break;

                    /*case R.id.nav_test:
                        Intent intent3 = new Intent(AccountActivity.this, TestActivity.class);
                        startActivity(intent3);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        break;*/

                    case R.id.nav_account:
                        /*Intent intent4 = new Intent(ListsActivity.this, AccountActivity.class);
                        startActivity(intent4);
                        break;*/

                }


                return false;
            }
        });
        changeLanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanDialog();
            }
        });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestId()
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getApplication(), gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent0 = new Intent(AccountActivity.this, AuthenticationActivity.class);
                startActivity(intent0);
                finish();
            }
        });
    }

    public void lanDialog() {
        /*Alert dialog -- Method creates locale change dialog*/
        final String[] vocaleList = {"English", "Čeština"};
        AlertDialog.Builder alertBuider = new AlertDialog.Builder(AccountActivity.this);
        alertBuider.setTitle("Choose language");
        alertBuider.setSingleChoiceItems(vocaleList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    //English
                    setLocale("en");
                    recreate();
                } else if (which == 1) {
                    //Czech
                    setLocale("cs");
                    recreate();
                }

            }
        });
        AlertDialog lDialog = alertBuider.create();
        lDialog.show();
    }
    /* Method changes used String set*/
    private void setLocale(String loc) {
        Locale locale = new Locale(loc);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", loc);
        editor.apply();
    }

    /*This method saves user prefered Locale, work is still in progress -> never used */
    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "en");
        setLocale(language);
    }
}

