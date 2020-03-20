package com.example.timad.poznavacka.activities.lists;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.timad.poznavacka.DBTestObject;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.RWAdapter;
import com.example.timad.poznavacka.TestAdapter;
import com.example.timad.poznavacka.PreviewTestObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private void buildRecyclerView(final ArrayList<PreviewTestObject> previewTestObjectArrayList1){
        mRecyclerView = findViewById(R.id.testView);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mTestAdapter = new TestAdapter(previewTestObjectArrayList1);
        mRecyclerView.setAdapter(mTestAdapter);

        mTestAdapter.notifyDataSetChanged();
        mTestAdapter.setOnItemClickListener(new TestAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(final int position) {
                if (SharedListsActivity.checkInternet(getApplication())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyTestActivity.this);
                    builder.setTitle(R.string.app_name);
                    builder.setIcon(R.drawable.ic_delete);
                    builder.setMessage("Do you really want to delete " + previewTestObjectArrayList1.get(position).getName() + " from tests?");
                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            removeTest(FirebaseAuth.getInstance().getCurrentUser().getUid(),previewTestObjectArrayList1.get(position).getDatabaseID(),position);


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



            @Override
            public void onResultsClick(int position) {
                if (SharedListsActivity.checkInternet(getApplication())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyTestActivity.this);
                    builder.setTitle(R.string.app_name);
                    builder.setIcon(R.drawable.ic_result);
                    builder.setMessage("Do you really want to check results of test " + previewTestObjectArrayList1.get(position).getName() +"?");
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

                } else {
                    Toast.makeText(getApplication(), "reconnect!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStart_EndClick(final int position) {
                if (SharedListsActivity.checkInternet(getApplication())) {
                    final PreviewTestObject test = previewTestObjectArrayList1.get(position);
                    if (!test.isStarted()) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MyTestActivity.this);
                        builder.setTitle(R.string.app_name);
                        builder.setIcon(R.drawable.ic_list_play_black_24dp);
                        builder.setMessage("Do you really want to  start test " + previewTestObjectArrayList1.get(position).getName() + "?");
                        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                test.setStarted(true);
                                String userID = test.getUserID();
                                String documentName = test.getDatabaseID();

                                setStarted_end(userID, documentName, position);
                                mTestAdapter.notifyDataSetChanged();
                                //create document in ActiveTests database

                                //create collection userID+databaseID for user results

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

                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(MyTestActivity.this);
                        builder.setTitle(R.string.app_name);
                        builder.setIcon(R.drawable.ic_stop);
                        builder.setMessage("Do you really want to  stop test " + previewTestObjectArrayList1.get(position).getName() + "?");
                        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                test.setFinished(true);
                                String userID = test.getUserID();
                                String documentName = test.getDatabaseID();

                                setFinished(userID,documentName,position);
                                mTestAdapter.notifyDataSetChanged();

                                //2 option load results to userID+userID as array of objects


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
        });

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

                               String content = document.getString("content");
                               boolean started = document.getBoolean("started");
                               String previewImageUrl = document.getString("previewImageUrl");
                               String databaseID = document.getId();
                               String userID = document.getString("userID");
                               String name = document.getString("name");
                               boolean finished = document.getBoolean("finished");
                               // String name = "lev";
                              //  boolean started = false;
                              //  String previewImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/73/Lion_waiting_in_Namibia.jpg/1200px-Lion_waiting_in_Namibia.jpg";
                              //  String databaseID = document.getId();
                              //  String content = "xd";
                              //  String userID= "xd";
                                PreviewTestObject test = new PreviewTestObject(name,started,previewImageUrl,databaseID,userID,content,finished);
                                previewTestObjectArrayList.add(test);

                            }
                            buildRecyclerView(previewTestObjectArrayList);
                        }

                    }
                });


    }

    private void buildTestActivity(String userID) {
              createArrayList();
              fetchTests(userID);
    }

    private void setFinished(String userID,String documentName,int position){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection(userID).document(documentName);
      docRef.update(
              "finished", previewTestObjectArrayList.get(position).isFinished()
      );

    }
    private void setStarted_end(String userID,String documentName,int position){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection(userID).document(documentName);
        docRef.update(
                "started", previewTestObjectArrayList.get(position).isStarted()
        );

    }
    private void addToActiveTests(){

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
    private void removeTest(final String userID, final String documentName, final int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(userID).document(documentName)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                           previewTestObjectArrayList.remove(position);
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
