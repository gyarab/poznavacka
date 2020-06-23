package com.example.timad.poznavacka.activities.practice;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timad.poznavacka.ClassificationData;
import com.example.timad.poznavacka.PoznavackaInfo;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.Zastupce;
import com.example.timad.poznavacka.activities.AccountActivity;
import com.example.timad.poznavacka.activities.lists.MyListsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;

public class PracticeActivity extends AppCompatActivity {

    private static final int ALL = 1;
    private static final int NOT_MEM = 0;

    TextView mTextView;
    Button mButtonAll;
    Button mButtonContinue;

    // Array with zastupci
    static PoznavackaInfo sLoadedPoznavacka;
    static ArrayList<Object> sZastupceArrOrig = new ArrayList<>();
    //int parameterCount;

    boolean mLoaded = false;
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

        if (MyListsActivity.sActivePoznavacka != null) {
            if (sLoadedPoznavacka != null && sLoadedPoznavacka.getId().equals(MyListsActivity.sActivePoznavacka.getId())) {
                mLoaded = true;
            } else {
                loadPoznavacka();
            }
        }

        if (!mLoaded) {
            mTextView.setText(R.string.select_list);
            mButtonAll.setEnabled(false);
            mButtonAll.setText("(0)");
            mButtonContinue.setEnabled(false);
            mButtonContinue.setText("(0)");
        } else {
            int count = sZastupceArrOrig.size();
            for (Object o :
                    sZastupceArrOrig) {
                if (o instanceof ClassificationData) {
                    Timber.d("sZastupceArrOrig item is ClassificationData = " + ((ClassificationData) o).getClassification());
                } else {
                    //for (int i = 0; i < ((Zastupce) o).getParameters(); i++) {
                    Timber.d("sZastupceArrOrig item is Zastupce = " + ((Zastupce) o).getParameter(0));
                    //}
                }
            }

            if (sZastupceArrOrig.get(0) instanceof ClassificationData) {
                count -= 1;
            }
            mButtonAll.setText(getString(R.string.practice_all) + count + ")");
            /*for (int i = 0; i < sZastupceArrOrig.size(); i++) {
                if (i == 0) {
                    Timber.d("sZastupceArrOrig - " + i + " = " + ((ClassificationData) sZastupceArrOrig.get(0)).getClassification());
                } else {
                    for (int k = 0; k < ((Zastupce) sZastupceArrOrig.get(i)).getParameters(); k++) {
                        Timber.d("sZastupceArrOrig - " + i + " = " + ((Zastupce) sZastupceArrOrig.get(i)).getParameter(k));
                    }
                }
            }*/
            mButtonContinue.setText(getString(R.string.continue_practice) + sNenauceniZastupci.size() + ")");
            if (sNenauceniZastupci.size() < 1) {
                mButtonContinue.setEnabled(false);
            }
        }

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

            } else {
                sNenauceniZastupci = gson.fromJson(jsonInt, intArrType);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(PracticeActivity.this, PracticeActivity2.class);
                Bundle b = new Bundle();
                b.putInt("key", ALL);
                intent.putExtras(b);
                startActivity(intent);
                overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                finish();
            }
        });

        mButtonContinue = findViewById(R.id.buttonNotMem);
        mButtonContinue.setOnClickListener(new View.OnClickListener() {
            /**
             * Po kliknutí na talčítko "testovat nenaučená" spustí Practice Activity 2 s parametrem NOT_MEM.
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PracticeActivity.this, PracticeActivity2.class);
                Bundle b = new Bundle();
                b.putInt("key", NOT_MEM);
                intent.putExtras(b);
                startActivity(intent);
                overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                finish();
            }
        });

        mLoaded = false;
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