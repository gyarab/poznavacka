package com.example.timad.poznavacka.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.timad.poznavacka.PoznavackaInfo;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.Zastupce;
import com.example.timad.poznavacka.activities.lists.MyListsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class PracticeActivity2 extends AppCompatActivity {

    TextView textView;
    ImageView imageView;
    ImageView imageView_classic;
    FloatingActionButton fab_crossed;
    FloatingActionButton fab_checked;
    FloatingActionButton fab_restart;
    View sceneView;

    // Array with zastupci
    PoznavackaInfo mLoadedPoznavacka;
    ArrayList<Zastupce> mZastupceArrOrig;
    int parameterCount;

    ArrayList<Integer> nenauceniZastupci = new ArrayList<>();
    int[] zastupciProPriste;
    int puvodniPocetVsechZastupcu;
    int pocetVsechZastupcu;
    int currentZastupce;

    protected Resources res;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice2);
        init();
        initValues();

        //hiding the scene
        fab_restart.hide();

        Log.d("Obrazek", "Working so far");

        //first appearance
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
                zastupciProPriste[currentZastupce] += 1;
                hideScene();
                if(pocetVsechZastupcu == 1){
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

                    // Ulozeni nenaucenych zastupcu
                    ArrayList<Integer> arr = new ArrayList<>();
                    for (int i = 0; i < zastupciProPriste.length; i++){
                        if(zastupciProPriste[i] > 0){
                            arr.add(i);
                        }
                    }

                    PracticeActivity.sNenauceniZastupci = arr;
                    MyListsFragment.getSMC(getApplicationContext()).updateFile(PracticeActivity.sLoadedPoznavacka.getId() + "/", "nenauceni.txt", arr);

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
                Intent welcomeIntent = new Intent(PracticeActivity2.this, SwitchActivity.class);
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
        Log.d("Zastupci", "Pocer: " + pocetVsechZastupcu);
        textView.setVisibility(View.INVISIBLE);
        fab_checked.hide();
        fab_crossed.hide();
    }

    private void initValues() {
        res = getResources();

        mZastupceArrOrig = PracticeActivity.sZastupceArrOrig;
        puvodniPocetVsechZastupcu = mZastupceArrOrig.size();
        parameterCount = mZastupceArrOrig.get(0).getParameters();

        Bundle b = getIntent().getExtras();
        int value = -1;
        if(b != null) {
            value = b.getInt("key");
        }

        if(value == 1){ // ALL
            if(mZastupceArrOrig.get(0).getParameter(0).equals("") || mZastupceArrOrig.get(0).getParameter(0).isEmpty()){
                puvodniPocetVsechZastupcu -= 1;
            }
            pocetVsechZastupcu = puvodniPocetVsechZastupcu;
            nenauceniZastupci = fillArr();
        } else if (value == 0) { // Not memorized
            puvodniPocetVsechZastupcu = mZastupceArrOrig.size();
            nenauceniZastupci = PracticeActivity.sNenauceniZastupci;
            pocetVsechZastupcu = nenauceniZastupci.size();
        }

        zastupciProPriste = new int[puvodniPocetVsechZastupcu];
        for (int i = 0; i < zastupciProPriste.length; i++) {
            zastupciProPriste[i] = 0;
        }
    }

    private ArrayList<Integer> fillArr(){
        ArrayList<Integer> arr = new ArrayList<Integer>();
        if(mZastupceArrOrig.get(0).getParameter(0).equals("") || mZastupceArrOrig.get(0).getParameter(0).isEmpty()) {
            for (int i = 1; i < mZastupceArrOrig.size(); i++){
                arr.add(i);
            }
        } else {
            for (int i = 0; i < mZastupceArrOrig.size(); i++){
                arr.add(i);
            }
        }

        return arr;
    }
}
