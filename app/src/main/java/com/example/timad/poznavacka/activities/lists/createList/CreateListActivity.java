package com.example.timad.poznavacka.activities.lists.createList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.example.timad.poznavacka.LockableViewPager;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.SectionsPageAdapter;
import com.example.timad.poznavacka.Zastupce;
import com.example.timad.poznavacka.activities.lists.MyListsActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.query.AdInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

public class CreateListActivity extends AppCompatActivity implements SetTitleFragment.OnFragmentInteractionListener, SetLanguageFragment.OnFragmentInteractionListener, SetRepresentativesFragment.OnFragmentInteractionListener, SetCreateOptionsFragment.OnFragmentInteractionListener, GeneratedListFragment.OnFragmentInteractionListener {

    private final String TAG = "CreateListActivity";

    protected SectionsPageAdapter mSectionsPageAdapter;
    public static LockableViewPager mViewPager;

    private String title;
    public static ArrayList<String> representatives;
    public static String languageURL;
    private boolean autoImportIsChecked;
    private ArrayList<Zastupce> mZastupceArr;

    private String path;

    private Fragment currentFragment;

    private static InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        /*if (savedInstanceState != null) {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
        }*/


/*        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
*//*
                Intent intent = new Intent(getApplicationContext(), MyListsActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
*//*

                getFragmentManager().popBackStack();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);*/


        //fragments navigation
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mInterstitialAd = new InterstitialAd(this);
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); TEST
        mInterstitialAd.setAdUnitId("ca-app-pub-2924053854177245/3480271080");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
        //getSupportFragmentManager().putFragment(outState, "myFragmentName", currentFragment);
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            Intent intent = new Intent(getApplicationContext(), MyListsActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
            return;
        } else if (count == 1) {
            getSupportFragmentManager().popBackStack();
            SetTitleFragment.btnNext.setVisibility(View.VISIBLE);
        } else if (count == 2) {
            getSupportFragmentManager().popBackStack();
            SetRepresentativesFragment.btnNext.setVisibility(View.VISIBLE);
        } else if (count == 3) {
            getSupportFragmentManager().popBackStack();
            SetRepresentativesFragment.btnNext.setVisibility(View.VISIBLE);
        } else if (count == 4) {
            GeneratedListFragment.cancelWikiSearchRepresentativesAsync();
            getSupportFragmentManager().popBackStack();
            SetCreateOptionsFragment.btnNext.setVisibility(View.VISIBLE);
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    private void setupViewPager(LockableViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new SetTitleFragment());
        adapter.addFragment(new SetLanguageFragment());
        adapter.addFragment(new SetRepresentativesFragment());
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
        SetLanguageFragment setLanguageFragment = new SetLanguageFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.replace(R.id.fragment_set_title, setLanguageRepresentativesFragment);
        fragmentTransaction.add(R.id.fragment_set_title, setLanguageFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void updateLanguage(String languageURL) {
        CreateListActivity.languageURL = languageURL;
        SetRepresentativesFragment setRepresentativesFragment = new SetRepresentativesFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.replace(R.id.fragment_set_title, setLanguageRepresentativesFragment);
        fragmentTransaction.add(R.id.fragment_set_language, setRepresentativesFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void updateRepresentatives(ArrayList<String> representatives) {
        CreateListActivity.representatives = representatives;
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
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Timber.d("The interstitial wasn't loaded yet.");
        }
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
        intent0.putExtra("MZASTUPCEARR", mZastupceArr);
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


