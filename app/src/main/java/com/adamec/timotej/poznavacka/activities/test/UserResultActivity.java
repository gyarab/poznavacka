package com.adamec.timotej.poznavacka.activities.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.adamec.timotej.poznavacka.PreviewResultObject;
import com.adamec.timotej.poznavacka.R;
import com.adamec.timotej.poznavacka.ResultAdapter;
import com.adamec.timotej.poznavacka.activities.lists.MyExamsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserResultActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    static private ResultAdapter mTestAdapter;
    private androidx.recyclerview.widget.RecyclerView.LayoutManager mLayoutManager;
    private static ArrayList<PreviewResultObject> previewResultObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_result);
        fetchResults(MyExamsActivity.currentUserID, MyExamsActivity.currentCollectionResultID);
    }
    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(getApplication(), MyExamsActivity.class);
        startActivity(intent1);
        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
        finish();
    }
    private void createArr(){
        previewResultObjects = new ArrayList<>();

    }

    private void buildRecyclerview() {
        mRecyclerView = findViewById(R.id.resultView);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mTestAdapter = new ResultAdapter(previewResultObjects);
        mRecyclerView.setAdapter(mTestAdapter);

        mTestAdapter.notifyDataSetChanged();
    }

    private void fetchResults(String userID, String examID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userID).collection("Exams").document(examID).collection("Results")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            createArr();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userID = document.getString("userID");
                                    String result = document.getString("result");
                                    String userName = document.getString("userName");
                                    String databaseID = document.getId();
                                    PreviewResultObject item = new PreviewResultObject(userID,result,databaseID,userName);
                                    previewResultObjects.add(item);
                                }
                                buildRecyclerview();
                            }

                        }
                    });
        }
    }
