package com.example.timad.poznavacka.activities.lists.createList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.timad.poznavacka.LockableViewPager;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.SectionsPageAdapter;
import com.example.timad.poznavacka.activities.lists.ListsActivity;

import java.util.ArrayList;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

public class CreateListActivity extends AppCompatActivity implements SetTitleFragment.OnFragmentInteractionListener, SetLanguageRepresentativesFragment.OnFragmentInteractionListener, SetCreateOptionsFragment.OnFragmentInteractionListener, GeneratedListFragment.OnFragmentInteractionListener {

    private final String TAG = "CreateListActivity";

    protected SectionsPageAdapter mSectionsPageAdapter;
    public static LockableViewPager mViewPager;

    private String title;
    private ArrayList<String> representatives;
    private String languageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), ListsActivity.class);
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
        adapter.addFragment(new CreateListFragment());
/*        adapter.addFragment(new SetTitleFragment());
        adapter.addFragment(new SetLanguageRepresentativesFragment());
        adapter.addFragment(new SetCreateOptionsFragment());
        adapter.addFragment(new GeneratedListFragment());*/

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
        this.representatives = representatives;
        Log.d(TAG, "loading into setCreateOptionsFragment");
        SetCreateOptionsFragment setCreateOptionsFragment = new SetCreateOptionsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_set_representatives, setCreateOptionsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public void updateCreateOptions(String languageURL, ArrayList<String> representatives) {
        this.languageURL = languageURL;
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("ARG_REPRESENTATIVES", this.representatives);
        bundle.putString("ARG_LANGUAGEURL", this.languageURL);
        GeneratedListFragment generatedListFragment = new GeneratedListFragment();
        generatedListFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_set_create_options, generatedListFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onSave() {

    }
}
