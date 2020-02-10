package com.example.timad.poznavacka.activities.lists;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timad.poznavacka.PreviewPoznavacka;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.SharedListAdapter;
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

import java.util.ArrayList;

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
        //vyt vori array prida do arraye vytvori recykelr view
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
                try {
                    Toast.makeText(getActivity(), documentSnapshot.get("name").toString(), Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
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
        });
    }

    private void displayFirestore(final String collectionName, final View view) {
        db.collection(collectionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    arrayList.add(new PreviewPoznavacka(R.drawable.ic_image, document.getString("name"), document.getId()));
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
    public void removePoznavacka(String documentName, String collectionName, String authorsName) {
        db.collection(collectionName).document(documentName)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
        mSharedListAdapter.notifyDataSetChanged();

    }

    static void addToFireStore(String CollectionName, final PoznavackaDbObject data, FirebaseFirestore db) {
        CollectionReference dbPoznavacka = db.collection(CollectionName);

        dbPoznavacka.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String docRef = documentReference.getId();
                arrayList.add(new PreviewPoznavacka(R.drawable.ic_image, data.getName(), docRef));
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