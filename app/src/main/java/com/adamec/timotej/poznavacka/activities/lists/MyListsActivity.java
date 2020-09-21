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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.internal.NavigationMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
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

    public static boolean savingDownloadedList;
    private String userIDfromGenerated;
    private String docID;

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

    private ExtendedFloatingActionButton examEFAB;

    private RelativeLayout noListsLayout;
    private static TourGuide mTourGuide;

    public static InterstitialAd mInterstitialAd;
    public static UnifiedNativeAd mUnifiedNativeAd;
    public static boolean initialized;
    private AdLoader nativeAdLoader;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            showInterstitial();

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

            SaveCreatedListAsync saveCreatedListAsync = new SaveCreatedListAsync();
            saveCreatedListAsync.execute();

        } else if (savingDownloadedList) {
            showInterstitial();
            newListBTNProgressBar.setVisibility(View.VISIBLE);
            newListBTNProgressBar.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));

            Intent newDownloadedListIntent = getIntent();
            title = newDownloadedListIntent.getStringExtra("TITLE");
            userIDfromGenerated = newDownloadedListIntent.getStringExtra("USERID");
            docID = newDownloadedListIntent.getStringExtra("DOCID");

            SaveDownloadedListAsync saveDownloadedListAsync = new SaveDownloadedListAsync();
            saveDownloadedListAsync.execute();
        } else if (sPoznavackaInfoArr == null || sPositionOfActivePoznavackaInfo == -1 || sPoznavackaInfoArr.isEmpty()) {
            if (sPoznavackaInfoArr == null) {
                sPoznavackaInfoArr = new ArrayList<>();
            }
        }

        if (!initialized) {
            init(getApplication());
            initialized = true;

            if (sPoznavackaInfoArr == null || sPositionOfActivePoznavackaInfo == -1 || sPoznavackaInfoArr.isEmpty()) {
                initTourGuide();
            }
        }

        /*//initialization
        if (sPoznavackaInfoArr == null) {
            Gson gson = new Gson();
            String s = getSMC(getApplication()).readFile("poznavacka.txt", true);

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
                    *//*Toast.makeText(getApplication(), "NOTHING IS HERE", Toast.LENGTH_SHORT).show();
                    noListsLayout = findViewById(R.id.no_lists_layout);
                    noListsLayout.setVisibility(View.VISIBLE);*//*
                    initTourGuide();

                }
            } else {
                sPoznavackaInfoArr = new ArrayList<>();
                sPositionOfActivePoznavackaInfo = -1;

                initTourGuide();
            }
        }*/

        /* Add new button */
        /*newListBTN = view.findViewById(R.id.new_list_btn);
        newListBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent0 = new Intent(getContext(), CreateListActivity.class);
                startActivity(intent0);
                getActivity().overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                getActivity().finish();
            }
        });*/
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
                        Intent intent0 = new Intent(getApplication(), SharedListsActivity.class);
                        startActivity(intent0);
                        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);

                        mInterstitialAd = new InterstitialAd(getApplication());
                        mInterstitialAd.setAdUnitId("ca-app-pub-2924053854177245/3480271080");
                        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //Test add
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        Timber.d("Interstitial shared lists ad loaded");

                        mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                // Load the next interstitial.
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                Timber.d("Interstitial shared lists ad loaded");
                            }

                        });
                        break;
                    case (R.id.action_create):
                        Intent intent1 = new Intent(getApplication(), CreateListActivity.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                        finish();
                        break;


                }
                return true;
            }
        });

        /*examEFAB = findViewById(R.id.exams_btn); //TODO return exams
        examEFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplication(), MyExamsActivity.class);
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
        mLManager = new LinearLayoutManager(getApplication());
        mAdapter = new RWAdapter(sPoznavackaInfoArr);

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
                sPositionOfActivePoznavackaInfo = position;
                sActivePoznavacka = (PoznavackaInfo) sPoznavackaInfoArr.get(sPositionOfActivePoznavackaInfo);
                mAdapter.notifyDataSetChanged();
            }

            /**
             * Po kliknutí na ikonku přenese uživatele do Practice Activity
             * @param position - pozice položky v poli
             */
            @Override
            public void onPracticeClick(final int position) {
                sPositionOfActivePoznavackaInfo = position;
                sActivePoznavacka = (PoznavackaInfo) sPoznavackaInfoArr.get(position);
                mAdapter.notifyDataSetChanged();

                Intent myIntent = new Intent(getApplication(), PracticeActivity.class);
                startActivity(myIntent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }

            /**
             * Po kliknutí na ikonku sdílet vyskočí dialogové okno, které se zeptá na potvrzení volby. Po potvrzení je Poznávačka sdílena.
             * @param position - pozice položky v poli
             */
            @Override
            public void onShareClick(final int position) {
                sActivePoznavacka = (PoznavackaInfo) sPoznavackaInfoArr.get(position);

                boolean poznavackaIsUploaded = sActivePoznavacka.isUploaded();
                String link = createDeepLink(sActivePoznavacka);

                if (SharedListsActivity.checkInternet(getApplicationContext())) {
                    if (!poznavackaIsUploaded) {
                        if (upload()) {
                            shareIntent(link);
                        }
                    } else {
                        shareIntent(link);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_SHORT).show();
                }

                //Deletion from database
                /*} else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyListsActivity.this);
                    builder.setTitle(R.string.remove_from_database);
                    builder.setIcon(R.drawable.ic_share_black_24dp);
                    builder.setMessage(getString(R.string.do_you_want_to_delete) + sActivePoznavacka.getName() + " from shared database?");
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Sharing of poznavacka
                            if (SharedListsActivity.checkInternet(getApplication())) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                //remote deletion
                                removeFromDatabase(sActivePoznavacka.getId());

                                //local change
                                sActivePoznavacka.setUploaded(false);
                                getSMC(getApplication()).updatePoznavackaFile("poznavacka.txt", sPoznavackaInfoArr);

                            } else {
                                Toast.makeText(getApplication(), "No internet, connect please!", Toast.LENGTH_SHORT).show();
                            }

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
                }*/
            }

            /**
             * Zeptá se uživatele na potvrzení smazání Poznávačky. Po potvrzení je Poznávačka smazána ze zařízení.
             * @param position - pozice položky v poli
             */
            @Override
            public void onDeleteClick(final int position) {
                sActivePoznavacka = (PoznavackaInfo) sPoznavackaInfoArr.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MyListsActivity.this);
                builder.setTitle(R.string.delete);
                builder.setIcon(R.drawable.ic_delete);
                builder.setMessage(getString(R.string.do_you_want_to_delete) + sActivePoznavacka.getName() + "?");
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Context context = getApplication();
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
                                    //Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                sActivePoznavacka = null;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
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

            /**
             * Po kliknutí na ikonu testu se zeptá na potvrzení o vytvoření testu. Když uživatel odpoví ano, tak je testen vytvořen.
             * @param position - pozice položky v poli
             */
            @Override
            public void onTestClick(final int position) {
                sActivePoznavacka = (PoznavackaInfo) sPoznavackaInfoArr.get(position);
                if (SharedListsActivity.checkInternet(getApplication())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyListsActivity.this);
                    builder.setTitle(R.string.add_to_exams);
                    builder.setIcon(R.drawable.ic_school_black_24dp);
                    builder.setMessage("Do you want to add " + sActivePoznavacka.getName() + " into exams?");
                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String classification = getSMC(getApplication()).readFile(sActivePoznavacka.getId() + "/" + sActivePoznavacka.getId() + "classification.txt", false);
                            String content = getSMC(getApplication()).readFile(sActivePoznavacka.getId() + "/" + sActivePoznavacka.getId() + ".txt", false);
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
                    Toast.makeText(getApplication(), "Connect to internet!", Toast.LENGTH_SHORT).show();
                }
            }


        });


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
                        Intent intent0 = new Intent(MyListsActivity.this, PracticeActivity.class);
                        startActivity(intent0);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

    private String createDeepLink(PoznavackaInfo poznavackaId) {
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://play.google.com/store/apps/details?id=com.adamec.timotej.poznavacka"))
                .setDomainUriPrefix("https://memimgapp.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setGoogleAnalyticsParameters(
                        new DynamicLink.GoogleAnalyticsParameters.Builder()
                                .setSource("shareSource")
                                .setMedium("shareMedium")
                                .setCampaign("shareCampaign")
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(getString(R.string.poznavacka) + " " + poznavackaId.getName())
                                .setDescription(String.valueOf(R.string.learn_for_exam))
                                .build())
                .buildDynamicLink();

        //TODO DEEP LINK

        Uri dynamicLinkUri = dynamicLink.getUri();
        return String.valueOf(dynamicLinkUri);
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
                    /*Toast.makeText(getApplication(), "NOTHING IS HERE", Toast.LENGTH_SHORT).show();
                    noListsLayout = findViewById(R.id.no_lists_layout);
                    noListsLayout.setVisibility(View.VISIBLE);*/
            }
        } else {
            Timber.d("init poznavacka info array from device");
            sPoznavackaInfoArr = new ArrayList<>();
            sPositionOfActivePoznavackaInfo = -1;
        }
        //}
    }

    private void loadLanguage() {
        SharedPreferences prefs = getApplication().getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "en");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    //TODO return

    private void loadNativeAd() {

        //nativeAdLoader = new AdLoader.Builder(getApplication(), "ca-app-pub-3940256099942544/2247696110") TEST
        nativeAdLoader = new AdLoader.Builder(getApplication(), "ca-app-pub-2924053854177245/6546317855")
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
                        MobileAds.initialize(getApplication());
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
        nativeAdLoader.loadAd(new AdRequest.Builder().build());
    }

    private void showInterstitial() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2924053854177245/3480271080");
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //Test add
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
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
                                //Toast.makeText(getApplication(), R.string.removed_from_database, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplication(), R.string.failed_to_remove_from_database, Toast.LENGTH_SHORT).show();
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
            //Toast.makeText(getApplication(), "Saving " + title + "...", Toast.LENGTH_LONG).show();
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
            //Toast.makeText(getApplication(), "Successfully saved " + title, Toast.LENGTH_LONG).show();

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
            //changing getApplication() to getApplication()
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
                Intent intent0 = new Intent(getApplication(), AuthenticationActivity.class);
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
                    Toast.makeText(getApplication(), "Failed to save " + title, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplication(), "Failed to save " + title, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplication(), "Failed to save " + title, Toast.LENGTH_SHORT).show();
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
    }

    private class SaveDownloadedListAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(getApplication(), "Saving " + title + "...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Users").document(userIDfromGenerated).collection("Poznavacky").document(docID);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    item = documentSnapshot.toObject(PoznavackaDbObject.class);
                    if (item == null) {
                        Timber.d("Item documentSnapshot docID=" + documentSnapshot.getId());
                        Timber.d("Item documentSnapshot id=" + documentSnapshot.getString("id"));
                        Timber.d("Item documentSnapshot name=" + documentSnapshot.getString("name"));
                        Timber.d("Item " + documentSnapshot.getString("name") + " is null");
                    }

                    // Store images
                    Context context = getApplication();
                    String path = item.getId() + "/";
                    File dir = new File(context.getFilesDir().getPath() + "/" + path);

                    // Create folder
                    try {
                        dir.mkdir();
                    } catch (Exception e) {
                        Toast.makeText(getApplication(), "Failed to save " + item.getName(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        return;
                    }

                    if (item.getClassification() != null) {
                        MyListsActivity.getSMC(getApplication()).createAndWriteToFile(path, item.getId() + "classification", item.getClassification());
                    }

                    if (!MyListsActivity.getSMC(getApplication()).createAndWriteToFile(path, item.getId(), item.getContent())) {
                        Toast.makeText(getApplication(), "Failed to save " + item.getName(), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        //images download
                        new DrawableFromUrlAsync().execute();  //viz Async metoda dole
                    }

                    String pathPoznavacka = "poznavacka.txt";
                    if (MyListsActivity.sPoznavackaInfoArr == null) {
                        MyListsActivity.getSMC(getApplication()).readFile(pathPoznavacka, true);
                    }
                    MyListsActivity.sPoznavackaInfoArr.add(new PoznavackaInfo(item.getName(), item.getId(), item.getAuthorsName(), item.getAuthorsID(), item.getHeadImagePath(), item.getHeadImageUrl(), item.getLanguageURL(), true));
                    //MyListsActivity.getSMC(getApplication()).updatePoznavackaFile(pathPoznavacka, MyListsActivity.sPoznavackaInfoArr);

                    Log.d("Files", "Saved successfully");
                }
            });


            return null;
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
            //Toast.makeText(getApplication(), "Successfully saved " + title, Toast.LENGTH_LONG).show();

            savingDownloadedList = false;
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
                Drawable returnDrawable = null;
                if (!(z.getImageURL() == null || z.getImageURL().isEmpty())) {
                    try {
                        returnDrawable = drawable_from_url(z.getImageURL());
                    } catch (IOException e) {
                        Log.d("Obrazek", "Obrazek nestahnut");
                        e.printStackTrace();
                    }
                    MyListsActivity.getSMC(getApplication()).saveDrawable(returnDrawable, path, z.getParameter(0));
                }
            }
            return null;
        }


        public Drawable drawable_from_url(String url) throws java.io.IOException {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-agent", "Mozilla");

            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            Log.d("Obrazek", "Obrazek stahnut");
            return new BitmapDrawable(Objects.requireNonNull(getApplication()).getResources(), bitmap);
        }
    }

    public boolean upload() {
        if (SharedListsActivity.checkInternet(getApplication())) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            //remote upload
            String content = getSMC(getApplication()).readFile(sActivePoznavacka.getId() + "/" + sActivePoznavacka.getId() + ".txt", false);
            String classification = getSMC(getApplication()).readFile(sActivePoznavacka.getId() + "/" + sActivePoznavacka.getId() + "classification.txt", false);
            if (!(user.getUid() == null)) {
                Timber.d("Adding to firestore");
                SharedListsActivity.addToFireStore(user.getUid(), new PoznavackaDbObject(sActivePoznavacka.getName(), sActivePoznavacka.getId(), classification, content, sActivePoznavacka.getAuthor(), sActivePoznavacka.getAuthorsID(), sActivePoznavacka.getPrewievImageUrl(), sActivePoznavacka.getPrewievImageLocation(), sActivePoznavacka.getLanguageURL(), System.currentTimeMillis()));
            } else {
                Intent intent0 = new Intent(getApplication(), AuthenticationActivity.class);
                startActivity(intent0);
                finish();
                return false;
            }

            //local change
            sActivePoznavacka.setUploaded(true);
            getSMC(getApplication()).updatePoznavackaFile("poznavacka.txt", sPoznavackaInfoArr);

            //Toast toast = Toast.makeText(getApplication(), "Shared", Toast.LENGTH_SHORT);
            //toast.show();
            return true;

        } else {
            Toast.makeText(getApplication(), "No internet", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void shareIntent(String link) {
        //String message = getString(R.string.i_created_list) + "\n\n" + getString(R.string.app_url_store) + "\n\n" + getString(R.string.after_search_for) + " " + sActivePoznavacka.getName();
        String message = getString(R.string.i_created_list) + "\n\n" + link;
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

    /*        //fragments navigation
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_list_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_file_download);
//        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_add_circle_black_24dp);
*/




/*
    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyListsFragment());
        adapter.addFragment(new SharedListsFragment());
        //adapter.addFragment(new CreateListFragment());
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber) {
        mViewPager.setCurrentItem(fragmentNumber);
    }*/

}