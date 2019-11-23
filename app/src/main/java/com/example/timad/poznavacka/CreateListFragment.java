package com.example.timad.poznavacka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class CreateListFragment extends Fragment {
    private static final String TAG = "CreateFragment";

    private Button btnTEST;

    FirestoreImpl firestoreImpl;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createlist, container, false);
        btnTEST = (Button) view.findViewById(R.id.btnTEST);

        btnTEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "TESTING BUTTON CLICK 1", Toast.LENGTH_SHORT).show();
            }
        });


        // Create a new zastupce
        Map<String, String> zkouska = new HashMap<>();
        zkouska.put("zastupce", "zkouskaZástupce");
        zkouska.put("druh", "zkouskaDruh");
        zkouska.put("rad", "zkouskaŘád");

        // Create a new zastupce with a reference to an image
        Map<String, String> zkouska1 = new HashMap<>();
        zkouska1.put("zastupce", "zkouskaZástupce");
        zkouska1.put("druh", "zkouskaDruh");
        zkouska1.put("rad", "zkouskaŘád");

        firestoreImpl.uploadFile(zkouska, db);
        firestoreImpl.uploadFile(zkouska1, db);


        return view;
    }
}