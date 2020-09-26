package com.adamec.timotej.poznavacka.activities.practice;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adamec.timotej.poznavacka.ClassificationData;
import com.adamec.timotej.poznavacka.PoznavackaInfo;
import com.adamec.timotej.poznavacka.R;
import com.adamec.timotej.poznavacka.Zastupce;
import com.adamec.timotej.poznavacka.activities.AccountActivity;
import com.adamec.timotej.poznavacka.activities.lists.MyListsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;

public class PracticeActivity extends AppCompatActivity {

    public static final int ALL = 1;
    public static final int NOT_MEM = 0;

    TextView mTextView;
    Button mButtonAll;
    Button mButtonContinue;

    // Array with zastupci
    public static PoznavackaInfo sLoadedPoznavacka;
    public static ArrayList<Object> sZastupceArrOrig = new ArrayList<>();
    //int parameterCount;

    public static boolean mLoaded = false;
    public static ArrayList<Integer> sNenauceniZastupci = new ArrayList<>();

    protected Resources res;

    /**
     * Načte Poznávačku ze zařízení, pokud není načtení.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        init();
        updateData();

        /*if (MyListsActivity.sActivePoznavacka != null) {
            if (sLoadedPoznavacka != null && sLoadedPoznavacka.getId().equals(MyListsActivity.sActivePoznavacka.getId())) {
                mLoaded = true;
                updateData();
            } else {
                new LoadPoznavacka().execute();
            }
        }*/

        //navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
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
                        Intent intent1 = new Intent(PracticeActivity.this, MyListsActivity.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;

                    /*case R.id.nav_test:
                        Intent intent3 = new Intent(PracticeActivity.this, TestActivity.class);
                        startActivity(intent3);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;*/

                    case R.id.nav_account:
                        Intent intent4 = new Intent(PracticeActivity.this, AccountActivity.class);
                        startActivity(intent4);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                }

                return false;
            }
        });
    }

    private void updateData() {
        if (!mLoaded) {
            Timber.d("Practice load (PracticeActivity), not loaded");
            mTextView.setText(R.string.select_list);
            mButtonAll.setEnabled(false);
            mButtonAll.setText("(0)");
            mButtonContinue.setEnabled(false);
            mButtonContinue.setText("(0)");
        } else {
            Timber.d("Practice load (PracticeActivity), loaded");
            int count = sZastupceArrOrig.size();
            if (sZastupceArrOrig.get(0) instanceof ClassificationData) {
                count -= 1;
            }
            if (count == PracticeActivity.sNenauceniZastupci.size()) {
                practice2Transition(PracticeActivity.ALL);
            } else if (PracticeActivity.sNenauceniZastupci.size() < 1) {
                practice2Transition(PracticeActivity.ALL);
            }
            mButtonAll.setText(getString(R.string.practice_all) + count + ")");
            mButtonContinue.setText(getString(R.string.continue_practice) + sNenauceniZastupci.size() + ")");
        }
    }

    private class LoadPoznavacka extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            loadPoznavacka();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateData();
        }
    }


    private void loadPoznavacka() {
        Gson gson = new Gson();
        Context context = this;
        String path = MyListsActivity.sActivePoznavacka.getId() + "/";
        sZastupceArrOrig.clear();

        Type zastupceArrType = new TypeToken<ArrayList<Zastupce>>() {
        }.getType();
        Type classificationArrType = new TypeToken<ClassificationData>() {
        }.getType();
        String jsonZastupce = MyListsActivity.getSMC(context).readFile(path + MyListsActivity.sActivePoznavacka.getId() + ".txt", false);
        String jsonClassification = MyListsActivity.getSMC(context).readFile(path + MyListsActivity.sActivePoznavacka.getId() + "classification.txt", false);
        ArrayList<Zastupce> zastupces = gson.fromJson(jsonZastupce, zastupceArrType);
        if (jsonClassification != null && !jsonClassification.isEmpty()) {
            ClassificationData classificationData = gson.fromJson(jsonClassification, classificationArrType);
            if (classificationData != null && classificationData.getClassification().size() != 0 && !classificationData.getClassification().get(0).isEmpty())
                sZastupceArrOrig.add(classificationData);
        }
        sZastupceArrOrig.addAll(zastupces);

        Type intArrType = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        String jsonInt = MyListsActivity.getSMC(context).readFile(path + "nenauceni.txt", true);
        try {
            if (jsonInt.equals("") || jsonInt.isEmpty()) {
                sNenauceniZastupci = fillArr();
                Timber.d("sNenauceniZastupci - fillArr()");
            } else {
                sNenauceniZastupci = gson.fromJson(jsonInt, intArrType);
                Timber.d("sNenauceniZastupci - fromJson()");
            }
        } catch (Exception e) {
            Timber.e("sNenauceniZastupci error - %s", e);
            //Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        for (Object z : sZastupceArrOrig) {
            if (z instanceof Zastupce) {
                ((Zastupce) z).setImage(MyListsActivity.getSMC(context).readDrawable(path, ((Zastupce) z).getParameter(0), context));
            }
        }

        Timber.d("This should be called once");
        sLoadedPoznavacka = MyListsActivity.sActivePoznavacka;
        mLoaded = true;
    }

    /**
     * Nastavení Event Listenerů u tlačítek
     */
    public void init() {
        Timber.plant(new Timber.DebugTree());
        mTextView = findViewById(R.id.textViewLoaded);
        mTextView.setText("");
        mButtonAll = findViewById(R.id.buttonAll);
        mButtonAll.setOnClickListener(new View.OnClickListener() {
            /**
             * Po kliknutí na talčítko "testovat vše" spustí Practice Activity 2 s parametrem ALL.
             *
             * @param view
             */
            @Override
            public void onClick(View view) {
                practice2Transition(ALL);
            }
        });

        mButtonContinue = findViewById(R.id.buttonNotMem);
        mButtonContinue.setOnClickListener(new View.OnClickListener() {
            /**
             * Po kliknutí na talčítko "testovat nenaučená" spustí Practice Activity 2 s parametrem NOT_MEM.
             *
             * @param view
             */
            @Override
            public void onClick(View view) {
                practice2Transition(NOT_MEM);
            }
        });

        //mLoaded = false;
    }

    private void practice2Transition(int type) {
        Intent intent = new Intent(PracticeActivity.this, PracticeActivity2.class);
        Bundle b = new Bundle();
        b.putInt("key", type);
        intent.putExtras(b);
        startActivity(intent);
        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
        finish();
    }

    /**
     * Vrátí array s nazapamatovanými zástupci do původního stavu, to znamená, že array naplní všemi Zástupci.
     *
     * @return
     */
    public ArrayList<Integer> fillArr() {
        ArrayList<Integer> arr = new ArrayList<Integer>();

        if (sZastupceArrOrig.get(0) instanceof ClassificationData) {
            for (int i = 1; i < sZastupceArrOrig.size(); i++) {
                arr.add(i);
            }
        } else {
            for (int i = 0; i < sZastupceArrOrig.size(); i++) {
                arr.add(i);
            }
        }

        return arr;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MyListsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}