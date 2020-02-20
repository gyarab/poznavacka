package com.example.timad.poznavacka.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.timad.poznavacka.BottomNavigationViewHelper;
import com.example.timad.poznavacka.PoznavackaInfo;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.Zastupce;
import com.example.timad.poznavacka.activities.lists.ListsActivity;
import com.example.timad.poznavacka.activities.lists.MyListsFragment;
import com.example.timad.poznavacka.activities.test.TestActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PracticeActivity extends AppCompatActivity {

    TextView textView;
    ImageView imageView;
    ImageView imageView_classic;
    FloatingActionButton fab_crossed;
    FloatingActionButton fab_checked;
    FloatingActionButton fab_restart;
    View sceneView;

    // Array with zastupci
    PoznavackaInfo mLoadedPoznavacka;
    ArrayList<Zastupce> mZastupceArrOrig = null;
    int parameterCount;

    //load this from local storage
    List<String> zastupci = Arrays.asList("Mlok skvrnitý", "Velemlok čínský", "Čolek obecný", "Čolek velký", "Červor", "Ropucha obecná", "Rosnička obecná", "Kuňka obecná", "Skokan hnědý", "Skokan zelený", "Kožatka velká", "Kareta obecná", "Želva sloní", "Želva nádherná", "Želva bahenní", "Hatérie novozélandská", "Gekon zední", "Leguán zelený", "Ještěrka obecná", "Ještěrka zelená", "Slepýš křehký", "Anakonda velká", "Kobra indická", "Taipan velký", "Užovka obojková", "Užovka podplamatá", "Zmije obecná", "Krokodýl nilský", "Aligátor severoamerický", "Gaviál indický", "Pštros dvouprstý", "Nandu pampový", "Kasuár přilbový", "Emu hnědý", "Kachna divoká", "Polák chocholačka", "Morčák velký");
    List<String> rady = Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "Hatérie", "Šupinatí", "Šupinatí", "Šupinatí", "Šupinatí", "Šupinatí", "Šupinatí", "Šupinatí", "Šupinatí", "Šupinatí", "Šupinatí", "Šupinatí", "Krokodýli", "Krokodýli", "Krokodýli", "Pštrosi", "Nanduové", "Kasuárové", "Kasuárové", "Vrubozubí", "Vrubozubí", "Vrubozubí", "Vrubozubí", "Vrubozubí", "Vrubozubí");
    List<String> tridy = Arrays.asList("Obojživelníci", "Obojživelníci", "Obojživelníci", "Obojživelníci", "Obojživelníci", "Obojživelníci", "Obojživelníci", "Obojživelníci", "Obojživelníci", "Obojživelníci", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci");

    static boolean loaded = false;
    static ArrayList<Integer> nenauceniZastupci = new ArrayList<>();
    int puvodniPocetVsechZastupcu;
    int pocetVsechZastupcu;
    int currentZastupce;

    protected Resources res;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        init();

        //hiding the scene
        fab_restart.hide();

        res = getResources();
        loaded = false;

        outer: if(MyListsFragment.sActivePoznavacka != null){
            if(mLoadedPoznavacka == null){
                // Proceed
            } else if (mLoadedPoznavacka.getId().equals(MyListsFragment.sActivePoznavacka.getId())){
                loaded = true;
                break outer;
            }

            Gson gson = new Gson();
            Context context = this;
            String path = MyListsFragment.sActivePoznavacka.getId() + "/";

            /*File f = new File(context.getFilesDir().getPath() + "/" + path);
            Log.d("ObrazekF", "Path: " + context.getFilesDir().getPath() + "/" + path);
            File[] files = f.listFiles();
            for (File f2: files){
                Log.d("ObrazekF", "Files:" + f2.getName());
            }*/

            Type cType = new TypeToken<ArrayList<Zastupce>>(){}.getType();
            String json = MyListsFragment.getSMC(context).readFile(path + MyListsFragment.sActivePoznavacka.getId() + ".txt", false);
            mZastupceArrOrig = gson.fromJson(json, cType);

            for (Zastupce z: mZastupceArrOrig) {
                z.setImage(MyListsFragment.getSMC(context).readDrawable(path, z.getParameter(0), context));
            }

            mLoadedPoznavacka = MyListsFragment.sActivePoznavacka;
            parameterCount = mZastupceArrOrig.get(0).getParameters();
            loaded = true;
        }

        if (loaded) {

        /*
        Log.d("zastupci", String.valueOf(zastupci.size()));
        Log.d("rady", String.valueOf(rady.size()));
        Log.d("tridy", String.valueOf(tridy.size()));
         */

            puvodniPocetVsechZastupcu = mZastupceArrOrig.size() - 1;
            pocetVsechZastupcu = puvodniPocetVsechZastupcu;

            nenauceniZastupci = new ArrayList<Integer>();
            for(int i = 1; i < mZastupceArrOrig.size(); i++) {
                nenauceniZastupci.add(i);
            }

            Log.d("Obrazek", "Working so far");

            //first appearance
            updateScene(get_setRandomisedCurrentZastupce());
        } else {

            textView.setText(R.string.select_list);
            fab_crossed.hide();
            fab_checked.hide();
            fab_restart.hide();
        }


        sceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loaded) {
                    showScene();
                }
            }
        });


        fab_crossed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideScene();
                updateScene(get_setRandomisedCurrentZastupce());
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

                    /*final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(R.string.all_learnt_message_0);
                        }
                    }, 1500);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(R.string.all_learnt_message_1);
                            fab_restart.show();
                        }
                    }, 4000);*/
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
                Intent welcomeIntent = new Intent(PracticeActivity.this, SwitchActivity.class);
                startActivity(welcomeIntent);
                imageView_classic.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
            }
        });


        //navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
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
                        Intent intent1 = new Intent(PracticeActivity.this, ListsActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.nav_test:
                        Intent intent3 = new Intent(PracticeActivity.this, TestActivity.class);
                        startActivity(intent3);
                        break;

                    case R.id.nav_account:
                        Intent intent4 = new Intent(PracticeActivity.this, AccountActivity.class);
                        startActivity(intent4);
                        break;


                }


                return false;
            }
        });
    }


    private int get_setRandomisedCurrentZastupce() {
        //if (pocetVsechZastupcu > 1) {
        int randomisedCurrentZastupce = new Random().nextInt(pocetVsechZastupcu);
        while (nenauceniZastupci.get(randomisedCurrentZastupce) == currentZastupce) {
            randomisedCurrentZastupce = new Random().nextInt(pocetVsechZastupcu);
        }
        currentZastupce = nenauceniZastupci.get(randomisedCurrentZastupce);
        return currentZastupce;
        /*} else {
            return currentZastupce;
        }*/

    }

    private void init() {
        textView = findViewById(R.id.textName);
        fab_crossed = findViewById(R.id.floatingActionButton_crossed);
        fab_checked = findViewById(R.id.floatingActionButton_checked);
        fab_restart = findViewById(R.id.floatingActionButton_restart);
        imageView = findViewById(R.id.imageView);
        imageView_classic = findViewById(R.id.imageView_classic);
        sceneView = findViewById(R.id.sceneView);
    }

    private void updateScene(int i) {
        /*imageView.setImageDrawable(ResourcesCompat.getDrawable(res, getResources().getIdentifier("image" + i, "drawable", getPackageName()), null));
        textView.setText(String.format("%s\n%s\n%s", tridy.get(i), rady.get(i), zastupci.get(i)));*/

        String s = "";

        for (int x = 0; x < parameterCount; x++) {
            s += mZastupceArrOrig.get(i).getParameter(x) + "\n";
        }

        textView.setText(s);
        imageView.setImageDrawable(mZastupceArrOrig.get(currentZastupce).getImage());
    }

    private void showScene() {
        textView.setVisibility(View.VISIBLE);
        fab_checked.show();
        fab_crossed.show();
    }

    private void hideScene() {
        textView.setVisibility(View.INVISIBLE);
        fab_checked.hide();
        fab_crossed.hide();
    }
}