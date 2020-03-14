package com.example.timad.poznavacka.activities.lists;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timad.poznavacka.PoznavackaInfo;
import com.example.timad.poznavacka.PreviewPoznavacka;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.SharedListAdapter;
import com.example.timad.poznavacka.Zastupce;
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


public class SharedListsActivity extends AppCompatActivity {
    private static final String TAG = "SharedFragment";

    // shared list starts here
    private EditText searchView;
    private RecyclerView mRecyclerView;
    static private SharedListAdapter mSharedListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static ArrayList<PreviewPoznavacka> arrayList;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private final int DOCUMENTS_PAGINATE_COUNT = 10;
    private List<DocumentSnapshot> poznavackyDocs = null;
    private DocumentSnapshot poznavackaSnapshot;
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
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInternet(getApplication())) {
                    if (mSharedListAdapter != null) {
                        mSharedListAdapter.getFilter().filter(s);
                    }
                } else {
                    Toast.makeText(getApplication(), "reconnect!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void buildFirestoreSnapshots() {
        Timber.d("Buidling Firestore snapshots");





/*        usersQuery = db.collection("Users")
                .orderBy("timeUpdated")
                .limit(1);

        usersQuery
                .get()
                .continueWithTask(new Continuation<QuerySnapshot, Task<List<QuerySnapshot>>>() {
                    @Override
                    public Task<List<QuerySnapshot>> then(@NonNull Task<QuerySnapshot> task) {
                        List<Task<QuerySnapshot>> tasks = new ArrayList<Task<QuerySnapshot>>();
                        for (DocumentSnapshot ds : task.getResult()) {
                            tasks.add(ds.getReference().collection("Poznavacky").orderBy("timeUploaded").limit(1).get());

                            userSnapshot = ds;

                        }
                        return Tasks.whenAllSuccess(tasks);
                    }
                })

                .addOnCompleteListener(new OnCompleteListener<List<QuerySnapshot>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<QuerySnapshot>> task) {
                        if (task.isSuccessful()) {
                            List<QuerySnapshot> list = task.getResult();
                            for (QuerySnapshot qs : list) {
                                for (DocumentSnapshot ds : qs) {
                                    arrayList.add(new PreviewPoznavacka(ds.getString("headImageUrl"), ds.getString("name"), ds.getString("id"), ds.getString("authorsName"), ds.getString("authorsID"), ds.getString("languageURL")));

                                    poznavackaSnapshot = ds;
                                    currentDocumentsPaginate++;
                                }
                            }
                            buildRecyclerView();
                        }
                    }
                });*/

        //fetchFirestore();
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
        //LEFT OFF, fetching not working
        fetchFirstFirestore();

        //load more
        mSharedListAdapter.setLoadMore(new LoadMore() {
            @Override
            public void onLoadMore() {
                Timber.d("Load more");

                arrayList.add(null);
                mSharedListAdapter.notifyItemInserted(arrayList.size() - 1);
//LEFT OFF, fetching not working
                fetchFirestore();

            }
        });


        mSharedListAdapter.setOnItemClickListener(new SharedListAdapter.OnItemClickListener() {

            @Override
            public void onDownloadClick(final int position) {
                if (checkInternet(getApplication())) {
                    //checks if user doesn't already have the poznavacka downloaded
                    String docID = arrayList.get(position).getId();
                    String userID = arrayList.get(position).getAuthorsUuid();
                    boolean download = true;
                    for (PoznavackaInfo info :
                            MyListsActivity.sPoznavackaInfoArr) {
                        Timber.d("Download - " + info.getName() + ", id=" + info.getId() + "?equals - " + arrayList.get(position).getName() + ", id=" + docID);
                        if (info.getId().equals(docID)) {
                            download = false;
                            Timber.d("Do not download");
                            break;
                        }
                    }
                    if (download) {
                        pickDocument(userID, docID);

                        AlertDialog.Builder builder = new AlertDialog.Builder(SharedListsActivity.this);
                        builder.setTitle(R.string.app_name);
                        builder.setIcon(R.drawable.ic_file_download);
                        builder.setMessage("Do you really want to download " + arrayList.get(position).getName() + "?");
                        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


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
                            MyListsActivity.sPoznavackaInfoArr.get(position).setUploaded(false);
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
        Timber.d("Fetching first Firestore");
        Query poznavackaQuery = db.collectionGroup("Poznavacky").orderBy("timeUploaded", Query.Direction.DESCENDING).limit(DOCUMENTS_PAGINATE_COUNT);

        //first query (setting up snapshot)
        //poznavackyDocs = poznavackaQuery.get().getResult().getDocuments();
        poznavackaQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Timber.d("Fetching first Firestore task successful");
                    poznavackyDocs = task.getResult().getDocuments();
                    addDocsToScene();
                }
            }
        });
    }

    private void fetchFirestore() {

        Query poznavackaQuery = db.collectionGroup("Poznavacky").orderBy("timeUploaded", Query.Direction.DESCENDING).limit(DOCUMENTS_PAGINATE_COUNT).startAfter(poznavackaSnapshot);

        poznavackaQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    poznavackyDocs = task.getResult().getDocuments();
                    addDocsToScene();
                }
            }
        });



