package com.example.timad.poznavacka.activities.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.activities.lists.MyListsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class TestPINFragment extends Fragment {
    private static final String TAG = "TestPINFragment";

    private Button enterPinButton;
    private EditText pinInput;

    private  String PIN;
    public static String firebaseTestID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_pin, container, false);
        enterPinButton = view.findViewById(R.id.enter_pin_button);
        pinInput = view.findViewById(R.id.pin_input);

        enterPinButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                PIN = String.valueOf(pinInput.getText());
                checkForTest(PIN);
               // TestActivity.mViewPager.setCurrentItem(1); //if verified go wait

            }
        });


        return view;
    }

    private void goToTestActivity(){
       Intent intent = new Intent(getActivity(),TestUserActivity.class);
       startActivity(intent);
    }
    private void checkForTest(String PIN){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query test = db.collection("ActiveTest").whereEqualTo("testCode", PIN);
        test.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                firebaseTestID= document.getId();
                                goToTestActivity();
                            }
                        } else {
                            Toast.makeText(getActivity(),"cannot find test with this PIN",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }



}