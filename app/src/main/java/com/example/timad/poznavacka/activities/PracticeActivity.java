package com.example.timad.poznavacka.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.timad.poznavacka.BottomNavigationViewHelper;
import com.example.timad.poznavacka.PoznavackaInfo;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.Zastupce;
import com.example.timad.poznavacka.activities.lists.ListsActivity;
import com.example.timad.poznavacka.activities.lists.MyListsFragment;
import com.example.timad.poznavacka.activities.test.TestActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PracticeActivity extends AppCompatActivity {

    private static final int ALL = 1;
    private static  final int NOT_MEM = 0;

    TextView mTextView;
    Button mButtonAll;
    Button mButtonContinue;

    // Array with zastupci
    static PoznavackaInfo sLoadedPoznavacka;
    static ArrayList<Zastupce> sZastupceArrOrig = null;
    //int parameterCount;

    boolean mLoaded = false;
    public static ArrayList<Integer> sNenauceniZastupci = new ArrayList<>();

    protected Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        init();

        outer: if(MyListsFragment.sActivePoznavacka != null){
            if(sLoadedPoznavacka == null){
                // Proceed
            } else if (sLoadedPoznavacka.getId().equals(MyListsFragment.sActivePoznavacka.getId())){
                mLoaded = true;
                break outer;
            }

            Gson gson = new Gson();
            Context context = this;
            String path = MyListsFragment.sActivePoznavacka.getId() + "/";

            Type zastupceArrType = new TypeToken<ArrayList<Zastupce>>(){}.getType();
            String jsonZastupce = MyListsFragment.getSMC(context).readFile(path + MyListsFragment.sActivePoznavacka.getId() + ".txt", false);
            sZastupceArrOrig = gson.fromJson(jsonZastupce, zastupceArrType);

            Type intArrType =  new TypeToken<ArrayList<Integer>>(){}.getType();
            String jsonInt = MyListsFragment.getSMC(context).readFile(path + "nenauceni.txt", true);
            if(jsonInt.equals("") || jsonInt.isEmpty()) {
                sNenauceniZastupci = fillArr();
            } else {
                sNenauceniZastupci = gson.fromJson(jsonInt, intArrType);
            }

            for (Zastupce z: sZastupceArrOrig) {
                z.setImage(MyListsFragment.getSMC(context).readDrawable(path, z.getParameter(0), context));
            }

            sLoadedPoznavacka = MyListsFragment.sActivePoznavacka;
            //parameterCount = sZastupceArrOrig.get(0).getParameters();
            mLoaded = true;
        }

        if(!mLoaded){
            mTextView.setText(R.string.select_list);
            mButtonAll.setEnabled(false);
            mButtonAll.setText("(0)");
            mButtonContinue.setEnabled(false);
            mButtonContinue.setText("(0)");
        } else {
            int count = sZastupceArrOrig.size();
            if(sZastupceArrOrig.get(0).getParameter(0).equals("") || sZastupceArrOrig.get(0).getParameter(0).isEmpty()){
                count -= 1;
            }
            mButtonAll.setText("Practice all (" + count + ")");
            mButtonContinue.setText("Continue practicing (" + sNenauceniZastupci.size() + ")");
            if(sNenauceniZastupci.size() < 1){
                mButtonContinue.setEnabled(false);
            }
        }

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
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;

                    case R.id.nav_test:
                        Intent intent3 = new Intent(PracticeActivity.this, TestActivity.class);
                        startActivity(intent3);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;

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

    public void init(){
        mTextView = findViewById(R.id.textViewLoaded);
        mTextView.setText("");
        mButtonAll = findViewById(R.id.buttonAll);
        mButtonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PracticeActivity.this, PracticeActivity2.class);
                Bundle b = new Bundle();
                b.putInt("key", ALL);
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        });

        mButtonContinue = findViewById(R.id.buttonNotMem);
        mButtonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PracticeActivity.this, PracticeActivity2.class);
                Bundle b = new Bundle();
                b.putInt("key", NOT_MEM);
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        });

        mLoaded = false;
    }

    public ArrayList<Integer> fillArr(){
        ArrayList<Integer> arr = new ArrayList<Integer>();
        if(sZastupceArrOrig.get(0).getParameter(0).equals("") || sZastupceArrOrig.get(0).getParameter(0).isEmpty()) {
            for (int i = 1; i < sZastupceArrOrig.size(); i++){
                arr.add(i);
            }
        } else {
            for (int i = 0; i < sZastupceArrOrig.size(); i++){
                arr.add(i);
            }
        }

        return arr;
    }
}