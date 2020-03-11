package com.example.timad.poznavacka.activities.lists.createList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.timad.poznavacka.LockableViewPager;
import com.example.timad.poznavacka.PoznavackaInfo;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.SectionsPageAdapter;
import com.example.timad.poznavacka.Zastupce;
import com.example.timad.poznavacka.activities.AuthenticationActivity;
import com.example.timad.poznavacka.activities.lists.ListsActivity;
import com.example.timad.poznavacka.activities.lists.MyListsFragment;
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
        saveCreatedList();
    }

    private void saveCreatedList() {

        //changing getContext() to getApllicationContext()
        //changing getApplication() to getApplication()
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Toast.makeText(getApplication(), "Saving " + title + "...", Toast.LENGTH_LONG).show();

        // Store images
        Gson gson = new Gson();
        Context context = getApplicationContext();
        String uuid = UUID.randomUUID().toString();
        String path = uuid + "/";
        File dir = new File(context.getFilesDir().getPath() + "/" + path);

        // Create folder
        try {
            dir.mkdir();
        } catch (Exception e) {
            Toast.makeText(getApplication(), "Failed to save " + title, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        // Saves images locally
        for (Zastupce z : mZastupceArr) {
            if (z.getImage() != null) {
                if (!MyListsFragment.getSMC(context).saveDrawable(z.getImage(), path, z.getParameter(0))) {
                    Toast.makeText(getApplication(), "Failed to save " + title, Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                // TODO exception for first thing

                            /*Toast.makeText(getApplication(), "Failed to save " + title, Toast.LENGTH_SHORT).show(); EDIT
                            deletePoznavacka(dir);
                            return;*/
            }
            z.setImage(null);
        }

        // Saving mZastupceArr
        String json = gson.toJson(mZastupceArr);
        //add to file
        String userName = user.getDisplayName();

        String userID = null;
        if (user != null) {
            userID = user.getUid();
        } else {
            Intent intent0 = new Intent(getApplication(), AuthenticationActivity.class);
            startActivity(intent0);
            finish();
        }

        // Add to database
        //PoznavackaDbObject item = new PoznavackaDbObject(title, uuid, json,userName);
        //SharedListsFragment.addToFireStore("Poznavacka", item);
        //Log.d("Files", json);
        if (!MyListsFragment.getSMC(context).createAndWriteToFile(path, uuid, json)) {
            Toast.makeText(getApplication(), "Failed to save " + title, Toast.LENGTH_SHORT).show();
            return;
        }

        String pathPoznavacka = "poznavacka.txt";
        if (MyListsFragment.sPoznavackaInfoArr == null) {
            MyListsFragment.getSMC(context).readFile(pathPoznavacka, true);
        }
        if (autoImportIsChecked) {
            MyListsFragment.sPoznavackaInfoArr.add(new PoznavackaInfo(title, uuid, userName, userID, mZastupceArr.get(1).getParameter(0), mZastupceArr.get(1).getImageURL(), languageURL, false));
        } else {
            MyListsFragment.sPoznavackaInfoArr.add(new PoznavackaInfo(title, uuid, userName, userID, mZastupceArr.get(0).getParameter(0), mZastupceArr.get(0).getImageURL(), languageURL, false));
        }
        MyListsFragment.getSMC(context).updatePoznavackaFile(pathPoznavacka, MyListsFragment.sPoznavackaInfoArr);

        Log.d("Files", "Saved successfully");
        Toast.makeText(getApplication(), "Successfully saved " + title, Toast.LENGTH_SHORT).show();
        Intent intent0 = new Intent(getApplicationContext(), ListsActivity.class);
        startActivity(intent0);
        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
        finish();

        // Deletes everything base in folder
                /*File[] files = c.getFilesDir().listFiles();
                for (int i = 0; i < files.length; i++)
                {
                    if(files[i].isDirectory()){
                        Log.d("Files", files[i].getPath() + " : " + files[i].getName());
                        File[] files2 = files[i].listFiles();
                        for (int x = 0; x < files2.length; x++) {
                            //files2[x].delete();
                        }
                    }
                    //files[i].delete();
                }
                Log.d("Files", "Deleted "+ files.length + " files");*/

                /*for (int i = 0; i < representatives.size(); i++) {
                    // Uploading poznavacka
                    Map<String, Object> representativeInfo = new HashMap<>();
                    representativeInfo.put(KEY_ZASTUPCE, representatives.get(i));
                    representativeInfo.put(KEY_IMGREF, "imageRef - cislo/hash?");
                    if (autoImportSwitch.isChecked()) {
                        representativeInfo.put(KEY_DRUH, "dohledany druh");
                        representativeInfo.put(KEY_RAD, "dohledany rad");
                    }
                    firestoreImpl.uploadRepresentative(title, representatives.get(i), representativeInfo);
                }*/
    }
}

