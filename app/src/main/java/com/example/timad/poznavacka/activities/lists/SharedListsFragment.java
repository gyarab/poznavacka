package com.example.timad.poznavacka.activities.lists;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timad.poznavacka.PoznavackaInfo;
import com.example.timad.poznavacka.PreviewPoznavacka;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.SharedListAdapter;
import com.example.timad.poznavacka.Zastupce;
import com.example.timad.poznavacka.activities.test.PoznavackaDbObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SharedListsFragment extends Fragment {
    private static final String TAG = "SharedFragment";

    // shared list starts here
    private EditText searchView;
    private RecyclerView mRecyclerView;
    static private SharedListAdapter mSharedListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static ArrayList<PreviewPoznavacka> arrayList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sharedlists, container, false);
        db = FirebaseFirestore.getInstance();
        //vyt vori array prida do arraye vytvori recykler view
        createArr();
        displayFirestore("Poznavacka", view);

        searchView = view.findViewById(R.id.search_view);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mSharedListAdapter != null) {
                    mSharedListAdapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    //vytvori arraylist
    static private void createArr() {
        arrayList = new ArrayList<>();
    }

    private void pickDocument(final String documentId, String collectionName) {
        DocumentReference docRef = db.collection(collectionName).document(documentId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                PoznavackaDbObject item = documentSnapshot.toObject(PoznavackaDbObject.class);

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

                if(! MyListsFragment.getSMC(getContext()).createAndWriteToFile(path, item.getId(), item.getContent())){
                    Toast.makeText(getActivity(), "Failed to save " + item.getName(), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Gson gson = new Gson();
                    Type cType = new TypeToken<ArrayList<Zastupce>>(){}.getType();
                    ArrayList<Zastupce> zastupceArr = gson.fromJson(item.getContent(), cType);
                    for(Zastupce z: zastupceArr) {
                        if(!(z.getImageURL() == null || z.getImageURL().isEmpty()))
                            // TODO finish
                            //MyListsFragment.getSMC(context).saveDrawable(WikiSearchRepresentativesCopy(z.getImageURL()), path, item.getId());
                            Log.d("Saving", "Image saved: " + z.getImageURL());
                    }
                }

                String pathPoznavacka = "poznavacka.txt";
                if (MyListsFragment.sPoznavackaInfoArr == null) {
                    MyListsFragment.getSMC(getContext()).readFile(pathPoznavacka, true);
                }
                MyListsFragment.sPoznavackaInfoArr.add(new PoznavackaInfo(item.getName(), item.getId(), item.getAuthorsName()));
                MyListsFragment.getSMC(getContext()).updatePoznavackaFile(pathPoznavacka, MyListsFragment.sPoznavackaInfoArr);

                Log.d("Files", "Saved successfully");
                Toast.makeText(getActivity(), "Successfully saved " + item.getName(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void buildRecyclerView(View view, final String collectionName) {
        mRecyclerView = view.findViewById(R.id.downloadView);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getContext());
        mSharedListAdapter = new SharedListAdapter(arrayList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mSharedListAdapter);
        mSharedListAdapter.setOnItemClickListener(new SharedListAdapter.OnItemClickListener() {

            @Override
            public void onDownloadClick(int position) {

                String id = arrayList.get(position).getId();
                pickDocument(id, collectionName);

            }

            @Override
            public void onDeleteClick(int position) {
                String id = arrayList.get(position).getId();
                String authorsName = "author";
                //TODO

                removePoznavacka(id,collectionName,authorsName,position);

            }
        });
    }

    /* TODO finish
    private static class WikiSearchRepresentativesCopy extends AsyncTask<Void, String, Void> {

        private WeakReference<SharedListsFragment> fragmentWeakReference;

        Drawable drawableFromUrl(String url) {
            SharedListsFragment fragment = fragmentWeakReference.get();
            try {

                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestProperty("User-agent", "Mozilla");

                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return new BitmapDrawable(Objects.requireNonNull(fragment.getContext()).getResources(), bitmap);
            } catch (IOException e){
                Toast.makeText(fragment.getActivity(), "Something went wrong while downloading Poznavacka", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }
    }*/

    private void displayFirestore(final String collectionName, final View view) {
        db.collection(collectionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    arrayList.add(new PreviewPoznavacka(R.drawable.ic_image, document.getString("name"), document.getId(),document.getString("authorsName")));
                                }
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                        }
                        buildRecyclerView(view, collectionName);

                    }
                });
    }

    //nedodelane
    public void sharePoznavacka(String collectionName, final String collecionShareName, String documentShareId) {
        DocumentReference docRef = db.collection(collectionName).document(documentShareId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                PoznavackaDbObject item = documentSnapshot.toObject(PoznavackaDbObject.class);
                addToFireStore(collecionShareName, item, db);

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
    public void removePoznavacka(final String documentName, final String collectionName, final String authorsName,final int position) {
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                PoznavackaDbObject item = documentSnapshot.toObject(PoznavackaDbObject.class);
                if(item.getAuthorsName().equals(authorsName)) {

                    db.collection(collectionName).document(documentName)
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

                }else{
                    Toast.makeText(getActivity(),"ur user name doesnt match creator"+","+item.getAuthorsName()+","+authorsName, Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    static void addToFireStore(String CollectionName, final PoznavackaDbObject data, FirebaseFirestore db) {
        CollectionReference dbPoznavacka = db.collection(CollectionName);

        dbPoznavacka.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String docRef = documentReference.getId();
                arrayList.add(new PreviewPoznavacka(R.drawable.ic_image, data.getName(), docRef,data.getAuthorsName()));
                mSharedListAdapter.notifyDataSetChanged();
                //   Toast.makeText(getActivity(),"added!",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
            }
        });
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
}