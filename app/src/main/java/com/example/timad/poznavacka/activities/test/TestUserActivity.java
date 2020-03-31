package com.example.timad.poznavacka.activities.test;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timad.poznavacka.AnswerObject;
import com.example.timad.poznavacka.ExamsAdapter;
import com.example.timad.poznavacka.PreviewTestObject;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.ResultObjectDB;
import com.example.timad.poznavacka.TestAdapter;
import com.example.timad.poznavacka.Zastupce;
import com.example.timad.poznavacka.activities.lists.MyExamsActivity;
import com.example.timad.poznavacka.activities.lists.MyListsActivity;
import com.example.timad.poznavacka.activities.lists.SharedListsActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TestUserActivity extends AppCompatActivity {
    private Button finishTest;
    private Button next;
    private Button previous;
    private ArrayList<Zastupce> zastupces;
    private int index;
    private int first;
    private int last;
    //recyclerview
    private RecyclerView mRecyclerView;
    static private ExamsAdapter mExamsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static ArrayList<AnswerObject> answerObjectArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_user);


        if(SharedListsActivity.checkInternet(this)){
            next= findViewById(R.id.next3);
            previous = findViewById(R.id.previous3);
            finishTest = findViewById(R.id.finishTest3);
            loadContent(TestPINFragment.firebaseTestID);
        }
    }
    @Override
    public void onBackPressed() {
        if (SharedListsActivity.checkInternet(getApplication())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(TestUserActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.drawable.ic_warning);
            builder.setMessage("Do you really want to leave test, all progress will be lost!");
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
        Intent intent1 = new Intent(getApplication(), MyListsActivity.class);
        startActivity(intent1);
        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
        finish();

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
    private void loadContent(final String documentName){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("ActiveTest").document(documentName);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                initializeZastupces(documentSnapshot.getString("content"));
                index=0;
                first = index;
                if(zastupces.get(0).getParameter(0).isEmpty()){
                    index++;
                    first = index;
                }
                testViewer(index,last,first);
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       index++;
                       testViewer(index,last,first);
                    }
                });
                previous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        index--;
                        testViewer(index,last,first);
                    }
                });
                finishTest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView result = findViewById(R.id.result3);
                        result.setText("17/20");
                        final ResultObjectDB item =  new ResultObjectDB(FirebaseAuth.getInstance().getCurrentUser().getUid(),result.getText().toString());
                        MyExamsActivity.updateResultsEmpty(documentSnapshot.getString("userID"),documentSnapshot.getString("testDBID"),getApplicationContext());
                        MyExamsActivity.addResultToDB(documentSnapshot.getId(),item);
                    }
                });





            }
            });
    }
    private void createArr(){
        zastupces=new ArrayList<>();
    }
    private void initializeZastupces(String content){
        createArr();
        Gson gson = new Gson();
        Type cType = new TypeToken<ArrayList<Zastupce>>() {
        }.getType();
        zastupces = gson.fromJson(content, cType);
        last = zastupces.size()-1;
    }
    private void testViewer(int index,int last,int first) {
        if(first!=0){
            setResults(index);
            buildRecyclerView();
        }
        Zastupce item = zastupces.get(index);
        String imageUrl = item.getImageURL();
        ImageView img = findViewById(R.id.zastupceImage3);
        Picasso.get().load(imageUrl).resize(500,500).onlyScaleDown().centerInside().error(R.drawable.ic_image).into(img);
        // previous button
        if (first == index) {
            previous.setEnabled(false);
        } else {
            previous.setEnabled(true);
        }
        //finish test button
        if (index == last) {
            finishTest.setEnabled(true);
        } else {
            finishTest.setEnabled(false);
        }
        //next button
        if(index==last){
            next.setEnabled(false);
        }else{
            next.setEnabled(true);
        }

    }

    private void buildRecyclerView(){
        mRecyclerView = findViewById(R.id.examView3);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mExamsAdapter = new ExamsAdapter(answerObjectArrayList);
        mRecyclerView.setAdapter(mExamsAdapter);

        mExamsAdapter.notifyDataSetChanged();

    }
    private void createAnswerArr(){
        answerObjectArrayList=new ArrayList<>();
    }

    private void setResults(int index){
        createAnswerArr();

        for(int counter=0;counter<zastupces.get(index).getParameters();counter++){

            String fieldName = zastupces.get(0).getParameter(counter);
            String result = zastupces.get(index).getParameter(counter);

            if(counter==0){
                fieldName="Název";
            }
            AnswerObject item = new AnswerObject(result,fieldName);
            //AnswerObject item = new AnswerObject("","");
            answerObjectArrayList.add(item);
        }





    }
    /*
     TextView content = findViewById(R.id.content3);
                initializeZastupces(documentSnapshot.getString("content"));
                content.setText(documentSnapshot.getString("content"));
                TextView result = findViewById(R.id.result3);
                result.setText("17/20");
                final ResultObjectDB item =  new ResultObjectDB(FirebaseAuth.getInstance().getCurrentUser().getUid(),result.getText().toString());
                finishTest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyExamsActivity.addResultToDB(documentSnapshot.getId(),item);
                    }
                });

     */

}
