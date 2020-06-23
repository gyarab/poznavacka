package com.example.timad.poznavacka.activities.practice;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.timad.poznavacka.ClassificationData;
import com.example.timad.poznavacka.PoznavackaInfo;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.Zastupce;
import com.example.timad.poznavacka.activities.AccountActivity;
import com.example.timad.poznavacka.activities.SwitchActivity;
import com.example.timad.poznavacka.activities.lists.MyListsActivity;
import com.example.timad.poznavacka.activities.test.TestActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;
import timber.log.Timber;

public class PracticeActivity2 extends AppCompatActivity {

    TextView textView;
    ImageView imageView;
    ImageView imageView_classic;
    FloatingActionButton fab_crossed;
    FloatingActionButton fab_checked;
    FloatingActionButton fab_restart;
    View sceneView;

    TextView fab_checked_text;
    TextView fab_crossed_text;
    boolean firstShown;

    // Array with zastupci
    PoznavackaInfo mLoadedPoznavacka;
    ArrayList<Object> mZastupceArrOrig;
    int parameterCount;
    boolean rad;

    ArrayList<Integer> nenauceniZastupci = new ArrayList<>();
    int puvodniPocetVsechZastupcu;
    int pocetVsechZastupcu;
    int currentZastupce;

    protected Resources res;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice2);
        init();
        initValues();

        Timber.d("Here working");
        //hiding the scene
        fab_restart.hide();

        //first appearance
        hideScene();
        updateScene(get_setRandomisedCurrentZastupce());

        sceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showScene();
            }
        });

        fab_crossed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideScene();
                if (pocetVsechZastupcu <= 1) {
                    updateScene(currentZastupce);
                } else {
                    updateScene(get_setRandomisedCurrentZastupce());
                }
            }
        });

        fab_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //removes the current representative
                if (pocetVsechZastupcu > 1) {
                    nenauceniZastupci.removeAll(Collections.singleton(currentZastupce));
                    pocetVsechZastupcu--;
                    hideScene();
                    updateScene(get_setRandomisedCurrentZastupce());

                } else {
                    //all learnt
                    imageView_classic.setVisibility(View.VISIBLE);
                    imageView_classic.setImageResource(R.drawable.check);
                    imageView.setVisibility(View.INVISIBLE);
                    hideScene();
                    sceneView.setClickable(false);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(null);

                    nenauceniZastupci.removeAll(Collections.singleton(currentZastupce));
                    PracticeActivity.sNenauceniZastupci = nenauceniZastupci;
                    MyListsActivity.getSMC(getApplicationContext()).updateFile(PracticeActivity.sLoadedPoznavacka.getId() + "/", "nenauceni.txt", nenauceniZastupci);
                }
            }
        });

        fab_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nenauceniZastupci.clear();
                hideScene();
                fab_restart.hide();
                sceneView.setClickable(true);
                Intent welcomeIntent = new Intent(PracticeActivity2.this, SwitchActivity.class);
                startActivity(welcomeIntent);
                imageView_classic.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
            }
        });

        //ad
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        //BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0).setIcon(R.drawable.brain_filled_white);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_practice:
                        /*Intent intent0 = new Intent(ListsActivity.this, PracticeActivity.class);
                        startActivity(intent0);*/
                        break;

                    case R.id.nav_lists:
                        Intent intent1 = new Intent(PracticeActivity2.this, MyListsActivity.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;

                    /*case R.id.nav_test:
                        Intent intent3 = new Intent(PracticeActivity2.this, TestActivity.class);
                        startActivity(intent3);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;*/

                    case R.id.nav_account:
                        Intent intent4 = new Intent(PracticeActivity2.this, AccountActivity.class);
                        startActivity(intent4);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        // Save state when leaving this activity before learning all
        if (nenauceniZastupci.size() > 0) {
            PracticeActivity.sNenauceniZastupci = nenauceniZastupci;
            MyListsActivity.getSMC(getApplicationContext()).updateFile(PracticeActivity.sLoadedPoznavacka.getId() + "/", "nenauceni.txt", nenauceniZastupci);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Save state when leaving this activity before learning all
        if (nenauceniZastupci.size() > 0) {
            PracticeActivity.sNenauceniZastupci = nenauceniZastupci;
            MyListsActivity.getSMC(getApplicationContext()).updateFile(PracticeActivity.sLoadedPoznavacka.getId() + "/", "nenauceni.txt", nenauceniZastupci);
        }
        Intent intent = new Intent(this, MyListsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private int get_setRandomisedCurrentZastupce() {
        if (pocetVsechZastupcu <= 1) {
            return currentZastupce;
        }
        int randomisedCurrentZastupce = new Random().nextInt(pocetVsechZastupcu);
        while (nenauceniZastupci.get(randomisedCurrentZastupce) == currentZastupce) {
            randomisedCurrentZastupce = new Random().nextInt(pocetVsechZastupcu);
        }
        currentZastupce = nenauceniZastupci.get(randomisedCurrentZastupce);
        return currentZastupce;
    }

    /**
     * Inicializace grafických prvků
     */
    private void init() {
        textView = findViewById(R.id.textName);
        fab_crossed = findViewById(R.id.floatingActionButton_crossed);
        fab_checked = findViewById(R.id.floatingActionButton_checked);
        fab_restart = findViewById(R.id.floatingActionButton_restart);
        imageView = findViewById(R.id.imageView);
        imageView_classic = findViewById(R.id.imageView_classic);
        sceneView = findViewById(R.id.sceneView);
        fab_checked_text = findViewById(R.id.floatingActionButton_checked_text);
        fab_crossed_text = findViewById(R.id.floatingActionButton_crossed_text);
        Timber.plant(new Timber.DebugTree());
    }

    private void updateScene(int i) {
        Timber.d("Whats up");
        String s = "";

        Timber.d("updateScene(): rad is %s", rad);
        for (int x = 0; x < parameterCount; x++) {
            Timber.d("updateScene(): parametersCount = %s", parameterCount);
            if (rad) {
                if (x > 0) {
                    s += ((ClassificationData) mZastupceArrOrig.get(0)).getClassification().get(x - 1) + ": ";
                    Timber.d("updateScene(): classification added - %s", s);
                }
            }
            s += ((Zastupce) mZastupceArrOrig.get(i)).getParameter(x) + "\n";
        }

        textView.setText(s);
        imageView.setImageDrawable(((Zastupce) mZastupceArrOrig.get(currentZastupce)).getImage());
    }

    private void showScene() {
        textView.setVisibility(View.VISIBLE);
        fab_checked.show();
        fab_crossed.show();
        if (!firstShown) {
            fab_checked_text.setVisibility(View.VISIBLE);
            fab_crossed_text.setVisibility(View.VISIBLE);
        }
        firstShown = true;
    }

    private void hideScene() {
        textView.setVisibility(View.INVISIBLE);
        fab_checked.hide();
        fab_crossed.hide();
        fab_checked_text.setVisibility(View.INVISIBLE);
        fab_crossed_text.setVisibility(View.INVISIBLE);
    }

    /**
     * Z balíčku, který jí byl předán, zjistí, zda je uživatel testován ze všech zástupců nebo jen z nezapamatovaných a podle toho nastaví parametry.
     */
    private void initValues() {
        res = getResources();

        TextViewCompat.setAutoSizeTextTypeWithDefaults(textView, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        mZastupceArrOrig = PracticeActivity.sZastupceArrOrig;
        for (Object o :
                mZastupceArrOrig) {
            if (o instanceof ClassificationData) {
                Timber.d("initValues(): mZastupceArrOrig item is ClassificationData");
            } else {
                Timber.d("initValues(): mZastupceArrOrig item is Zastupce");
            }
        }
        Timber.d("initValues(): mZastupceArrOrig");
        puvodniPocetVsechZastupcu = mZastupceArrOrig.size();
        rad = mZastupceArrOrig.get(0) instanceof ClassificationData;
        if (rad) {
            parameterCount = ((ClassificationData) mZastupceArrOrig.get(0)).getClassification().size() + 1;
        } else {
            parameterCount = 1;
        }
        Timber.d("initValues(): parameterCount = %s", parameterCount);

        Bundle b = getIntent().getExtras();
        int value = -1;
        if (b != null) {
            value = b.getInt("key");
        }

        if (value == 1) { // ALL
            if (rad) {
                puvodniPocetVsechZastupcu -= 1;
            }
            pocetVsechZastupcu = puvodniPocetVsechZastupcu;
            nenauceniZastupci = fillArr();
        } else if (value == 0) { // Not memorized
            nenauceniZastupci = PracticeActivity.sNenauceniZastupci;
            pocetVsechZastupcu = nenauceniZastupci.size();
        }
    }

    private ArrayList<Integer> fillArr() {
        ArrayList<Integer> arr = new ArrayList<>();
        if (rad) {
            for (int i = 1; i < mZastupceArrOrig.size(); i++) {
                arr.add(i);
            }
        } else {
            for (int i = 0; i < mZastupceArrOrig.size(); i++) {
                arr.add(i);
            }
        }

        return arr;
    }
}
