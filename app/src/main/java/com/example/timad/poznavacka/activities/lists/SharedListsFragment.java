package com.example.timad.poznavacka.activities.lists;

import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timad.poznavacka.PoznavackaInfo;
import com.example.timad.poznavacka.PreviewPoznavacka;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.SharedListAdapter;
import com.example.timad.poznavacka.Zastupce;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;


public class SharedListsFragment extends Fragment {
    private static final String TAG = "SharedFragment";

    // shared list starts here
    private EditText searchView;
    private RecyclerView mRecyclerView;
    static private SharedListAdapter mSharedListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static ArrayList<PreviewPoznavacka> arrayList;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private static ArrayList<String> imgUrls = new ArrayList<>();
    private static ArrayList<Drawable> imgDrawables = new ArrayList<>();
    private static ArrayList<Zastupce> zastupceArr = new ArrayList<>();
    private static PoznavackaDbObject item;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sharedlists, container, false);
        Timber.plant(new Timber.DebugTree());
        if (checkInternet(getContext())) {
            buildSharedListFragment(view);
        } else {
            Toast.makeText(getContext(), "ur not connected,restart app and connect plis!", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void buildSharedListFragment(View view) {
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        //vyt vori array prida do arraye vytvori recykler view
        createArr();
        displayFirestore(view);
        searchView = view.findViewById(R.id.search_view);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInternet(getContext())) {
                    if (mSharedListAdapter != null) {
                        mSharedListAdapter.getFilter().filter(s);
                    }
                } else {
                    Toast.makeText(getContext(), "reconnect!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
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
                item = documentSnapshot.toObject(PoznavackaDbObject.class);
                if (item == null) {
                    Timber.d("Item documentSnapshot docID=" + documentSnapshot.getId());
                    Timber.d("Item documentSnapshot id=" + documentSnapshot.getString("id"));
                    Timber.d("Item documentSnapshot name=" + documentSnapshot.getString("name"));
                    Timber.d("Item " + documentSnapshot.getString("name") + " is null");
                }

                // Store images
                Context context = getContext();
                String path = item.getId() + "/";
                File dir = new File(context.getFilesDir().getPath() + "/" + path);

                // Create folder
                try {
                    dir.mkdir();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Failed to save " + item.getName(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }

                if (!MyListsFragment.getSMC(getContext()).createAndWriteToFile(path, item.getId(), item.getContent())) {
                    Toast.makeText(getActivity(), "Failed to save " + item.getName(), Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    new DrawableFromUrlAsync(SharedListsFragment.this).execute();  //viz Async metoda dole

                }

                String pathPoznavacka = "poznavacka.txt";
                if (MyListsFragment.sPoznavackaInfoArr == null) {
                    MyListsFragment.getSMC(getContext()).readFile(pathPoznavacka, true);
                }
                MyListsFragment.sPoznavackaInfoArr.add(new PoznavackaInfo(item.getName(), item.getId(), item.getAuthorsName(), item.getAuthorsID(), item.getHeadImagePath(), item.getHeadImageUrl(), item.getLanguageURL(), true));
                MyListsFragment.getSMC(getContext()).updatePoznavackaFile(pathPoznavacka, MyListsFragment.sPoznavackaInfoArr);

                Log.d("Files", "Saved successfully");
                Toast.makeText(getActivity(), "Successfully saved " + item.getName(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void buildRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.downloadView);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getContext());
        mSharedListAdapter = new SharedListAdapter(arrayList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mSharedListAdapter);
        mSharedListAdapter.setOnItemClickListener(new SharedListAdapter.OnItemClickListener() {

            @Override
            public void onDownloadClick(final int position) {
                if (checkInternet(getContext())) {
                    //checks if user doesn't already have the poznavacka downloaded
                    String docID = arrayList.get(position).getId();
                    String userID = arrayList.get(position).getAuthorsUuid();
                    boolean download = true;
                    for (PoznavackaInfo info :
                            MyListsFragment.sPoznavackaInfoArr) {
                        Timber.d("Download - " + info.getName() + ", id=" + info.getId() + "?equals - " + arrayList.get(position).getName() + ", id=" + docID);
                        if (info.getId().equals(docID)) {
                            download = false;
                            Timber.d("Do not download");
                            break;
                        }
                    }
                    if (download) {
                        pickDocument(userID, docID);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                    Toast.makeText(getContext(), "reconnect!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteClick(final int position) {
                if (checkInternet(getContext())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                                Toast.makeText(getActivity(), "ur not logged in" + e.toString(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            removePoznavacka(userID, documentID, position);

                            //local change
                            MyListsFragment.sPoznavackaInfoArr.get(position).setUploaded(false);
                            MyListsFragment.getSMC(getContext()).updatePoznavackaFile("poznavacka.txt", MyListsFragment.sPoznavackaInfoArr);

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
                    Toast.makeText(getContext(), "reconnect!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayFirestore(final View view) {
        Timber.d("DisplayFirestore");
        db.collection("Users")
                .get()
                .continueWithTask(new Continuation<QuerySnapshot, Task<List<QuerySnapshot>>>() {
                    @Override
                    public Task<List<QuerySnapshot>> then(@NonNull Task<QuerySnapshot> task) {
                        List<Task<QuerySnapshot>> tasks = new ArrayList<Task<QuerySnapshot>>();
                        for (DocumentSnapshot ds : task.getResult()) {
                            tasks.add(ds.getReference().collection("Poznavacky").get());
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
                                    arrayList.add(new PreviewPoznavacka(ds.getString("headImageUrl"), ds.getString("name"), ds.getString("id"), ds.getString("authorsName"), ds.getString("authorsID")));
                                }
                            }
                        }
                        buildRecyclerView(view);
                    }
                });






/*                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot queryDocumentSnapshot :
                                    Objects.requireNonNull(task.getResult())) {
                                Timber.d("DisplayFirestore queryDocumentSnapshot = %s", queryDocumentSnapshot);
                            }


                            Timber.d("DisplayFirestore successfully connected");
                            if (task.getResult() == null) {
                                Log.d(TAG, "DisplayFirestore null log");
                                Timber.d("DisplayFirestore null Result");
                            } else {
                                Log.d(TAG, "DisplayFirestore result coming.. log");
                                Timber.d("DisplayFirestore result coming..");
                                //LEFT OFF, getDocuments() nevraci nic
                                Timber.d("DisplayFirestore result = %s", task.getResult().size());
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    Timber.d("DisplayFirestore Current doc = %s", document.getId());
                                    QuerySnapshot querySnapshot = document.getReference().collection("Poznavacky").get().getResult();
                                    if (querySnapshot != null) { //if user has created any poznavacka
                                        for (QueryDocumentSnapshot doc :
                                                querySnapshot) {
                                            arrayList.add(new PreviewPoznavacka(doc.getString("headImageUrl"), doc.getString("name"), doc.getId(), doc.getString("authorsName"), doc.getString("authorsID")));
                                        }
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(getActivity(), "error displaying", Toast.LENGTH_SHORT).show();
                        }
                        buildRecyclerView(view);

                    }
                });*/
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
                    Toast.makeText(getActivity(), documentSnapshot.get("name").toString(), Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
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
        CollectionReference dbPoznavacka = FirebaseFirestore.getInstance().collection("Users");
        final DocumentReference dbUser = dbPoznavacka.document(userID);
        CollectionReference dbPoznavacky = dbUser.collection("Poznavacky");

        dbPoznavacky.document(data.getId()).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                //so the userDocument is not abstract and can be accessed later
                Map<String, String> dummyDataNeededforAccess = new HashMap<>();
                dummyDataNeededforAccess.put("dummy", "neededToFunction");
                dbUser.set(dummyDataNeededforAccess);

                //adding locally
                arrayList.add(new PreviewPoznavacka(data.getHeadImageUrl(), data.getName(), data.getId(), data.getAuthorsName(), data.getAuthorsID()));
                mSharedListAdapter.notifyDataSetChanged();
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

        private WeakReference<SharedListsFragment> fragmentWeakReference;

        DrawableFromUrlAsync(SharedListsFragment context) {
            fragmentWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Drawable doInBackground(Void... voids) {
            SharedListsFragment fragment = fragmentWeakReference.get();
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
                    MyListsFragment.getSMC(fragment.getContext()).saveDrawable(returnDrawable, path, z.getParameter(0));
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
            return new BitmapDrawable(Objects.requireNonNull(getContext()).getResources(), bitmap);
        }
    }
}