/*        while (currentDocumentsPaginate < DOCUMENTS_PAGINATE_COUNT) {
            usersQuery
                    .startAt(userSnapshot)
                    .get()
                    .continueWithTask(new Continuation<QuerySnapshot, Task<List<QuerySnapshot>>>() {
                        @Override
                        public Task<List<QuerySnapshot>> then(@NonNull Task<QuerySnapshot> task) {
                            List<Task<QuerySnapshot>> tasks = new ArrayList<Task<QuerySnapshot>>();
                            for (DocumentSnapshot ds : task.getResult()) {
                                tasks.add(ds.getReference().collection("Poznavacky").orderBy("timeUploaded").limit(1).get());
                            }

                            return Tasks.whenAllSuccess(tasks);
                        }
                    })


                    .addOnCompleteListener(new OnCompleteListener<List<QuerySnapshot>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<QuerySnapshot>> task) {
                            if (task.isSuccessful()) {
                                if (!nextUser) {
                                    arrayList.remove(arrayList.size() - 1);
                                    //mSharedListAdapter.notifyItemRemoved(arrayList.size());
                                }
                                List<QuerySnapshot> list = task.getResult();
                                for (QuerySnapshot qs : list) {
                                    for (DocumentSnapshot ds : qs) {
                                        arrayList.add(new PreviewPoznavacka(ds.getString("headImageUrl"), ds.getString("name"), ds.getString("id"), ds.getString("authorsName"), ds.getString("authorsID"), ds.getString("languageURL")));
                                        currentDocumentsPaginate++;
                                        if (currentDocumentsPaginate == DOCUMENTS_PAGINATE_COUNT) {
                                            nextUser();
                                            break;
                                        }
                                    }
                                }
                            }
                            //buildRecyclerView();

                        }
                    });
        }
        nextUser = false;
        currentDocumentsPaginate = 0;
        mSharedListAdapter.notifyDataSetChanged();
        mSharedListAdapter.setLoaded();
    }

    private void nextUser() {
        nextUser = true;
        Task<QuerySnapshot> task = usersQuery.startAfter(userSnapshot).get();
        for (DocumentSnapshot document : task.getResult()) {
            userSnapshot = document;
        }*/
    }

    private void addDocsToScene() {
        poznavackaSnapshot = poznavackyDocs.get(poznavackyDocs.size() - 1);
        arrayList.remove(arrayList.size() - 1);
        mSharedListAdapter.notifyItemRemoved(arrayList.size());
        for (DocumentSnapshot ds :
                poznavackyDocs) {
            arrayList.add(new PreviewPoznavacka(ds.getString("headImageUrl"), ds.getString("name"), ds.getString("id"), ds.getString("authorsName"), ds.getString("authorsID"), ds.getString("languageURL")));
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
    public void removePoznavacka(final String userID, final String documentName, final int position) {
        final DocumentReference docRef = db.collection("Users").document(userID).collection("Poznavacky").document(documentName);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                PoznavackaDbObject item = documentSnapshot.toObject(PoznavackaDbObject.class);

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

                //adding locally
                /*arrayList.add(new PreviewPoznavacka(data.getHeadImageUrl(), data.getName(), data.getId(), data.getAuthorsName(), data.getAuthorsID(), data.getLanguageURL()));
                mSharedListAdapter.notifyDataSetChanged();*/
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


    /*    //search
        @Override
        public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
            inflater.inflate(R.menu.preview_poznavacka_menu, menu);

            MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) searchItem.getActionView();

            searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mSharedListAdapter.getFilter().filter(newText);
                    return false;
                }
            });
        }*/

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