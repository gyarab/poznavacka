package com.example.timad.poznavacka.activities.lists;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.timad.poznavacka.DBTestObject;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.TestAdapter;
import com.example.timad.poznavacka.PreviewTestObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyTestActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    static private TestAdapter mTestAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static ArrayList<PreviewTestObject> previewTestObjectArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_test);

        String  userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //TODO recycler view doesnt work yet, will work soon
        if(SharedListsActivity.checkInternet(getApplicationContext())){
            try {
                buildTestActivity(userID);
                Toast.makeText(this, "Tests were build", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    //current focus
    private void buildRecyclerView(ArrayList<PreviewTestObject> previewTestObjectArrayList1){
        mRecyclerView = findViewById(R.id.testView);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mTestAdapter = new TestAdapter(previewTestObjectArrayList1);
        mRecyclerView.setAdapter(mTestAdapter);

    }
    private  void createArrayList(){
        previewTestObjectArrayList =new ArrayList<>();
    }
    private void fetchTests(final String userID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PreviewTestObject test =new PreviewTestObject(document.getString("name"),document.getBoolean("started"),document.getString("previewImageUrl"),document.getId(),document.getString("userID"),document.getString("content"));
                                String x = test.toString();
                                Toast.makeText(getApplicationContext(),x,Toast.LENGTH_LONG).show();
                                previewTestObjectArrayList.add(test);

                            }
                        }

                    }
                });


    }

    private void buildTestActivity(String userID) {
              createArrayList();
             // int i = testObjectArrayList.size();

              //Toast.makeText(this,i+"",Toast.LENGTH_LONG).show();
              fetchTests(userID);
              buildRecyclerView(previewTestObjectArrayList);
    }











    // in plan to be done later or already finished
    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(getApplication(), MyListsActivity.class);
        startActivity(intent1);
        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
        finish();
    }
    public static void addToTests(final String userID,final DBTestObject data ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection(userID)
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            mTestAdapter.notifyDataSetChanged();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });


    }
    private void removeTest(final String userID, final String documentName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(userID).document(documentName)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                            mTestAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private void startTest() {
    }
    private void endTest(){
    }
    private void checkResults(){

    }



}
