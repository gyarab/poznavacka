package com.example.timad.poznavacka.activities.test;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.ResultObjectDB;
import com.example.timad.poznavacka.activities.lists.MyListsActivity;
import com.example.timad.poznavacka.activities.lists.SharedListsActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TestUserActivity extends AppCompatActivity {
    private Button finishTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_user);

        if(SharedListsActivity.checkInternet(this)){
            finishTest=findViewById(R.id.finishTest3);
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
                TextView content = findViewById(R.id.content3);
                content.setText(documentSnapshot.getString("content"));
                TextView result = findViewById(R.id.result3);
                result.setText("17/20");
                final ResultObjectDB item =  new ResultObjectDB(FirebaseAuth.getInstance().getCurrentUser().getUid(),result.getText().toString());
                /*
                finishTest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyTestActivity.addResultToDB(documentSnapshot.getId(),item);
                    }
                });
                */
            }
            });
    }
    private void finishTest(){


    }

}
