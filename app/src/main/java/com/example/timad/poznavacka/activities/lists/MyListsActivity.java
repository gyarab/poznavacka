package com.example.timad.poznavacka.activities.lists;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.timad.poznavacka.BottomNavigationViewHelper;
import com.example.timad.poznavacka.DBTestObject;
import com.example.timad.poznavacka.PoznavackaInfo;
import com.example.timad.poznavacka.PreviewTestObject;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.RWAdapter;
import com.example.timad.poznavacka.StorageManagerClass;
import com.example.timad.poznavacka.Zastupce;
import com.example.timad.poznavacka.activities.AccountActivity;
import com.example.timad.poznavacka.activities.AuthenticationActivity;
import com.example.timad.poznavacka.activities.PracticeActivity;
import com.example.timad.poznavacka.activities.lists.createList.CreateListActivity;
import com.example.timad.poznavacka.activities.test.TestActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.NavigationMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

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

import static java.security.AccessController.getContext;


public class MyListsActivity extends AppCompatActivity {

    private static final String TAG = "myListsActivity";

    public static boolean savingNewList;
    private boolean autoImportIsChecked;
    private String title;
    private ArrayList<Zastupce> mZastupceArr;
    private String path;
    private String uuid;
    private String languageURL;

    public static boolean savingDownloadedList;
    private String userID;
    private String docID;

    private PoznavackaDbObject item;


    public static StorageManagerClass sSMC;

    public static PoznavackaInfo sActivePoznavacka = null;
    public static ArrayList<PoznavackaInfo> sPoznavackaInfoArr;

    private RecyclerView mRecyclerView;
    public static RWAdapter mAdapter;
    private RecyclerView.LayoutManager mLManager;
    public static int sPositionOfActivePoznavackaInfo;

    private FabSpeedDial newListBTN;
    private ProgressBar newListBTNProgressBar;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        newListBTN = findViewById(R.id.fabSpeedDial);
        newListBTNProgressBar = findViewById(R.id.fabSpeedDialProgressBar);
        newListBTNProgressBar.setVisibility(View.INVISIBLE);

