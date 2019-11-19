package com.example.timad.poznavacka;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class PracticeActivity extends AppCompatActivity {

    TextView textView;
    ImageView imageView;
    ImageView imageView_classic;
    FloatingActionButton fab_crossed;
    FloatingActionButton fab_checked;
    FloatingActionButton fab_restart;
    View sceneView;

    //load this from firebase database
    List<String> zastupci = Arrays.asList("Mlok skvrnitý", "Velemlok čínský", "Čolek obecný", "Čolek velký", "Červor", "Ropucha obecná", "Rosnička obecná", "Kuňka obecná", "Skokan hnědý", "Skokan zelený", "Kožatka velká", "Kareta obecná", "Želva sloní", "Želva nádherná", "Želva bahenní", "Hatérie novozélandská", "Gekon zední", "Leguán zelený", "Ještěrka obecná", "Ještěrka zelená", "Slepýš křehký", "Anakonda velká", "Kobra indická", "Taipan velký", "Užovka obojková", "Užovka podplamatá", "Zmije obecná", "Krokodýl nilský", "Aligátor severoamerický", "Gaviál indický", "Pštros dvouprstý", "Nandu pampový", "Kasuár přilbový", "Emu hnědý", "Kachna divoká", "Polák chocholačka", "Morčák velký");
    List<String> rady = Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "Hatérie", "Šupinatí", "Šupinatí", "Šupinatí", "Šupinatí", "Šupinatí", "Šupinatí", "Šupinatí", "Šupinatí", "Šupinatí", "Šupinatí", "Šupinatí", "Krokodýli", "Krokodýli", "Krokodýli", "Pštrosi", "Nanduové", "Kasuárové", "Kasuárové", "Vrubozubí", "Vrubozubí", "Vrubozubí", "Vrubozubí", "Vrubozubí", "Vrubozubí");
    List<String> tridy = Arrays.asList("Obojživelníci", "Obojživelníci", "Obojživelníci", "Obojživelníci", "Obojživelníci", "Obojživelníci", "Obojživelníci", "Obojživelníci", "Obojživelníci", "Obojživelníci", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Plazi", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci", "Ptáci");

    static boolean loaded = false;
    static ArrayList<Integer> nenauceniZastupci = new ArrayList<>();
    int puvodniPocetVsechZastupcu;
    int pocetVsechZastupcu;
    int currentZastupce;

    protected Resources res; //instead load from firebase storage


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        init();

        //hiding the scene
        hideScene();
        fab_restart.hide();

        res = getResources(); //vymenit za firebase

        if (loaded) {

        /*
        Log.d("zastupci", String.valueOf(zastupci.size()));
        Log.d("rady", String.valueOf(rady.size()));
        Log.d("tridy", String.valueOf(tridy.size()));
         */

            puvodniPocetVsechZastupcu = nenauceniZastupci.size();
            pocetVsechZastupcu = puvodniPocetVsechZastupcu;


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
                showScene();
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
                    imageView_classic.setImageResource(R.drawable.devilbrinke_deepfryed);
                    imageView.setVisibility(View.INVISIBLE);
                    hideScene();
                    sceneView.setClickable(false);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(null);

                    final Handler handler = new Handler();
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
                    }, 4000);
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
        imageView.setImageDrawable(ResourcesCompat.getDrawable(res, getResources().getIdentifier("image" + i, "drawable", getPackageName()), null));
        textView.setText(String.format("%s\n%s\n%s", tridy.get(i), rady.get(i), zastupci.get(i)));
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