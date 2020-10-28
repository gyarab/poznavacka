package com.adamec.timotej.poznavacka.activities.lists;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.adamec.timotej.poznavacka.ActiveTestObject;
import com.adamec.timotej.poznavacka.DBTestObject;
import com.adamec.timotej.poznavacka.R;
import com.adamec.timotej.poznavacka.ResultObjectDB;
import com.adamec.timotej.poznavacka.TestAdapter;
import com.adamec.timotej.poznavacka.PreviewTestObject;
import com.adamec.timotej.poznavacka.activities.test.UserResultActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

public class MyExamsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    static private TestAdapter mTestAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static ArrayList<PreviewTestObject> previewTestObjectArrayList;
    public static String currentCollectionResultID;
    public static String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_exams);
        for (int i = 0; i < 10; i++) {
            Timber.d("code = %s", HashCode());
        }

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (SharedListsActivity.checkInternet(getApplicationContext())) {
            try {
                buildTestActivity(userID);
                Toast.makeText(this, "Tests were build", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Vymaže výsledky testu
     *
     * @param userID
     * @param examID
     */
    private void clearResults(final String userID, final String examID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("Users").document(userID).collection("Exams").document(examID).collection("Results");

        colRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                deleteResults(colRef, document.getId());
                            }
                        }

                    }
                });
    }

    /**
     * Dostanetě do výsledků
     */
    private void gotToResults() {
        Intent intent = new Intent(getApplication(), UserResultActivity.class);
        startActivity(intent);
    }
    //current focus

    /**
     * postaví recykler view
     *
     * @param previewTestObjectArrayList1
     */
    private void buildRecyclerView(final ArrayList<PreviewTestObject> previewTestObjectArrayList1) {
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyExamsActivity.this);
                    builder.setTitle(R.string.app_name);
                    builder.setIcon(R.drawable.ic_delete);
                    builder.setMessage("Do you really want to delete " + previewTestObjectArrayList1.get(position).getName() + " from tests?");
                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            String userID = previewTestObjectArrayList1.get(position).getUserID();
                            String dbID = previewTestObjectArrayList1.get(position).getDatabaseID();
                            DocumentReference docRef = db.collection("Users").document(userID).collection("Exams").document(dbID);

                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    if (documentSnapshot.exists()) {
                                        if (documentSnapshot.getBoolean("finished") || !documentSnapshot.getBoolean("started")) {
                                            /*if (previewTestObjectArrayList1.get(position).isResultsEmpty()) {
                                                clearResults(userID, dbID);
                                            }*/
                                            removeTest(FirebaseAuth.getInstance().getCurrentUser().getUid(), previewTestObjectArrayList1.get(position).getDatabaseID(), position);
                                        }
                                    }
                                }
                            });


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


            /**
             * dostane tě do výsledků
             * @param position
             */
            @Override
            public void onResultsClick(final int position) {
                if (SharedListsActivity.checkInternet(getApplication())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyExamsActivity.this);
                    builder.setTitle(R.string.app_name);
                    builder.setIcon(R.drawable.ic_result);
                    builder.setMessage("Do you really want to check results of test " + previewTestObjectArrayList1.get(position).getName() + "?");
                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            String userID = previewTestObjectArrayList1.get(position).getUserID();
                            String dbID = previewTestObjectArrayList1.get(position).getDatabaseID();
                            DocumentReference docRef = db.collection("Users").document(userID).collection("Exams").document(dbID);

                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        PreviewTestObject item = previewTestObjectArrayList1.get(position);
                                        currentUserID = userID;
                                        currentCollectionResultID = documentSnapshot.getId();
                                        if (item.isResultsEmpty()) {
                                            gotToResults();
                                        }
                                    }
                                }
                            });

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

            /**
             * začne skončí test
             * @param position
             */
            @Override
            public void onStart_EndClick(final int position) {
                if (SharedListsActivity.checkInternet(getApplication())) {
                    final PreviewTestObject test = previewTestObjectArrayList1.get(position);
                    if (!test.isStarted()) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MyExamsActivity.this);
                        builder.setTitle(R.string.app_name);
                        builder.setIcon(R.drawable.ic_list_play_black_24dp);
                        builder.setMessage("Do you really want to  start test " + previewTestObjectArrayList1.get(position).getName() + "?");
                        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                String userID = previewTestObjectArrayList1.get(position).getUserID();
                                String dbID = previewTestObjectArrayList1.get(position).getDatabaseID();
                                DocumentReference docRef = db.collection("Users").document(userID).collection("Exams").document(dbID);

                                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        if (documentSnapshot.exists()) {
                                            if (documentSnapshot.getString("activeTestID").equals("")) {
                                                test.setStarted(true);
                                                String userID = test.getUserID();
                                                String documentName = test.getDatabaseID();

                                                setStarted_end(userID, documentName, position);
                                                mTestAdapter.notifyItemChanged(position);
                                                //mTestAdapter.notifyDataSetChanged();

                                                //create document in ActiveTests database
                                                String content = test.getContent();

                                                checkForHashCode(position, HashCode());

                                            }
                                        }
                                    }
                                });
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

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MyExamsActivity.this);
                        builder.setTitle(R.string.app_name);
                        builder.setIcon(R.drawable.ic_stop);
                        builder.setMessage("Do you really want to  stop test " + previewTestObjectArrayList1.get(position).getName() + "?");
                        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference docRef = db.collection(previewTestObjectArrayList1.get(position).getUserID()).document(previewTestObjectArrayList1.get(position).getDatabaseID());

                                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        if (documentSnapshot.exists()) {
                                            if (!documentSnapshot.getBoolean("finished")) {
                                                test.setFinished(true);
                                                String userID = test.getUserID();
                                                String documentName = test.getDatabaseID();

                                                setFinished(userID, documentName, position);


                                                String deactivateTest = test.getActiveTestID();

                                                mTestAdapter.notifyDataSetChanged();
                                                deactivateTest(position);

                                            }
                                        }
                                    }
                                });

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

    /**
     * vygeneruje unique kod
     *
     * @return
     */
    private String HashCode() {
        List<String> arr = new ArrayList();
        final String chars = "0123456789";
        String a = "a";
        String b = "A";
        for (char ch : chars.toCharArray()) {
            arr.add(Character.toString(ch));
        }
        Random rand = new Random();
        String code = "";
        for (int i = 0; i < 6; i++) {
            int x = rand.nextInt(chars.length());
            code += arr.get(x);
        }
        return code;
    }

    /**
     * zjistí jestli kód je unique
     *
     * @param position
     * @param code
     */
    private void checkForHashCode(final int position, final String code) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query test = db.collection("ActiveTests").whereEqualTo("testCode", code);
        test.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                checkForHashCode(position, HashCode());
                            } else {
                                StartingTestAction(position, code);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "cannot find test with this PIN", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * vytvoří pole pro objekty
     */

    private void createArrayList() {
        previewTestObjectArrayList = new ArrayList<>();
    }

    /**
     * vyvolá testy z firebase
     *
     * @param userID
     */
    private void fetchTests(final String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String classification = document.getString("classification");
                                String content = document.getString("content");
                                boolean started = document.getBoolean("started");
                                String previewImageUrl = document.getString("previewImageUrl");
                                String databaseID = document.getId();
                                String userID = document.getString("userID");
                                String name = document.getString("name");
                                boolean finished = document.getBoolean("finished");
                                String activeTestID = document.getString("activeTestID");
                                String testCode = document.getString("testCode");
                                boolean resultsEmpty = document.getBoolean("resultsEmpty");

                                PreviewTestObject test = new PreviewTestObject(name, started, previewImageUrl, databaseID, userID, classification, content, finished, activeTestID, testCode, resultsEmpty);
                                previewTestObjectArrayList.add(test);

                            }
                            buildRecyclerView(previewTestObjectArrayList);
                        }

                    }
                });


    }

    /**
     * dá vědět testu, že byla založená databáze testů
     *
     * @param userID
     * @param testID
     * @param context
     */
    public static void updateResultsEmpty(String userID, String testID, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Toast.makeText(context, userID + "," + testID, Toast.LENGTH_LONG).show();
        DocumentReference docRef = db.collection(userID).document(testID);
        docRef.update(
                "resultsEmpty", true
        );

    }

    /**
     * sestaví dohromady testovací aktivitu
     *
     * @param userID
     */
    private void buildTestActivity(String userID) {
        createArrayList();
        fetchTests(userID);
    }

    /**
     * dá vědět fireabase, že test je ukončen
     *
     * @param userID
     * @param documentName
     * @param position
     */
    private void setFinished(String userID, String documentName, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection(userID).document(documentName);
        docRef.update(
                "finished", previewTestObjectArrayList.get(position).isFinished()
        );

    }

    /**
     * dá věědět firebase, že test se začal
     *
     * @param userID
     * @param documentName
     * @param position
     */
    private void setStarted_end(String userID, String documentName, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(userID).collection("Exams").document(documentName);

        docRef.update(
                "started", previewTestObjectArrayList.get(position).isStarted()
        );
    }

    /**
     * přidá test do aktivních testů
     *
     * @param data
     * @param position
     */
    private void addToActiveTests(ActiveTestObject data, final int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("ActiveTests")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String activateTestID = documentReference.getId();

                        updateActiveTestID(userID, activateTestID, position);
                        previewTestObjectArrayList.get(position).setActiveTestID(activateTestID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    /**
     * začne testovací akci
     *
     * @param position
     * @param code
     */
    private void StartingTestAction(final int position, final String code) {
      /*  final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("CodeCounter").document("6UkZGRkDj3yKPJdwV5dn");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                TestCodeObject data = documentSnapshot.toObject(TestCodeObject.class);*/

        String classification = previewTestObjectArrayList.get(position).getClassification();
        String content = previewTestObjectArrayList.get(position).getContent();
        String userID = previewTestObjectArrayList.get(position).getUserID();
        String testDBID = previewTestObjectArrayList.get(position).getDatabaseID();

        ActiveTestObject test = new ActiveTestObject(classification, content, code, userID, testDBID);
        addToActiveTests(test, position);
        setTestCodeTestDB(code, userID, testDBID);
        PreviewTestObject item = previewTestObjectArrayList.get(position);
        item.setTestCode(code);
        mTestAdapter.notifyDataSetChanged();
    }

    /**
     * @param testCode
     * @param userID
     * @param documentName
     */
    private void setTestCodeTestDB(final String testCode, String userID, String documentName) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(userID).collection("Exams").document(documentName);
        docRef.update(
                "testCode", testCode
        );

    }

    /**
     * přidá otkaz testu an jeho aktivační odkaz
     *
     * @param userID
     * @param activeTestID
     * @param position
     */
    private void updateActiveTestID(String userID, String activeTestID, int position) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String documentName = previewTestObjectArrayList.get(position).getDatabaseID();
        DocumentReference docRef = db.collection("Users").document(userID).collection("Exams").document(documentName);
        docRef.update(
                "activeTestID", activeTestID
        );
    }

    /**
     * deaktivuje test
     *
     * @param position
     */
    private void deactivateTest(final int position) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("ActiveTests").document(previewTestObjectArrayList.get(position).getActiveTestID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    /**
     * prida výsledek testu do databáze
     *
     * @param activeTestID
     * @param data
     * @param userID
     * @param databaseTestID
     */
    public static void addResultToDB(final String activeTestID, final ResultObjectDB data, final String userID, final String databaseTestID) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection(userID).document(databaseTestID);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    boolean finished = documentSnapshot.getBoolean("finished");

                    if (!finished) {
                        Query userExists = db.collection(databaseTestID).whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        userExists.get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().size() == 0) {
                                                db.collection(databaseTestID)
                                                        .add(data)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {

                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });
                                            }
                                        }
                                    }
                                });
                    }
                }
            }
        });

    }

    /**
     * smaže výsledky v databázi
     *
     * @param colRef
     * @param documentName
     */
    public void deleteResults(CollectionReference colRef, String documentName) {
        colRef.document(documentName)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    // in plan to be done later or already finished

    /**
     * akce provedená při zmačknutí vracecího se tlačítka na telefonu
     */
    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(getApplication(), MyListsActivity.class);
        startActivity(intent1);
        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
        finish();
    }

    /**
     * prida test do tesů
     *
     * @param userID
     * @param data
     */
    public static void addToTests(final String userID, final DBTestObject data) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(userID)
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }

    /**
     * smaže test
     *
     * @param userID
     * @param examID
     * @param position
     */
    private void removeTest(final String userID, final String examID, final int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(userID).collection("Exams").document(examID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        previewTestObjectArrayList.remove(position);
                        mTestAdapter.notifyItemRemoved(position);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void checkResults() {

    }


}