        if (savingNewList) {
            newListBTNProgressBar.setVisibility(View.VISIBLE);
            newListBTNProgressBar.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));

            Intent newListIntent = getIntent();
            autoImportIsChecked = newListIntent.getBooleanExtra("AUTOIMPORTISCHECKED", false);
            title = newListIntent.getStringExtra("TITLE");
            mZastupceArr = newListIntent.getParcelableArrayListExtra("MZASTUPCEARR"); //TODO THIS
            path = newListIntent.getStringExtra("PATH");
            uuid = newListIntent.getStringExtra("UUID");
            languageURL = newListIntent.getStringExtra("LANGUAGEURL");
            SaveCreatedListAsync saveCreatedListAsync = new SaveCreatedListAsync();
            saveCreatedListAsync.execute();

        } else if (savingDownloadedList) {
            newListBTNProgressBar.setVisibility(View.VISIBLE);
            newListBTNProgressBar.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));

            Intent newDownloadedListIntent = getIntent();
            title = newDownloadedListIntent.getStringExtra("TITLE");
            userID = newDownloadedListIntent.getStringExtra("USERID");
            docID = newDownloadedListIntent.getStringExtra("DOCID");

            SaveDownloadedListAsync saveDownloadedListAsync = new SaveDownloadedListAsync();
            saveDownloadedListAsync.execute();
        }

        //initialization
        if (sPoznavackaInfoArr == null) {
            Gson gson = new Gson();
            String s = getSMC(getApplication()).readFile("poznavacka.txt", true);

            if (s != null) {
                if (!s.isEmpty()) {
                    Type cType = new TypeToken<ArrayList<PoznavackaInfo>>() {
                    }.getType();
                    sPoznavackaInfoArr = gson.fromJson(s, cType);
                    sActivePoznavacka = sPoznavackaInfoArr.get(0);
                    sPositionOfActivePoznavackaInfo = 0;
                } else {
                    sPoznavackaInfoArr = new ArrayList<>();
                    sPositionOfActivePoznavackaInfo = -1;
                }
            } else {
                sPoznavackaInfoArr = new ArrayList<>();
                sPositionOfActivePoznavackaInfo = -1;
            }
        }

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
                return super.onPrepareMenu(navigationMenu);
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case (R.id.action_test):
                        Intent intent2 = new Intent(getApplication(),MyTestActivity.class);
                        startActivity(intent2);
                        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                        finish();
                        break;
                    case (R.id.action_download):
                        Intent intent0 = new Intent(getApplication(), SharedListsActivity.class);
                        startActivity(intent0);
                        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                        finish();
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

        /* RecyclerView */
        mRecyclerView = findViewById(R.id.recyclerViewL);
        mRecyclerView.setHasFixedSize(true);
        mLManager = new LinearLayoutManager(getApplication());
        mAdapter = new RWAdapter(sPoznavackaInfoArr);

        mRecyclerView.setLayoutManager(mLManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RWAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                sPositionOfActivePoznavackaInfo = position;
                sActivePoznavacka = sPoznavackaInfoArr.get(sPositionOfActivePoznavackaInfo);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPracticeClick(final int position) {
                sPositionOfActivePoznavackaInfo = position;
                sActivePoznavacka = sPoznavackaInfoArr.get(sPositionOfActivePoznavackaInfo);
                mAdapter.notifyDataSetChanged();

                Intent myIntent = new Intent(getApplication(), PracticeActivity.class);
                startActivity(myIntent);
            }

            @Override
            public void onShareClick(final int position) {

                boolean poznavackaIsUploaded = sPoznavackaInfoArr.get(position).isUploaded();

                if (!poznavackaIsUploaded) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyListsActivity.this);
                    builder.setTitle(R.string.app_name);
                    builder.setIcon(R.drawable.ic_share_black_24dp);
                    builder.setMessage("Do you want to share " + sPoznavackaInfoArr.get(position).getName() + "?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Sharing of poznavacka
                            if (SharedListsActivity.checkInternet(getApplication())) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                //remote upload
                                String content = getSMC(getApplication()).readFile(sPoznavackaInfoArr.get(position).getId() + "/" + sPoznavackaInfoArr.get(position).getId() + ".txt", false);
                                if (!(user.getUid() == null)) {
                                    Timber.d("Adding to firestore");
                                    SharedListsActivity.addToFireStore(user.getUid(), new PoznavackaDbObject(sPoznavackaInfoArr.get(position).getName(), sPoznavackaInfoArr.get(position).getId(), content, sPoznavackaInfoArr.get(position).getAuthor(), sPoznavackaInfoArr.get(position).getAuthorsID(), sPoznavackaInfoArr.get(position).getPrewievImageUrl(), sPoznavackaInfoArr.get(position).getPrewievImageLocation(), sPoznavackaInfoArr.get(position).getLanguageURL(), System.currentTimeMillis()));
                                } else {
                                    Intent intent0 = new Intent(getApplication(), AuthenticationActivity.class);
                                    startActivity(intent0);
                                    finish();
                                }

                                //local change
                                sPoznavackaInfoArr.get(position).setUploaded(true);
                                getSMC(getApplication()).updatePoznavackaFile("poznavacka.txt", sPoznavackaInfoArr);

                                Toast toast = Toast.makeText(getApplication(), "Shared", Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                Toast.makeText(getApplication(), "ur not connected, connect please!", Toast.LENGTH_SHORT).show();
                            }

                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                } else {
                    Toast.makeText(getApplication(), "Shared", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteClick(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyListsActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setIcon(R.drawable.ic_delete);
                builder.setMessage("Do you really want to delete " + sPoznavackaInfoArr.get(position).getName() + "?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Context context = getApplication();
                        getSMC(context).deletePoznavacka(sPoznavackaInfoArr.get(position).getId() + "/");

                        sPoznavackaInfoArr.remove(position);
                        getSMC(context).updatePoznavackaFile("poznavacka.txt", sPoznavackaInfoArr);

                        if (position <= sPositionOfActivePoznavackaInfo) {
                            sPositionOfActivePoznavackaInfo -= 1;
                            if (sPoznavackaInfoArr.size() > 0) {
                                if (sPositionOfActivePoznavackaInfo < 0) {
                                    sPositionOfActivePoznavackaInfo = 0;
                                }
                                sActivePoznavacka = sPoznavackaInfoArr.get(sPositionOfActivePoznavackaInfo);
                            } else {
                                sActivePoznavacka = null;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

            @Override
            public void onTestClick(final int position) {
                if (SharedListsActivity.checkInternet(getApplication())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyListsActivity.this);
                    builder.setTitle(R.string.app_name);
                    builder.setIcon(R.drawable.ic_test);
                    builder.setMessage("Do you really want to put " + sPoznavackaInfoArr.get(position).getName() + " into tests ?");
                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String content = getSMC(getApplication()).readFile(sPoznavackaInfoArr.get(position).getId() + "/" + sPoznavackaInfoArr.get(position).getId() + ".txt", false);
                            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            String name = sPoznavackaInfoArr.get(position).getName();
                            boolean started = false;
                            String previewImgUrl = sPoznavackaInfoArr.get(position).getPrewievImageUrl();
                            boolean finished = false;
                            DBTestObject data= new DBTestObject(name,content,userID,previewImgUrl,started,finished,"","");
                            MyTestActivity.addToTests(userID,data);
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
                    Toast.makeText(getApplication(), "reconnect!", Toast.LENGTH_SHORT).show();
                }
            }


        });


        //navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_practice:
                        Intent intent0 = new Intent(MyListsActivity.this, PracticeActivity.class);
                        startActivity(intent0);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        break;

                    case R.id.nav_lists:
                        /*Intent intent1 = new Intent(ListsActivity.this, ListsActivity.class);
                startActivity(intent1);*/
                        break;

                    case R.id.nav_test:
                        Intent intent3 = new Intent(MyListsActivity.this, TestActivity.class);
                        startActivity(intent3);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;

                    case R.id.nav_account:
                        Intent intent4 = new Intent(MyListsActivity.this, AccountActivity.class);
                        startActivity(intent4);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;

                }


                return false;
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
            Toast.makeText(getApplication(), "Saving " + title + "...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String pathPoznavacka = "poznavacka.txt";
            MyListsActivity.getSMC(getApplicationContext()).updatePoznavackaFile(pathPoznavacka, MyListsActivity.sPoznavackaInfoArr);

            Log.d("Files", "Saved successfully");
            Toast.makeText(getApplication(), "Successfully saved " + title, Toast.LENGTH_LONG).show();

            savingNewList = false;
            newListBTNProgressBar.setVisibility(View.GONE);
            newListBTNProgressBar.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //changing getContext() to getApllicationContext()
            //changing getApplication() to getApplication()
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            // Store images
            Gson gson = new Gson();
            Context context = getApplicationContext();


            //DONE IN CREATE LIST ACTIVITY
            /*String uuid = UUID.randomUUID().toString();
            String path = uuid + "/";
            File dir = new File(context.getFilesDir().getPath() + "/" + path);

            // Create folder
            try {
                dir.mkdir();
            } catch (Exception e) {
                Toast.makeText(getApplication(), "Failed to save " + title, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return null;
            }


           // Saves images locally
            for (Zastupce z : mZastupceArr) {
                if (z.getImage() != null) {
                    if (!MyListsActivity.getSMC(context).saveDrawable(z.getImage(), path, z.getParameter(0))) {
                        Toast.makeText(getApplication(), "Failed to save " + title, Toast.LENGTH_SHORT).show();
                        return null;
                    }
                } else {
                    // TODO exception for first thing

                            *//*Toast.makeText(getApplication(), "Failed to save " + title, Toast.LENGTH_SHORT).show(); EDIT
                            deletePoznavacka(dir);
                            return;*//*
                }
                z.setImage(null);
            }*/

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
            if (!MyListsActivity.getSMC(context).createAndWriteToFile(path, uuid, json)) {
                Toast.makeText(getApplication(), "Failed to save " + title, Toast.LENGTH_SHORT).show();
                return null;
            }

            String pathPoznavacka = "poznavacka.txt";
            if (MyListsActivity.sPoznavackaInfoArr == null) {
                MyListsActivity.getSMC(context).readFile(pathPoznavacka, true);
            }
            if (autoImportIsChecked) {
                MyListsActivity.sPoznavackaInfoArr.add(new PoznavackaInfo(title, uuid, userName, userID, mZastupceArr.get(1).getParameter(0), mZastupceArr.get(1).getImageURL(), languageURL, false));
            } else {
                MyListsActivity.sPoznavackaInfoArr.add(new PoznavackaInfo(title, uuid, userName, userID, mZastupceArr.get(0).getParameter(0), mZastupceArr.get(0).getImageURL(), languageURL, false));
            }

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
            Toast.makeText(getApplication(), "Saving " + title + "...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Users").document(userID).collection("Poznavacky").document(docID);
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
            String pathPoznavacka = "poznavacka.txt";
            MyListsActivity.getSMC(getApplicationContext()).updatePoznavackaFile(pathPoznavacka, MyListsActivity.sPoznavackaInfoArr);

            Log.d("Files", "Saved successfully");
            Toast.makeText(getApplication(), "Successfully saved " + title, Toast.LENGTH_LONG).show();

            savingDownloadedList = false;
            newListBTNProgressBar.setVisibility(View.GONE);
            newListBTNProgressBar.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
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
