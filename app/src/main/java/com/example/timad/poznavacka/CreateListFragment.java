package com.example.timad.poznavacka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class CreateListFragment extends Fragment {
    private static final String TAG = "CreateListFragment";

    private static final String KEY_TITLE = "title";
    private static final String KEY_RAD = "rad";
    private static final String KEY_DRUH = "druh";
    private static final String KEY_ZASTUPCE = "poznavackaInfo";
    private static final String KEY_IMGREF = "imageRef";

    private Button btnCREATE;
    private EditText userInputTitle;
    private EditText userInputRepresentatives;
    private EditText userDividngString;
    private Switch autoRadDruhSwitch;

    private FirestoreImpl firestoreImpl;
    private FirebaseFirestore db; //for testing

    private ArrayList<String> representatives;
    private String title;
    private String dividingString;

    public static String testString;

    /* RecyclerView */
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLManager;
    private ArrayList<Zastupce> mZastupceArr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createlist, container, false);
        firestoreImpl = new FirestoreImpl();
        btnCREATE = view.findViewById(R.id.createButton);
        userInputRepresentatives = view.findViewById(R.id.userInputRepresentatives);
        userInputTitle = view.findViewById(R.id.userInputTitle);
        autoRadDruhSwitch = view.findViewById(R.id.autoRadDruhSwitch);
        db = FirebaseFirestore.getInstance(); //testing

/*
        //jenom testovani
        new JSoup().execute("Pes_domácí");
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                Log.d(TAG, testString);
            }

        }, 2000);*/

        /* RecyclerView */
        mZastupceArr = new ArrayList<>();
        mZastupceArr.add(new Zastupce("Nazev", "Druh", "Kmen"));
        mZastupceArr.add(new Zastupce("Plejtvak", "Ploutvoviti", "Ryba"));

        mRecyclerView = view.findViewById(R.id.recyclerViewZ);
        mRecyclerView.setHasFixedSize(false);
        mLManager = new LinearLayoutManager(getContext());
        mAdapter = new ZastupceAdapter(mZastupceArr);

        mRecyclerView.setLayoutManager(mLManager);
        mRecyclerView.setAdapter(mAdapter);

        btnCREATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = userInputTitle.getText().toString();
                dividingString = userDividngString.getText().toString();
                String rawRepresentatives = userInputRepresentatives.getText().toString();

                representatives = new ArrayList<>(Arrays.asList(rawRepresentatives.split("\\s*" + dividingString + "\\s*")));

                for (int i = 0; i < representatives.size(); i++) {

                    //uploading poznavacka
                    Map<String, Object> representativeInfo = new HashMap<>();
                    representativeInfo.put(KEY_ZASTUPCE, representatives.get(i));
                    representativeInfo.put(KEY_IMGREF, "imageRef - cislo/hash?");
                    if (autoRadDruhSwitch.isChecked()) {
                        representativeInfo.put(KEY_DRUH, "dohledany druh");
                        representativeInfo.put(KEY_RAD, "dohledany rad");
                    }

                    firestoreImpl.uploadRepresentative(title, representatives.get(i), representativeInfo);

                }


                Toast.makeText(getActivity(), "CREATE BUTTON CLICKED", Toast.LENGTH_SHORT).show();
            }
        });


        //test

        // Create a new poznavackaInfo with a reference to an image
        /*Map<String, Object> zkouska1 = new HashMap<>();
        zkouska1.put(KEY_TITLE, "zkouskaTitle");
        zkouska1.put("poznavackaInfo", "zkouskaZástupce1");
        zkouska1.put("druh", "zkouskaDruh1");
        zkouska1.put("rad", "zkouskaŘád1");
        zkouska1.put("imgRef", 265);*/


        //firestoreImpl.uploadRepresentative(zkouska1, "poznavackaExample");
        //firestoreImpl.readData("poznavackaExample");
        return view;
    }
}