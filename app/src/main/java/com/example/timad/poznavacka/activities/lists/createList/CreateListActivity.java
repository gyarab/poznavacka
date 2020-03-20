package com.example.timad.poznavacka.activities.lists.createList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.timad.poznavacka.LockableViewPager;
import com.example.timad.poznavacka.PoznavackaInfo;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.SectionsPageAdapter;
import com.example.timad.poznavacka.Zastupce;
import com.example.timad.poznavacka.activities.AuthenticationActivity;
import com.example.timad.poznavacka.activities.lists.MyListsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

public class CreateListActivity extends AppCompatActivity implements SetTitleFragment.OnFragmentInteractionListener, SetLanguageRepresentativesFragment.OnFragmentInteractionListener, SetCreateOptionsFragment.OnFragmentInteractionListener, GeneratedListFragment.OnFragmentInteractionListener {

    private final String TAG = "CreateListActivity";

    protected SectionsPageAdapter mSectionsPageAdapter;
    public static LockableViewPager mViewPager;

    private String title;
    public static ArrayList<String> representatives;
    public static String languageURL;
    private boolean autoImportIsChecked;
    private ArrayList<Zastupce> mZastupceArr;

    private String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), MyListsActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        //fragments navigation
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);


    }


    private void setupViewPager(LockableViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        //TODO change back
        //adapter.addFragment(new CreateListFragment());
        adapter.addFragment(new SetTitleFragment());
        adapter.addFragment(new SetLanguageRepresentativesFragment());
        adapter.addFragment(new SetCreateOptionsFragment());
        adapter.addFragment(new GeneratedListFragment());

        viewPager.setAdapter(adapter);
        viewPager.setSwipeable(false);
    }

    public void setViewPager(int fragmentNumber) {
        mViewPager.setCurrentItem(fragmentNumber);
    }

    @Override
    public void updateTitle(String input) {
        title = input;
        Timber.d("loading into setRepresentativesFragment");
        SetLanguageRepresentativesFragment setLanguageRepresentativesFragment = new SetLanguageRepresentativesFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_set_title, setLanguageRepresentativesFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public void updateLanguageAndRepresentatives(String languageURL, ArrayList<String> representatives) {
        CreateListActivity.representatives = representatives;
        CreateListActivity.languageURL = languageURL;
        Timber.d("loading into setCreateOptionsFragment");
        SetCreateOptionsFragment setCreateOptionsFragment = SetCreateOptionsFragment.newInstance(languageURL, representatives);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_set_representatives, setCreateOptionsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void updateCreateOptions(boolean autoImportIsChecked, int userParametersCount, ArrayList<String> userScientificClassification, ArrayList<String> reversedUserScientificClassification) {
        this.autoImportIsChecked = autoImportIsChecked;
        GeneratedListFragment generatedListFragment = GeneratedListFragment.newInstance(representatives, autoImportIsChecked, userParametersCount, userScientificClassification, reversedUserScientificClassification, languageURL);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_set_create_options, generatedListFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onSave(ArrayList<Zastupce> mZastupceArr) {
        this.mZastupceArr = mZastupceArr;

        String uuid = UUID.randomUUID().toString();
        path = uuid + "/";
        SaveImagesAsync saveImagesAsync = new SaveImagesAsync();
        saveImagesAsync.execute();

        MyListsActivity.savingNewList = true;
        Intent intent0 = new Intent(getApplicationContext(), MyListsActivity.class);
        intent0.putExtra("AUTOIMPORTISCHECKED", autoImportIsChecked);
        intent0.putExtra("TITLE", title);
        intent0.putExtra("MZASTUPCEARR", mZastupceArr); //TODO THIS
        intent0.putExtra("PATH", path);
        intent0.putExtra("UUID", uuid);
        intent0.putExtra("LANGUAGEURL", languageURL);
        startActivity(intent0);
        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
    }

    private class SaveImagesAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            File dir = new File(getApplicationContext().getFilesDir().getPath() + "/" + path);

            dir.mkdir(); //creates folder

            // Saves images locally
            for (Zastupce z : mZastupceArr) {
                if (z.getImage() != null) {
                    if (!MyListsActivity.getSMC(getApplicationContext()).saveDrawable(z.getImage(), path, z.getParameter(0))) {
                        return null;
                    }
                } else {
                    // TODO exception for first thing
                            /*Toast.makeText(getApplication(), "Failed to save " + title, Toast.LENGTH_SHORT).show(); EDIT
                            deletePoznavacka(dir);
                            return;*/
                }
                z.setImage(null);
            }

            return null;
        }
    }

}


