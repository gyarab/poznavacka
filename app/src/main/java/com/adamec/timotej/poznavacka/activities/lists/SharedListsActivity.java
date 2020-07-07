package com.adamec.timotej.poznavacka.activities.lists;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adamec.timotej.poznavacka.PoznavackaInfo;
import com.adamec.timotej.poznavacka.PreviewPoznavacka;
import com.adamec.timotej.poznavacka.R;
import com.adamec.timotej.poznavacka.SharedListAdapter;
import com.adamec.timotej.poznavacka.Zastupce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Interface.LoadMore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

//import static com.example.timad.poznavacka.activities.lists.MyListsActivity.mInterstitialAd; //TODO what


public class SharedListsActivity extends AppCompatActivity {
    private static final String TAG = "SharedFragment";

    // shared list starts here
    private EditText searchView;
    private RecyclerView mRecyclerView;
    private SharedListAdapter mSharedListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static ArrayList<PreviewPoznavacka> arrayList;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private final int DOCUMENTS_PAGINATE_COUNT = 10;
    private List<DocumentSnapshot> poznavackyDocs = null;
    private DocumentSnapshot poznavackaSnapshot = null;
    private boolean firstQueryFetched;

    private static ArrayList<String> imgUrls = new ArrayList<>();
    private static ArrayList<Drawable> imgDrawables = new ArrayList<>();
    private static ArrayList<Zastupce> zastupceArr = new ArrayList<>();
    private static PoznavackaDbObject item;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharedlists);
        Timber.plant(new Timber.DebugTree());

        if (checkInternet(this)) {
            buildSharedListFragment();
        } else {
            Toast.makeText(getApplication(), "ur not connected,restart app and connect plis!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(getApplication(), MyListsActivity.class);
        startActivity(intent1);
        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
        finish();
    }

    private void buildSharedListFragment() {
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        //vyt vori array prida do arraye vytvori recykler view
        createArr();
        //buildFirestoreSnapshots();
        buildRecyclerView();
        //displayWholeFirestore();

        searchView = findViewById(R.id.search_view);
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = textView.getText().toString();
                    if (searchText.isEmpty()) {
                        fetchFirstFirestore();
                    } else {
                        fetchFirstFirestoreSearch(searchText);
                    }

                    Toast.makeText(getApplication(), "Search " + textView.getText().toString(), Toast.LENGTH_SHORT).show();
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        searchView.clearFocus();
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void fetchFirstFirestoreSearch(final String searchText) {
        deactivateLoadMore();
        arrayList.clear();
        mSharedListAdapter.notifyDataSetChanged();
        arrayList.add(null);
        mSharedListAdapter.notifyItemInserted(arrayList.size() - 1);

        Timber.d("Fetching first Firestore search for = %s", searchText);
        Query poznavackaQuery = db.collectionGroup("Poznavacky").whereGreaterThanOrEqualTo("name", searchText).orderBy("name").limit(DOCUMENTS_PAGINATE_COUNT);

        //first query (setting up snapshot)
        //poznavackyDocs = poznavackaQuery.get().getResult().getDocuments();
        poznavackaQuery
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Timber.d("first search query completed");
                        if (task.isSuccessful()) {
                            Timber.d("first searcb query successful");
                            poznavackyDocs = task.getResult().getDocuments();
                            Timber.d("first search poznavackyDocs size = %s", poznavackyDocs.size());
                            addDocsToScene();
                            if (poznavackyDocs.size() == 0) {
                                activateLoadMore(searchText);
                            }
                        }
                    }
                });
    }

    private void fetchFirestoreSearch(final String searchText) {
        Timber.d("Fetching firestore search");

        Query poznavackaQuery = db.collectionGroup("Poznavacky").whereGreaterThanOrEqualTo("name", searchText).limit(DOCUMENTS_PAGINATE_COUNT).startAfter(poznavackaSnapshot);

        poznavackaQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Timber.d("query completed");
                if (task.isSuccessful()) {
                    Timber.d("query successful");
                    poznavackyDocs = task.getResult().getDocuments();
                    if (poznavackyDocs.size() == 0) {
                        deactivateLoadMore();
                    }
                    addDocsToScene();
                }
            }
        });
    }

    //vytvori arraylist
    static private void createArr() {
        arrayList = new ArrayList<>();
    }

    private void pickDocument(final String userID, String docID) {
        DocumentReference docRef = db.collection("Users").document(userID).collection("Poznavacky").document(docID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
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
                    MyListsActivity.getSMC(getApplication()).createAndWriteToFile(path, item.getId() + "classification", item.getClassification());
                    if (!MyListsActivity.getSMC(getApplication()).createAndWriteToFile(path, item.getId(), item.getContent())) {
                        Toast.makeText(getApplication(), "Failed to save " + item.getName(), Toast.LENGTH_SHORT).show();
                        return;
                    } else {

                        new DrawableFromUrlAsync(SharedListsActivity.this).execute();  //viz Async metoda dole

                    }

                    String pathPoznavacka = "poznavacka.txt";
                    if (MyListsActivity.sPoznavackaInfoArr == null) {
                        MyListsActivity.getSMC(getApplication()).readFile(pathPoznavacka, true);
                    }
                    MyListsActivity.sPoznavackaInfoArr.add(new PoznavackaInfo(item.getName(), item.getId(), item.getAuthorsName(), item.getAuthorsID(), item.getHeadImagePath(), item.getHeadImageUrl(), item.getLanguageURL(), true));
                    MyListsActivity.getSMC(getApplication()).updatePoznavackaFile(pathPoznavacka, MyListsActivity.sPoznavackaInfoArr);

                    Log.d("Files", "Saved successfully");
                    Toast.makeText(getApplication(), "Successfully saved " + item.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void buildRecyclerView() {
        Timber.d("Building recycler view");
        mRecyclerView = findViewById(R.id.downloadView);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mSharedListAdapter = new SharedListAdapter(mRecyclerView, arrayList, this);
        mRecyclerView.setAdapter(mSharedListAdapter);

        arrayList.add(null);
        mSharedListAdapter.notifyItemInserted(arrayList.size() - 1);
        fetchFirstFirestore();


        mSharedListAdapter.setOnItemClickListener(new SharedListAdapter.OnItemClickListener() {

            @Override
            public void onDownloadClick(final int position) {
                if (checkInternet(getApplication())) {
                    //checks if user doesn't already have the poznavacka downloaded
                    final String docID = arrayList.get(position).getId();
                    final String userID = arrayList.get(position).getAuthorsUuid();
                    boolean download = true;
                    for (Object info :
                            MyListsActivity.sPoznavackaInfoArr) {
                        if (info instanceof PoznavackaInfo) {
                            PoznavackaInfo currentPozn = (PoznavackaInfo) info;
                            Timber.d("Download - " + currentPozn.getName() + ", id=" + currentPozn.getId() + "?equals - " + arrayList.get(position).getName() + ", id=" + docID);
                            if (currentPozn.getId().equals(docID)) {
                                download = false;
                                Timber.d("Do not download");
                                Toast.makeText(getApplicationContext(), "Already downloaded", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                    if (download) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SharedListsActivity.this);
                        builder.setTitle(R.string.app_name);
                        builder.setIcon(R.drawable.ic_file_download);
                        builder.setMessage("Do you really want to download " + arrayList.get(position).getName() + "?");
                        builder
                                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //pickDocument(userID, docID);
                                        dialog.dismiss();

                                        MyListsActivity.savingDownloadedList = true;
                                        Intent intent0 = new Intent(getApplicationContext(), MyListsActivity.class);

                                        intent0.putExtra("TITLE", arrayList.get(position).getName());
                                        intent0.putExtra("USERID", userID);
                                        intent0.putExtra("DOCID", docID);

                                        startActivity(intent0);
                                        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                                        finish();
                                    }
                                })
                                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                } else {
                    Toast.makeText(getApplication(), "reconnect!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteClick(final int position) {
                if (checkInternet(getApplication())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SharedListsActivity.this);
                    builder.setTitle(R.string.app_name);
                    builder.setIcon(R.drawable.ic_delete);
                    builder.setMessage("Do you really want to delete " + arrayList.get(position).getName() + "?");
                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String documentID = arrayList.get(position).getId();
                            String userID;
                            try {
                                userID = user.getUid();
                            } catch (Exception e) {
                                Toast.makeText(getApplication(), "ur not logged in" + e.toString(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            removePoznavacka(userID, documentID, position);

                            //local change
                            for (Object object :
                                    MyListsActivity.sPoznavackaInfoArr) {
                                if (object instanceof PoznavackaInfo) {
                                    PoznavackaInfo currentPozn = (PoznavackaInfo) object;
                                    if (currentPozn.getId().equals(arrayList.get(position).getId())) {
                                        currentPozn.setUploaded(false);
                                    }
                                }
                            }

                            //MyListsActivity.sPoznavackaInfoArr.get(position).setUploaded(false);
                            MyListsActivity.getSMC(getApplication()).updatePoznavackaFile("poznavacka.txt", MyListsActivity.sPoznavackaInfoArr);

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
    }

    private void fetchFirstFirestore() {
        deactivateLoadMore();
        arrayList.clear();
        mSharedListAdapter.notifyDataSetChanged();
        arrayList.add(null);
        mSharedListAdapter.notifyItemInserted(arrayList.size() - 1);

        Timber.d("Fetching first Firestore");
        Query poznavackaQuery = db.collectionGroup("Poznavacky").orderBy("timeUploaded", Query.Direction.DESCENDING).limit(DOCUMENTS_PAGINATE_COUNT);

        //first query (setting up snapshot)
        poznavackaQuery
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Timber.d("query completed");
                        if (task.isSuccessful()) {
                            Timber.d("query successful");
                            poznavackyDocs = task.getResult().getDocuments();
                            Timber.d("poznavackyDocs size = %s", poznavackyDocs.size());
                            if (poznavackyDocs.size() == 0) {
                                deactivateLoadMore();
                            } else {
                                addDocsToScene();
                                activateLoadMore();
                            }
                        }
                    }
                });
    }

    private void fetchFirestore() {
        Timber.d("Fetching firestore");

        Query poznavackaQuery = db.collectionGroup("Poznavacky").orderBy("timeUploaded", Query.Direction.DESCENDING).limit(DOCUMENTS_PAGINATE_COUNT).startAfter(poznavackaSnapshot);

        poznavackaQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Timber.d("query completed");
                if (task.isSuccessful()) {
                    Timber.d("query successful");
                    poznavackyDocs = task.getResult().getDocuments();
                    addDocsToScene();
                }
            }
        });
    }

    private void activateLoadMore(final String searchText) {
        //load more
        mSharedListAdapter.setLoadMore(new LoadMore() {
            @Override
            public void onLoadMore() {
                fetchFirestoreSearch(searchText);
                Timber.d("Load more of = %s", searchText);

                arrayList.add(null);
                mSharedListAdapter.notifyItemInserted(arrayList.size() - 1);
            }
        });
    }

    private void activateLoadMore() {
        //load more
        mSharedListAdapter.setLoadMore(new LoadMore() {
            @Override
            public void onLoadMore() {
                fetchFirestore();
                Timber.d("Load more");

                arrayList.add(null);
                mSharedListAdapter.notifyItemInserted(arrayList.size() - 1);
            }
        });
    }

    private void deactivateLoadMore() {
        mSharedListAdapter.setLoadMore(new LoadMore() {
            @Override
            public void onLoadMore() {
                Timber.d("Don't load more");
                mSharedListAdapter.setLoaded();
            }
        });
    }

    private void addDocsToScene() {
        Timber.d("Adding docs to scene");
        arrayList.remove(arrayList.size() - 1);
        mSharedListAdapter.notifyItemRemoved(arrayList.size());
        if (poznavackyDocs.size() != 0) {
            poznavackaSnapshot = poznavackyDocs.get(poznavackyDocs.size() - 1);
            for (DocumentSnapshot ds :
                    poznavackyDocs) {
                arrayList.add(new PreviewPoznavacka(ds.getString("headImageUrl"), ds.getString("name"), ds.getString("id"), ds.getString("authorsName"), ds.getString("authorsID"), ds.getString("languageURL")));
            }
        }
        mSharedListAdapter.notifyDataSetChanged();
        mSharedListAdapter.setLoaded();
    }

    //nedodelane
    public void sharePoznavacka(String collectionName, final String collecionShareName, String documentShareId) {
        DocumentReference docRef = db.collection(collectionName).document(documentShareId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                PoznavackaDbObject item = documentSnapshot.toObject(PoznavackaDbObject.class);
                addToFireStore(collecionShareName, item);

                try {
                    Toast.makeText(getApplication(), documentSnapshot.get("name").toString(), Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // potreba pridat moznost odebrani autorem w verejnym collectionu chce to upravit
    // nedodelane
    public void removePoznavacka(final String userID, final String docID, final int position) {
        final DocumentReference docRef = db.collection("Users").document(userID).collection("Poznavacky").document(docID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){

                    docRef
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    arrayList.remove(position);
                                    mSharedListAdapter.notifyDataSetChanged();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
            }
        }
        });


    }

    public static void addToFireStore(String userID, final PoznavackaDbObject data) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference dbPoznavacka = db.collection("Users");
        final DocumentReference dbUser = dbPoznavacka.document(userID);
        CollectionReference dbPoznavacky = dbUser.collection("Poznavacky");

        //timestamp
        Map<String, Long> timeUpdated = new HashMap<>();
        timeUpdated.put("timeUpdated", data.getTimeUploaded());
        dbUser.set(timeUpdated);

        dbPoznavacky.document(data.getId()).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

    public static boolean checkInternet(Context context) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        } else {
            connected = false;
        }
        return connected;
    }

    private class DrawableFromUrlAsync extends AsyncTask<Void, Void, Drawable> {

        private WeakReference<SharedListsActivity> fragmentWeakReference;

        DrawableFromUrlAsync(SharedListsActivity context) {
            fragmentWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Drawable doInBackground(Void... voids) {
            SharedListsActivity fragment = fragmentWeakReference.get();
            String path = item.getId() + "/";
            Gson gson = new Gson();
            Type cType = new TypeToken<ArrayList<Zastupce>>() {
            }.getType();
            zastupceArr = gson.fromJson(item.getContent(), cType);
            for (Zastupce z : zastupceArr) {
                Drawable returnDrawable = null;
                if (!(z.getImageURL() == null || z.getImageURL().isEmpty())) {
                    try {
                        returnDrawable = drawable_from_url(z.getImageURL());
                    } catch (IOException e) {
                        Log.d("Obrazek", "Obrazek nestahnut");
                        e.printStackTrace();
                    }
                    MyListsActivity.getSMC(fragment.getApplication()).saveDrawable(returnDrawable, path, z.getParameter(0));
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
}