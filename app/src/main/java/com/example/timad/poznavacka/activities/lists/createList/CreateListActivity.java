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

public class CreateListActivity extends AppCompatActivity implements SetTitleFragment.OnFragmentInteractionListener, SetRepresentativesFragment.OnFragmentInteractionListener, SetCreateOptionsFragment.OnFragmentInteractionListener, GeneratedListFragment.OnFragmentInteractionListener {

    private final String TAG = "CreateListActivity";

    protected SectionsPageAdapter mSectionsPageAdapter;
    public static LockableViewPager mViewPager;

    private String title;
    private String rawRepresentatives;
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
        adapter.addFragment(new SetRepresentativesFragment());
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
        SetRepresentativesFragment setRepresentativesFragment = new SetRepresentativesFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_set_title, setRepresentativesFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public void updateRepresentatives(String input) {
        rawRepresentatives = input;
        Log.d(TAG, "loading into setCreateOptionsFragment");
        Bundle bundle = new Bundle();
        bundle.putString("ARG_RAWREPRESENTATIVES", rawRepresentatives);
        SetCreateOptionsFragment setCreateOptionsFragment = new SetCreateOptionsFragment();
        setCreateOptionsFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_set_representatives, setCreateOptionsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void updateCreateOptions(String languageURL, ArrayList<String> representatives) {
        this.representatives = representatives;
        this.languageURL = languageURL;
        Bundle bundle = new Bundle();
        bundle.putString("ARG_RAWREPRESENTATIVES", rawRepresentatives);
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
