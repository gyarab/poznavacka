package com.adamec.timotej.poznavacka.activities.lists;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adamec.timotej.poznavacka.ClassificationData;
import com.adamec.timotej.poznavacka.DBTestObject;
import com.adamec.timotej.poznavacka.PoznavackaInfo;
import com.adamec.timotej.poznavacka.R;
import com.adamec.timotej.poznavacka.RWAdapter;
import com.adamec.timotej.poznavacka.StorageManagerClass;
import com.adamec.timotej.poznavacka.Zastupce;
import com.adamec.timotej.poznavacka.activities.AccountActivity;
import com.adamec.timotej.poznavacka.activities.AuthenticationActivity;
import com.adamec.timotej.poznavacka.activities.lists.createList.CreateListActivity;
import com.adamec.timotej.poznavacka.activities.practice.PracticeActivity;
import com.adamec.timotej.poznavacka.activities.practice.PracticeActivity2;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.internal.NavigationMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import timber.log.Timber;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class MyListsActivity extends AppCompatActivity {

    private static final String TAG = "myListsActivity";

    public static boolean savingNewList;
    private boolean autoImportIsChecked;
    private String title;
    private ArrayList<Object> mZastupceArr;
    private String path;
    private String uuid;
    private String languageURL;

    private boolean savingFromDeeplink;
    public static boolean downloadingCorruptedList;
    public static boolean savingDownloadedList;
    private String userId_saving;
    private String docId_saving;
    private SaveCreatedListAsync currentSaveCreatedListAsync;
    private SaveDownloadedListAsync currentSaveDownloadedListAsync;
    private DrawableFromUrlAsync currentDrawableFromUrlAsync;

    private PoznavackaDbObject item;

    public static StorageManagerClass sSMC;

    public static PoznavackaInfo sActivePoznavacka = null;
    public static ArrayList<Object> sPoznavackaInfoArr;

    private RecyclerView mRecyclerView;
    public static RWAdapter mAdapter;
    private RecyclerView.LayoutManager mLManager;
    public static int sPositionOfActivePoznavackaInfo;

    private static FabSpeedDial newListBTN;
    private ProgressBar newListBTNProgressBar;

    private boolean loadingPractice = false;

    private ExtendedFloatingActionButton examEFAB;

    private RelativeLayout noListsLayout;
    private static TourGuide mTourGuide;

    public static InterstitialAd mNewListInterstitialAd;
    public static UnifiedNativeAd mUnifiedNativeAd;
    private RewardedAd mRewardedAd;
    public static boolean initialized;
    private AdLoader nativeAdLoader;
    public static boolean rewardAdWatched;

    private InterstitialAd mPracticeLoadInterstitialAd;
    private boolean mPracticeLoadInterstitalAdWatched = false;
    private boolean practice2Transition = false;
    private boolean practiceTransition = false;
    private boolean practiceNavPressed = false;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.plant(new Timber.DebugTree());
        loadLanguage();
        setContentView(R.layout.activity_lists);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        newListBTN = findViewById(R.id.fabSpeedDial);
        newListBTNProgressBar = findViewById(R.id.fabSpeedDialProgressBar);
        newListBTNProgressBar.setVisibility(View.INVISIBLE);

        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        if (!prefs.contains("My_Lang")) {
            lanDialog();
        }

        if (savingNewList) {
            showNewListInterstitial();
            if (mNewListInterstitialAd == null || !mNewListInterstitialAd.isLoaded()) {
                Toast.makeText(getApplicationContext(), getString(R.string.wait_save), Toast.LENGTH_LONG).show();
            }

            newListBTNProgressBar.setVisibility(View.VISIBLE);
            newListBTNProgressBar.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));

            Intent newListIntent = getIntent();
            autoImportIsChecked = newListIntent.getBooleanExtra("AUTOIMPORTISCHECKED", false);
            title = newListIntent.getStringExtra("TITLE");
            path = newListIntent.getStringExtra("PATH");
            uuid = newListIntent.getStringExtra("UUID");
            languageURL = newListIntent.getStringExtra("LANGUAGEURL");

            ClassificationData classificationData = newListIntent.getParcelableExtra("CLASSIFICATIONDATA");
            ArrayList<Zastupce> zastupces = newListIntent.getParcelableArrayListExtra("ZASTUPCES");
            mZastupceArr = new ArrayList<>();
            assert classificationData != null;
            if (classificationData.getClassification() != (ArrayList<String>) null) {
                mZastupceArr.add(classificationData);
            }
            assert zastupces != null;
            mZastupceArr.addAll(zastupces);

            currentSaveCreatedListAsync = new SaveCreatedListAsync();
            currentSaveCreatedListAsync.execute();

        } else if (savingDownloadedList) {
            if (mNewListInterstitialAd == null || !mNewListInterstitialAd.isLoaded()) {
                Toast.makeText(getApplicationContext(), getString(R.string.wait_save), Toast.LENGTH_LONG).show();
            }
            saveDownloadedList();

        } else if (sPoznavackaInfoArr == null || sPositionOfActivePoznavackaInfo == -1 || sPoznavackaInfoArr.isEmpty()) {
            if (sPoznavackaInfoArr == null) {
                sPoznavackaInfoArr = new ArrayList<>();
            }
        }

        //checkForDynamicLinks();

        if (!initialized) {
            init(getApplicationContext());
            initialized = true;

            if (sPoznavackaInfoArr == null || sPositionOfActivePoznavackaInfo == -1 || sPoznavackaInfoArr.isEmpty()) {
                initTourGuide();
            }
        }

        /*examEFAB = findViewById(R.id.exams_btn); //TODO return exams
        examEFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(), MyExamsActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                finish();
            }
        });*/

        /**
         * Inicializace Recycler View
         */
        mRecyclerView = findViewById(R.id.recyclerViewL);
        mRecyclerView.setHasFixedSize(true);
        mLManager = new LinearLayoutManager(getApplicationContext());
        mAdapter = new RWAdapter(sPoznavackaInfoArr, getApplicationContext());

        mRecyclerView.setLayoutManager(mLManager);
        mRecyclerView.setAdapter(mAdapter);

        //TODO return
        if ((sPositionOfActivePoznavackaInfo != -1) && (mUnifiedNativeAd == null)) {
            loadNativeAd();
        }

        /**
         * nastavení Event Listenerů
         */
        mAdapter.setOnItemClickListener(new RWAdapter.OnItemClickListener() {
            /**
             * Po kliknutí na položku v Recycler View nastaví ji jako aktivní
             * @param position - pozice položky v poli
             */
            @Override
            public void onItemClick(int position) {
                if (!loadingPractice) {
                    sPositionOfActivePoznavackaInfo = position;
                    sActivePoznavacka = (PoznavackaInfo) sPoznavackaInfoArr.get(sPositionOfActivePoznavackaInfo);
                    mAdapter.notifyDataSetChanged();
                }
            }

            /**
             * Po kliknutí na ikonku přenese uživatele do Practice Activity
             * @param position - pozice položky v poli
             */
            @Override
            public void onPracticeClick(final int position) {
                if (!loadingPractice) {
                    showPracticeLoadInterstitial();
                    loadingPractice = true;
                    sPositionOfActivePoznavackaInfo = position;
                    sActivePoznavacka = (PoznavackaInfo) sPoznavackaInfoArr.get(position);
                    if (sActivePoznavacka == null) {
                        Timber.d("sActivePoznavacka is null, onPracticeClick");
                    } else {
                        Timber.d("sActivePoznavacka is not null, onPracticeClick");
                    }

                    if (sActivePoznavacka != null) {
                        if (PracticeActivity.sLoadedPoznavacka != null && PracticeActivity.sLoadedPoznavacka.getId().equals(MyListsActivity.sActivePoznavacka.getId())) {
                            Timber.d("Practice load, active poznavacka = loaded poznavacka");
                            PracticeActivity.mLoaded = true;
                            updateData();
                        } else {
                            Timber.d("Practice load, active poznavacka != loaded poznavacka");
                            new LoadPoznavacka().execute();
                        }
                    }
                }
            }

            /**
             * Po kliknutí na ikonku sdílet vyskočí dialogové okno, které se zeptá na potvrzení volby. Po potvrzení je Poznávačka sdílena.
             * @param position - pozice položky v poli
             */
            @Override
            public void onShareClick(final int position) {
                if (!loadingPractice) {
                    Timber.d("Share clicked");
                    if (SharedListsActivity.checkInternet(getApplicationContext())) {
                        Timber.d("Share clicked, internet good");
                        sActivePoznavacka = (PoznavackaInfo) sPoznavackaInfoArr.get(position);
                        //createDeepLink(sActivePoznavacka);
                        if (SharedListsActivity.checkInternet(getApplicationContext())) {
                            if (!sActivePoznavacka.isUploaded()) {
                                if (upload()) {
                                    Timber.d("Share clicked, uploaded");
                                    shareIntent(String.valueOf("https://tinyurl.com/memimg-app"));
                                }
                            } else {
                                Timber.d("Share clicked, not uploaded");
                                shareIntent(String.valueOf("https://tinyurl.com/memimg-app"));
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            /**
             * Zeptá se uživatele na potvrzení smazání Poznávačky. Po potvrzení je Poznávačka smazána ze zařízení.
             * @param position - pozice položky v poli
             */
            @Override
            public void onDeleteClick(final int position) {
                if (!loadingPractice) {
                    deleteDialog(position);
                }
            }

            /**
             * Po kliknutí na ikonu testu se zeptá na potvrzení o vytvoření testu. Když uživatel odpoví ano, tak je testen vytvořen.
             * @param position - pozice položky v poli
             */
            @Override
            public void onTestClick(final int position) {
                if (!loadingPractice) {
                    sActivePoznavacka = (PoznavackaInfo) sPoznavackaInfoArr.get(position);
                    if (SharedListsActivity.checkInternet(getApplicationContext())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MyListsActivity.this);
                        builder.setTitle(R.string.add_to_exams);
                        builder.setIcon(R.drawable.ic_school_black_24dp);
                        builder.setMessage("Do you want to add " + sActivePoznavacka.getName() + " into exams?");
                        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String classification = getSMC(getApplicationContext()).readFile(sActivePoznavacka.getId() + "/" + sActivePoznavacka.getId() + "classification.txt", false);
                                String content = getSMC(getApplicationContext()).readFile(sActivePoznavacka.getId() + "/" + sActivePoznavacka.getId() + ".txt", false);
                                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                String name = sActivePoznavacka.getName();
                                boolean started = false;
                                String previewImgUrl = sActivePoznavacka.getPrewievImageUrl();
                                boolean finished = false;
                                boolean resultsEmpty = false;
                                DBTestObject data = new DBTestObject(name, classification, content, userID, previewImgUrl, started, finished, "", "", resultsEmpty);
                                MyExamsActivity.addToTests(userID, data);
                                //    MyTestActivity.addToTests(FirebaseAuth.getInstance().getCurrentUser().getUid(),data);

                                dialog.dismiss();
                            }
                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Connect to internet!", Toast.LENGTH_SHORT).show();
                    }
                }
            }


        });

        newListBTN.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                if (!SharedListsActivity.checkInternet(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "Not connected to internet", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (savingDownloadedList || savingNewList) {
                    return false;
                }
                if (mTourGuide != null) {
                    mTourGuide.cleanUp();
                }
                return super.onPrepareMenu(navigationMenu);
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case (R.id.action_download):
                        Intent intent0 = new Intent(getApplicationContext(), SharedListsActivity.class);
                        startActivity(intent0);
                        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                        loadNewListInterstitial();
                        break;
                    case (R.id.action_create):
                        if (mRewardedAd.isLoaded()) {
                            RewardedAdCallback adCallback = new RewardedAdCallback() {
                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                    rewardAdWatched = true;
                                    mRewardedAd = createAndLoadRewardedAd();
                                }

                                @Override
                                public void onRewardedAdClosed() {
                                    super.onRewardedAdClosed();
                                    mRewardedAd = createAndLoadRewardedAd();
                                    if (rewardAdWatched) {
                                        Intent intent1 = new Intent(getApplicationContext(), CreateListActivity.class);
                                        startActivity(intent1);
                                        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                                        finish();
                                    }
                                }

                                @Override
                                public void onRewardedAdFailedToShow(AdError adError) {
                                    super.onRewardedAdFailedToShow(adError);
                                    mRewardedAd = createAndLoadRewardedAd();
                                    Intent intent1 = new Intent(getApplicationContext(), CreateListActivity.class);
                                    startActivity(intent1);
                                    overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                                    finish();
                                }
                            };
                            mRewardedAd.show(MyListsActivity.this, adCallback);
                        } else {
                            mRewardedAd = createAndLoadRewardedAd();
                            rewardAdWatched = true;
                            Intent intent1 = new Intent(getApplicationContext(), CreateListActivity.class);
                            startActivity(intent1);
                            overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                            finish();
                            Timber.d("The rewarded ad wasn't loaded yet.");
                        }
                        rewardAdWatched = false;
                        break;
                }
                return true;
            }
        });

        if (mPracticeLoadInterstitialAd == null || !mPracticeLoadInterstitialAd.isLoaded()) {
            loadPracticeLoadInterstitial();
        }

        //navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        //BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_practice:
                        if (savingDownloadedList || savingNewList) {
                            break;
                        }
                        practiceNavPressed = true;
                        showPracticeLoadInterstitial();
                        if (mPracticeLoadInterstitalAdWatched) {
                            Intent intent0 = new Intent(MyListsActivity.this, PracticeActivity.class);
                            startActivity(intent0);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                        break;

                    case R.id.nav_lists:
                        /*Intent intent1 = new Intent(ListsActivity.this, ListsActivity.class);
                startActivity(intent1);*/
                        break;

                    /*case R.id.nav_test:
                        Intent intent3 = new Intent(MyListsActivity.this, TestActivity.class);
                        startActivity(intent3);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;*/

                    case R.id.nav_account:
                        if (savingDownloadedList || savingNewList) {
                            break;
                        }
                        Intent intent4 = new Intent(MyListsActivity.this, AccountActivity.class);
                        startActivity(intent4);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;

                }


                return false;
            }
        });
    }

    public RewardedAd createAndLoadRewardedAd() {
        RewardedAd rewardedAd = new RewardedAd(getApplicationContext(),
                "ca-app-pub-2924053854177245/2892047910");
        //ca-app-pub-3940256099942544/5224354917 Test add

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return rewardedAd;
    }

    private void loadNewListInterstitial() {
        mNewListInterstitialAd = new InterstitialAd(getApplicationContext());
        mNewListInterstitialAd.setAdUnitId("ca-app-pub-2924053854177245/3480271080");
        //mNewListInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //Test add
        mNewListInterstitialAd.loadAd(new AdRequest.Builder().build());
        Timber.d("NewListInterstitial shared lists load ad");

        mNewListInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mNewListInterstitialAd.loadAd(new AdRequest.Builder().build());
                Toast.makeText(getApplicationContext(), getString(R.string.wait_save), Toast.LENGTH_LONG).show();
                Timber.d("NewListInterstitial shared lists ad closed");
            }
        });
    }

    private void loadPracticeLoadInterstitial() {
        mPracticeLoadInterstitialAd = new InterstitialAd(getApplicationContext());
        mPracticeLoadInterstitialAd.setAdUnitId("ca-app-pub-2924053854177245/7485518486");
        //mPracticeLoadInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //Test add
        mPracticeLoadInterstitialAd.loadAd(new AdRequest.Builder().build());
        Timber.d("PracticeLoadInterstitial shared lists load ad");

        mPracticeLoadInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mPracticeLoadInterstitialAd.loadAd(new AdRequest.Builder().build());
                Timber.d("PracticeLoadInterstitial shared lists ad closed");
                mPracticeLoadInterstitalAdWatched = true;
                if (practiceTransition) {
                    practiceTransition();
                } else if (practice2Transition) {
                    practice2Transition(PracticeActivity.ALL);
                } else if (practiceNavPressed) {
                    Intent intent0 = new Intent(MyListsActivity.this, PracticeActivity.class);
                    startActivity(intent0);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mPracticeLoadInterstitalAdWatched = true;
            }
        });
    }

    private void updateData() {
        if (sActivePoznavacka == null) {
            Timber.d("sActivePoznavacka is null, updateData()");
        } else {
            Timber.d("sActivePoznavacka is not null, updateData()");
        }
        if (getSMC(getApplicationContext()).readFile(sActivePoznavacka.getId() + "/" + sActivePoznavacka.getId() + ".txt", false) != null
                && !getSMC(getApplicationContext()).readFile(sActivePoznavacka.getId() + "/" + sActivePoznavacka.getId() + ".txt", false).equals("")
                && !getSMC(getApplicationContext()).readFile(sActivePoznavacka.getId() + "/" + sActivePoznavacka.getId() + ".txt", false).isEmpty()) {
            Timber.d("Practice load, updateData()");
            if (!PracticeActivity.mLoaded) {
                Timber.d("Practice load, updateData(), notLoaded");
                practiceTransition = true;
                if (mPracticeLoadInterstitalAdWatched) {
                    practiceTransition();
                }
            } else {
                Timber.d("Practice load, updateData(), loaded");
                int count = PracticeActivity.sZastupceArrOrig.size();
                if (PracticeActivity.sZastupceArrOrig.get(0) instanceof ClassificationData) {
                    count -= 1;
                }
                Timber.d("Practice load, updateData(), count = %s", count);
                Timber.d("Practice load, updateData(), sNenuceniZastupci.size() = %s", PracticeActivity.sNenauceniZastupci.size());
                if (count == PracticeActivity.sNenauceniZastupci.size()) {
                    practice2Transition = true;
                    if (mPracticeLoadInterstitalAdWatched) {
                        practice2Transition(PracticeActivity.ALL);
                    }
                } else if (PracticeActivity.sNenauceniZastupci.size() < 1) {
                    practice2Transition = true;
                    if (mPracticeLoadInterstitalAdWatched) {
                        practice2Transition(PracticeActivity.ALL);
                    }
                } else {
                    practiceTransition = true;
                    if (mPracticeLoadInterstitalAdWatched) {
                        practiceTransition();
                    }
                }
            }
        }
    }

    private void practiceTransition() {
        Timber.d("Practice load, practiceTransition()");
        Intent myIntent = new Intent(getApplicationContext(), PracticeActivity.class);
        startActivity(myIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void practice2Transition(int type) {
        Timber.d("Practice load, practice2Transition()");
        Intent intent = new Intent(this, PracticeActivity2.class);
        Bundle b = new Bundle();
        b.putInt("key", type);
        intent.putExtras(b);
        startActivity(intent);
        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
        finish();
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
        PracticeActivity.sZastupceArrOrig.clear();

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
                PracticeActivity.sZastupceArrOrig.add(classificationData);
        }
        if (zastupces == null) {
            Timber.d("List is corrupted");
            Thread thread = new Thread() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Timber.d("List is corrupted, ui thread run");
                            getSMC(getApplicationContext()).deletePoznavacka(path);
                            getSMC(getApplicationContext()).updatePoznavackaFile("poznavacka.txt", sPoznavackaInfoArr);
                            if (SharedListsActivity.checkInternet(getApplicationContext())) {
                                Toast.makeText(getApplicationContext(), R.string.corrupted_file_downloading_new, Toast.LENGTH_LONG).show();
                                if (mNewListInterstitialAd != null && !mNewListInterstitialAd.isLoaded()) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.wait_save), Toast.LENGTH_LONG).show();
                                }
                                userId_saving = sActivePoznavacka.getAuthorsID();
                                Timber.d("List is corrupted, userID_saving = %s", userId_saving);
                                docId_saving = sActivePoznavacka.getId();
                                Timber.d("List is corrupted, docID_saving = %s", docId_saving);
                                downloadingCorruptedList = true;
                                saveDownloadedList();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.corrupted_file_please_download_new, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            };
            thread.start();
            return;
        } else {
            PracticeActivity.sZastupceArrOrig.addAll(zastupces);
        }

        Type intArrType = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        String jsonInt = MyListsActivity.getSMC(context).readFile(path + "nenauceni.txt", true);
        try {
            if (jsonInt == null || jsonInt.equals("") || jsonInt.isEmpty()) {
                PracticeActivity.sNenauceniZastupci = fillArr();
                Timber.d("sNenauceniZastupci - fillArr()");
            } else {
                PracticeActivity.sNenauceniZastupci = gson.fromJson(jsonInt, intArrType);
                Timber.d("sNenauceniZastupci - fromJson()");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Object z : PracticeActivity.sZastupceArrOrig) {
            if (z instanceof Zastupce) {
                ((Zastupce) z).setImage(MyListsActivity.getSMC(context).readDrawable(path, ((Zastupce) z).getParameter(0), context));
            }
        }

        Timber.d("This should be called once");
        PracticeActivity.sLoadedPoznavacka = MyListsActivity.sActivePoznavacka;
        PracticeActivity.mLoaded = true;
    }

    public ArrayList<Integer> fillArr() {
        ArrayList<Integer> arr = new ArrayList<Integer>();

        if (PracticeActivity.sZastupceArrOrig.get(0) instanceof ClassificationData) {
            for (int i = 1; i < PracticeActivity.sZastupceArrOrig.size(); i++) {
                arr.add(i);
            }
        } else {
            for (int i = 0; i < PracticeActivity.sZastupceArrOrig.size(); i++) {
                arr.add(i);
            }
        }

        return arr;
    }

    private void deleteDialog(int position) {
        sActivePoznavacka = (PoznavackaInfo) sPoznavackaInfoArr.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(MyListsActivity.this);
        builder.setTitle(R.string.delete);
        builder.setIcon(R.drawable.ic_delete);
        builder.setMessage(getString(R.string.do_you_want_to_delete) + sActivePoznavacka.getName() + "?");
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePoznavacka(position);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        Button btnPositive = alert.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = alert.getButton(AlertDialog.BUTTON_NEGATIVE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        layoutParams.weight = 20;
        btnPositive.setLayoutParams(layoutParams);
        btnNegative.setLayoutParams(layoutParams);
    }

    private void deletePoznavacka(int position) {
        Context context = getApplicationContext();
        getSMC(context).deletePoznavacka(sActivePoznavacka.getId() + "/");

        sPoznavackaInfoArr.remove(position);
        getSMC(context).updatePoznavackaFile("poznavacka.txt", sPoznavackaInfoArr);

        if (position <= sPositionOfActivePoznavackaInfo) {
            sPositionOfActivePoznavackaInfo -= 1;
            if (sPoznavackaInfoArr.size() > 0) {
                if (sPositionOfActivePoznavackaInfo < 0) {
                    sPositionOfActivePoznavackaInfo = 0;
                }
                try {
                    sActivePoznavacka = (PoznavackaInfo) sPoznavackaInfoArr.get(sPositionOfActivePoznavackaInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sActivePoznavacka = null;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void saveDownloadedList() {
        Timber.d("saveDownloadedList()");
        showNewListInterstitial();
        savingDownloadedList = true;
        newListBTNProgressBar.setVisibility(View.VISIBLE);
        newListBTNProgressBar.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));

        if (!savingFromDeeplink && !downloadingCorruptedList) {
            Intent newDownloadedListIntent = getIntent();
            //title = newDownloadedListIntent.getStringExtra("TITLE");
            userId_saving = newDownloadedListIntent.getStringExtra("USERID");
            docId_saving = newDownloadedListIntent.getStringExtra("DOCID");
        }

        currentSaveDownloadedListAsync = new SaveDownloadedListAsync();
        currentSaveDownloadedListAsync.execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Test add ca-app-pub-3940256099942544/5224354917
        mRewardedAd = createAndLoadRewardedAd();
        /*RewardedAdLoadCallback rewardedAdLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                super.onRewardedAdLoaded();
            }
        };
        mRewardedAd.loadAd(new AdRequest.Builder().build(), rewardedAdLoadCallback);*/

        //checkForDynamicLinks();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (savingDownloadedList) {
            Timber.d("onPause savingDownloadedList=true");
            if (currentSaveCreatedListAsync != null) {
                currentSaveDownloadedListAsync.cancel(true);
                currentDrawableFromUrlAsync.cancel(true);
            }
        }

        if (savingNewList) {
            if (currentSaveCreatedListAsync != null) {
                currentSaveCreatedListAsync.cancel(true);
            }
        }
    }

    private void checkForDynamicLinks() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
            @Override
            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                Timber.i("We have a dynamic link.");

                Uri deepLink = null;
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.getLink();
                }

                if (deepLink != null) {
                    Timber.i("Here's the deep link URL:\n%s", deepLink.toString());

                    savingDownloadedList = true;
                    savingFromDeeplink = true;
                    userId_saving = deepLink.getQueryParameter("userId");
                    Timber.d("Deep link userId = %s", userId_saving);
                    docId_saving = deepLink.getQueryParameter("docId");
                    Timber.d("Deep link docId = %s", docId_saving);
                    saveDownloadedList();
                }
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.e("Error retrieving dynamic link data.");
                    }
                });
    }

    private void createDeepLink(PoznavackaInfo poznavackaInfo) {
        Timber.d("Share clicked, create deep link");
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://play.google.com/store/apps/details?id=com.adamec.timotej.poznavacka"))
                //.setLink(Uri.parse("https://tinyurl.com/memimg-app"))
                //.setLink(Uri.parse("https://play.google.com/store/apps/details?id=com.adamec.timotej.poznavacka/?userId=" + poznavackaInfo.getAuthorsID() + "&docId=" + poznavackaInfo.getId()))
                .setDomainUriPrefix("https://memimgapp.page.link")
                //.setAndroidParameters(new DynamicLink.AndroidParameters.Builder().setMinimumVersion(10).build())
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setGoogleAnalyticsParameters(
                        new DynamicLink.GoogleAnalyticsParameters.Builder()
                                .setSource("shareSource")
                                .setMedium("shareMedium")
                                .setCampaign("shareCampaign")
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(getString(R.string.poznavacka) + " - " + poznavackaInfo.getName())
                                .setDescription(getString(R.string.learn_for_exam))
                                .build())
                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Timber.d("Share clicked, deep link successful");
                            Timber.d("short link = %s", shortLink);
                            Timber.d("flow chart link = %s", flowchartLink);

                            if (SharedListsActivity.checkInternet(getApplicationContext())) {
                                if (!poznavackaInfo.isUploaded()) {
                                    if (upload()) {
                                        Timber.d("Share clicked, uploaded");
                                        shareIntent(String.valueOf(shortLink));
                                    }
                                } else {
                                    Timber.d("Share clicked, not uploaded");
                                    shareIntent(String.valueOf(shortLink));
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Timber.e("Share clicked, deep link unsuccessful");
                            // Error
                            // ...
                        }
                    }
                });
    }

    /**
     * Inicializace Recycler View s polem Poznávaček. Poznávačky jsou načteny ze souborů na zařízení.
     *
     * @param context
     */
    public static void init(Context context) {
        //initialization
        Timber.d("init poznavacka info array from device");

        //if (sPoznavackaInfoArr != null) {
        Timber.d("init poznavacka info array is null");
        Gson gson = new Gson();
        String s = getSMC(context).readFile("poznavacka.txt", true);

        if (s != null) {
            if (!s.isEmpty()) {
                Type cType = new TypeToken<ArrayList<PoznavackaInfo>>() {
                }.getType();
                sPoznavackaInfoArr = gson.fromJson(s, cType);
                sActivePoznavacka = (PoznavackaInfo) sPoznavackaInfoArr.get(0);
                sPositionOfActivePoznavackaInfo = 0;
            } else {
                sPoznavackaInfoArr = new ArrayList<>();
                sPositionOfActivePoznavackaInfo = -1;
            }
        } else {
            Timber.d("init poznavacka info array from device");
            sPoznavackaInfoArr = new ArrayList<>();
            sPositionOfActivePoznavackaInfo = -1;
        }
        //}
    }

    private void loadLanguage() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "en");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    //TODO return

    private void loadNativeAd() {

        //nativeAdLoader = new AdLoader.Builder(getApplicationContext(), "ca-app-pub-3940256099942544/2247696110") TEST
        nativeAdLoader = new AdLoader.Builder(getApplicationContext(), "ca-app-pub-2924053854177245/6546317855")
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // Show the ad.
                        mUnifiedNativeAd = unifiedNativeAd;
                        sPoznavackaInfoArr.add(mUnifiedNativeAd);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, and so on.
                        MobileAds.initialize(getApplicationContext());
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
        nativeAdLoader.loadAd(new AdRequest.Builder().build());
    }

    private void showNewListInterstitial() {
        if (mNewListInterstitialAd != null && mNewListInterstitialAd.isLoaded()) {
            mNewListInterstitialAd.show();
        } else {
            Timber.d("The interstitial wasn't loaded yet.");
        }
    }

    private void showPracticeLoadInterstitial() {
        if (mPracticeLoadInterstitialAd.isLoaded()) {
            mPracticeLoadInterstitialAd.show();
        } else {
            mPracticeLoadInterstitalAdWatched = true;
            Timber.d("The interstitial wasn't loaded yet.");
        }
    }

    private void initTourGuide() {
        mTourGuide = TourGuide.init(this).with(TourGuide.Technique.CLICK)
                .setPointer(new Pointer())
                .setToolTip(new ToolTip().setTitle(getString(R.string.oh_its_empty_in_here))
                        .setDescription(getString(R.string.click_to_add_new_list))
                        .setGravity(Gravity.TOP))
                .playOn(newListBTN);
        mTourGuide.setOverlay(new Overlay());
        mTourGuide.getToolTip().setEnterAnimation(new TranslateAnimation(0, 0, 200f, 0));
        mTourGuide.getToolTip().getMEnterAnimation().setFillAfter(true);
        mTourGuide.getToolTip().getMEnterAnimation().setInterpolator(new BounceInterpolator());
        mTourGuide.getToolTip().getMEnterAnimation().setDuration(1000);
        //mTourGuide.getMPointer().setColor(R.color.colorAccentSecond);
    }

    private void removeFromDatabase(String docID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final DocumentReference docRef = db.collection("Users").document(user.getUid()).collection("Poznavacky").document(docID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                docRef
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), R.string.failed_to_remove_from_database, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    public static StorageManagerClass getSMC(Context context) {
        if (sSMC == null) {
            sSMC = new StorageManagerClass(context.getFilesDir().getPath());
        }

        return sSMC;
    }


    private class SaveCreatedListAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //TODO return
            if (sPoznavackaInfoArr.contains(mUnifiedNativeAd)) {
                sPoznavackaInfoArr.remove(mUnifiedNativeAd);
                mAdapter.notifyItemRemoved(sPoznavackaInfoArr.size());
            }

            String pathPoznavacka = "poznavacka.txt";
            MyListsActivity.getSMC(getApplicationContext()).updatePoznavackaFile(pathPoznavacka, MyListsActivity.sPoznavackaInfoArr);

            Log.d("Files", "Saved successfully");

            savingNewList = false;
            newListBTNProgressBar.setVisibility(View.GONE);
            newListBTNProgressBar.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));

            //TODO return
            if (mUnifiedNativeAd != null) {
                sPoznavackaInfoArr.add(mUnifiedNativeAd);
                mAdapter.notifyItemInserted(sPoznavackaInfoArr.size());
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //changing getContext() to getApllicationContext()
            //changing getApplicationContext() to getApplicationContext()
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            // Store images
            Gson gson = new Gson();
            Context context = getApplicationContext();

            //add to file
            String userName = user.getDisplayName();

            String userID = null;
            if (user != null) {
                userID = user.getUid();
            } else {
                Intent intent0 = new Intent(getApplicationContext(), AuthenticationActivity.class);
                startActivity(intent0);
                finish();
            }

            //check for classification
            if (mZastupceArr.get(0) instanceof ClassificationData) {
                //no idea why there are more classificationData items
                int index = 1;
                while (true) {
                    if (mZastupceArr.get(index) instanceof ClassificationData) {
                        mZastupceArr.remove(index);
                    } else {
                        break;
                    }
                }

                //saving classification
                String jsonClassification = gson.toJson((ClassificationData) mZastupceArr.get(0));
                if (!MyListsActivity.getSMC(context).createAndWriteToFile(path, uuid + "classification", jsonClassification)) {
                    Toast.makeText(getApplicationContext(), "Failed to save " + title, Toast.LENGTH_SHORT).show();
                    return null;
                }

                //saving zastupces
                ArrayList<Zastupce> zastupceArr = new ArrayList<>();
                for (int i = 1; i < mZastupceArr.size(); i++) {
                    zastupceArr.add((Zastupce) mZastupceArr.get(i));
                }
                String jsonZastupces = gson.toJson(zastupceArr);
                // Add to database
                //PoznavackaDbObject item = new PoznavackaDbObject(title, uuid, json,userName);
                //SharedListsFragment.addToFireStore("Poznavacka", item);
                //Log.d("Files", json);
                if (!MyListsActivity.getSMC(context).createAndWriteToFile(path, uuid, jsonZastupces)) {
                    Toast.makeText(getApplicationContext(), "Failed to save " + title, Toast.LENGTH_SHORT).show();
                    return null;
                }

                MyListsActivity.sPoznavackaInfoArr.add(new PoznavackaInfo(title, uuid, userName, userID, ((Zastupce) mZastupceArr.get(1)).getParameter(0), ((Zastupce) mZastupceArr.get(1)).getImageURL(), languageURL, false));
            } else {
                // Saving mZastupceArr
                ArrayList<Zastupce> zastupceArr = new ArrayList<>();
                for (Object zastupce :
                        mZastupceArr) {
                    zastupceArr.add((Zastupce) zastupce);
                }

                String json = gson.toJson(zastupceArr);
                if (!MyListsActivity.getSMC(context).createAndWriteToFile(path, uuid, json)) {
                    Toast.makeText(getApplicationContext(), "Failed to save " + title, Toast.LENGTH_SHORT).show();
                    return null;
                }

                MyListsActivity.sPoznavackaInfoArr.add(new PoznavackaInfo(title, uuid, userName, userID, ((Zastupce) mZastupceArr.get(0)).getParameter(0), ((Zastupce) mZastupceArr.get(0)).getImageURL(), languageURL, false));
            }

            String pathPoznavacka = "poznavacka.txt";
            if (MyListsActivity.sPoznavackaInfoArr == null) {
                MyListsActivity.getSMC(context).readFile(pathPoznavacka, true);
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            try {
                getSMC(getApplicationContext()).deletePoznavacka(path);
                getSMC(getApplicationContext()).updatePoznavackaFile("poznavacka.txt", sPoznavackaInfoArr);
                if ((sPoznavackaInfoArr.get(sPoznavackaInfoArr.size() - 1) instanceof PoznavackaInfo)
                        && ((PoznavackaInfo) sPoznavackaInfoArr.get(sPoznavackaInfoArr.size() - 1)).getId().equals(uuid)) {
                    sPoznavackaInfoArr.remove(sPoznavackaInfoArr.size() - 1);
                }
            } catch (Exception e) {
                Timber.e(e);
            }
            super.onCancelled();
        }
    }

    private class SaveDownloadedListAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            if (downloadingCorruptedList) {
                loadingPractice = false;
                sPoznavackaInfoArr.remove(sActivePoznavacka);
                mAdapter.notifyDataSetChanged();
                downloadingCorruptedList = false;
            }
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.d("SaveDownloadedListAsync doInBackground...");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            try {
                DocumentReference docRef = db.collection("Users").document(userId_saving).collection("Poznavacky").document(docId_saving);
                Timber.d("List is corrupted, probably not, SaveDownloadedListAsync, userID_saving = %s", userId_saving);
                Timber.d("List is corrupted, probably not, SaveDownloadedListAsync, docID_saving = %s", docId_saving);
                docRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                item = documentSnapshot.toObject(PoznavackaDbObject.class);
                                if (item == null) {
                                    Timber.d("Item documentSnapshot docID=%s", documentSnapshot.getId());
                                    Timber.d("Item documentSnapshot id=%s", documentSnapshot.getString("id"));
                                    Timber.d("Item documentSnapshot name=%s", documentSnapshot.getString("name"));
                                    Timber.d("Item " + documentSnapshot.getString("name") + " is null");
                                }

                                // Store images
                                Context context = getApplicationContext();
                                path = item.getId() + "/";
                                File dir = new File(context.getFilesDir().getPath() + "/" + path);

                                // Create folder
                                try {
                                    dir.mkdir();
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Failed to save " + item.getName(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                    return;
                                }

                                if (item.getClassification() != null) {
                                    MyListsActivity.getSMC(getApplicationContext()).createAndWriteToFile(path, item.getId() + "classification", item.getClassification());
                                }

                                if (!MyListsActivity.getSMC(getApplicationContext()).createAndWriteToFile(path, item.getId(), item.getContent())) {
                                    Toast.makeText(getApplicationContext(), "Failed to save " + item.getName(), Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    //images download
                                    currentDrawableFromUrlAsync = new DrawableFromUrlAsync();
                                    if (!isCancelled()) {
                                        currentDrawableFromUrlAsync.execute();  //viz Async metoda dole
                                    }
                                }

                                String pathPoznavacka = "poznavacka.txt";
                                if (MyListsActivity.sPoznavackaInfoArr == null) {
                                    MyListsActivity.getSMC(getApplicationContext()).readFile(pathPoznavacka, true);
                                }
                                MyListsActivity.sPoznavackaInfoArr.add(new PoznavackaInfo(item.getName(), item.getId(), item.getAuthorsName(), item.getAuthorsID(), item.getHeadImagePath(), item.getHeadImageUrl(), item.getLanguageURL(), true));
                                //MyListsActivity.getSMC(getApplicationContext()).updatePoznavackaFile(pathPoznavacka, MyListsActivity.sPoznavackaInfoArr);

                                Log.d("Files", "Saved successfully");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.d("Firebase document connection failure");
                        Thread thread = new Thread() {
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(MyListsActivity.this, getString(R.string.unable_to_download_maybe_list_is_not_available), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        };
                        e.printStackTrace();
                        thread.start();
                    }
                });
            } catch (Exception e) {
                Thread thread = new Thread() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MyListsActivity.this, getString(R.string.error_try_again), Toast.LENGTH_LONG).show();
                                newListBTNProgressBar.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                                newListBTNProgressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                };
                downloadingCorruptedList = false;
                savingDownloadedList = false;
                savingFromDeeplink = false;
                thread.start();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            Timber.d("onCancelled() - SaveDownloadedListAsync");
            try {
                getSMC(getApplicationContext()).deletePoznavacka(path);
                getSMC(getApplicationContext()).updatePoznavackaFile("poznavacka.txt", sPoznavackaInfoArr);
                savingDownloadedList = false;
                if (item != null
                        && (sPoznavackaInfoArr.get(sPoznavackaInfoArr.size() - 1) instanceof PoznavackaInfo)
                        && ((PoznavackaInfo) sPoznavackaInfoArr.get(sPoznavackaInfoArr.size() - 1)).getId().equals(item.getId())) {
                    Timber.d("OnCancelled(): " + ((PoznavackaInfo) sPoznavackaInfoArr.get(sPoznavackaInfoArr.size() - 1)).getName() + " removed");
                    sPoznavackaInfoArr.remove(sPoznavackaInfoArr.size() - 1);
                } else {
                    Timber.d("OnCancelled(): nothing removed");
                }
            } catch (Exception e) {
                Timber.e(e);
            }
            super.onCancelled();
        }
    }

    private class DrawableFromUrlAsync extends AsyncTask<Void, Void, Drawable> {

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            //TODO return
            if (sPoznavackaInfoArr.contains(mUnifiedNativeAd)) {
                sPoznavackaInfoArr.remove(mUnifiedNativeAd);
                mAdapter.notifyItemRemoved(sPoznavackaInfoArr.size());
            }

            String pathPoznavacka = "poznavacka.txt";
            MyListsActivity.getSMC(getApplicationContext()).updatePoznavackaFile(pathPoznavacka, MyListsActivity.sPoznavackaInfoArr);

            Log.d("Files", "Saved successfully");

            savingDownloadedList = false;
            savingFromDeeplink = false;
            newListBTNProgressBar.setVisibility(View.GONE);
            newListBTNProgressBar.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
            //TODO return
            if (mUnifiedNativeAd != null) {
                sPoznavackaInfoArr.add(mUnifiedNativeAd);
                mAdapter.notifyItemInserted(sPoznavackaInfoArr.size());
            }
        }

        @Override
        protected Drawable doInBackground(Void... voids) {
            String path = item.getId() + "/";
            Gson gson = new Gson();
            Type cType = new TypeToken<ArrayList<Zastupce>>() {
            }
                    .getType();
            ArrayList<Zastupce> zastupceArr = gson.fromJson(item.getContent(), cType);
            for (Zastupce z : zastupceArr) {
                if (isCancelled()) {
                    break;
                }
                Drawable returnDrawable = null;
                if (!(z.getImageURL() == null || z.getImageURL().isEmpty())) {
                    try {
                        returnDrawable = drawable_from_url(z.getImageURL());
                    } catch (IOException e) {
                        Log.e("Obrazek", "Obrazek nestahnut");
                        e.printStackTrace();
                    }
                    if (!MyListsActivity.getSMC(getApplicationContext()).saveDrawable(returnDrawable, path, z.getParameter(0))) {
                        Thread thread = new Thread() {
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(MyListsActivity.this, getString(R.string.error_try_again), Toast.LENGTH_LONG).show();
                                        newListBTNProgressBar.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                                        newListBTNProgressBar.setVisibility(View.GONE);
                                        if ((sPoznavackaInfoArr.get(sPoznavackaInfoArr.size() - 1) instanceof PoznavackaInfo)
                                                && ((PoznavackaInfo) sPoznavackaInfoArr.get(sPoznavackaInfoArr.size() - 1)).getId().equals(item.getId())) {
                                            Timber.d("Image download error, " + ((PoznavackaInfo) sPoznavackaInfoArr.get(sPoznavackaInfoArr.size() - 1)).getName() + " removed");
                                            sPoznavackaInfoArr.remove(sPoznavackaInfoArr.size() - 1);
                                        }
                                    }
                                });
                            }
                        };
                        savingDownloadedList = false;
                        savingFromDeeplink = false;
                        thread.start();
                        return null;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            try {
                getSMC(getApplicationContext()).deletePoznavacka(path);
                getSMC(getApplicationContext()).updatePoznavackaFile("poznavacka.txt", sPoznavackaInfoArr);
                savingDownloadedList = false;
                if ((sPoznavackaInfoArr.get(sPoznavackaInfoArr.size() - 1) instanceof PoznavackaInfo)
                        && ((PoznavackaInfo) sPoznavackaInfoArr.get(sPoznavackaInfoArr.size() - 1)).getId().equals(item.getId())) {
                    Timber.d("OnCancelled(): " + ((PoznavackaInfo) sPoznavackaInfoArr.get(sPoznavackaInfoArr.size() - 1)).getName() + " removed");
                    sPoznavackaInfoArr.remove(sPoznavackaInfoArr.size() - 1);
                } else {
                    Timber.d("OnCancelled(): nothing removed");
                }
            } catch (Exception e) {
                Timber.e(e);
            }
            Timber.d("onCancelled() - DrawableFromUrlAsync, end");
            super.onCancelled();
        }

        public Drawable drawable_from_url(String url) throws java.io.IOException {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-agent", "Mozilla");

            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            Log.d("Obrazek", "Obrazek stahnut");
            return new BitmapDrawable(Objects.requireNonNull(getApplicationContext()).getResources(), bitmap);
        }
    }

    public boolean upload() {
        Timber.d("Share clicked, upload");
        if (SharedListsActivity.checkInternet(getApplicationContext())) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            //remote upload
            String content = getSMC(getApplicationContext()).readFile(sActivePoznavacka.getId() + "/" + sActivePoznavacka.getId() + ".txt", false);
            String classification = getSMC(getApplicationContext()).readFile(sActivePoznavacka.getId() + "/" + sActivePoznavacka.getId() + "classification.txt", false);
            if (!(user.getUid() == null)) {
                Timber.d("Adding to firestore");
                SharedListsActivity.addToFireStore(user.getUid(), new PoznavackaDbObject(sActivePoznavacka.getName(), sActivePoznavacka.getId(), classification, content, sActivePoznavacka.getAuthor(), sActivePoznavacka.getAuthorsID(), sActivePoznavacka.getPrewievImageUrl(), sActivePoznavacka.getPrewievImageLocation(), sActivePoznavacka.getLanguageURL(), System.currentTimeMillis()));
            } else {
                Intent intent0 = new Intent(getApplicationContext(), AuthenticationActivity.class);
                startActivity(intent0);
                finish();
                return false;
            }

            //local change
            sActivePoznavacka.setUploaded(true);
            getSMC(getApplicationContext()).updatePoznavackaFile("poznavacka.txt", sPoznavackaInfoArr);

            return true;

        } else {
            Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void shareIntent(String link) {
        String message = getString(R.string.i_created_list) + "\n\n" + link + "\n\n" + getString(R.string.after_search_for) + " " + sActivePoznavacka.getName();
        //String message = getString(R.string.i_created_list) + "\n\n" + link;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");

        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MyBroadcastReceiver.class),
                FLAG_UPDATE_CURRENT);

        Intent chooser = Intent.createChooser(sendIntent, null, pi.getIntentSender());
        startActivity(chooser);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void lanDialog() {
        /*Alert dialog -- Method creates locale change dialog*/
        final String[] vocaleList = {"English", "Čeština"};
        AlertDialog.Builder alertBuider = new AlertDialog.Builder(this);
        alertBuider.setTitle("Choose language");
        alertBuider.setSingleChoiceItems(vocaleList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    //English
                    setLocale("en");
                    initialized = false;
                    recreate();
                } else if (which == 1) {
                    //Czech
                    setLocale("cs");
                    initialized = false;
                    recreate();
                }

            }
        });
        AlertDialog lDialog = alertBuider.create();
        lDialog.show();
    }

    /* Method changes used String set*/
    private void setLocale(String loc) {
        Locale locale = new Locale(loc);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", loc);
        editor.apply();
    }
}
