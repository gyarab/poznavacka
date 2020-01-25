package com.example.timad.poznavacka.activities.lists;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.timad.poznavacka.FirestoreImpl;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.Zastupce;
import com.example.timad.poznavacka.ZastupceAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

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
    private Button btnSAVE;
    private ProgressBar progressBar;
    private EditText userInputTitle;
    private EditText userInputRepresentatives;
    private EditText userDividngString;
    private Switch autoImportSwitch;

    private boolean switchPressedOnce;
    private boolean loadingRepresentative;
    private boolean autoImportIsChecked;

    private FirestoreImpl firestoreImpl;
    private FirebaseFirestore db; //for testing

    private ArrayList<String> representatives;
    private String title;
    private String dividingString;

    //RecyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLManager;
    private ArrayList<Zastupce> mZastupceArr;

    private int parameters;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createlist, container, false);
        firestoreImpl = new FirestoreImpl();
        btnCREATE = view.findViewById(R.id.createButton);
        btnSAVE = view.findViewById(R.id.saveButton);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        userInputRepresentatives = view.findViewById(R.id.userInputRepresentatives);
        userInputTitle = view.findViewById(R.id.userInputTitle);
        autoImportSwitch = view.findViewById(R.id.autoImportSwitch);
        userDividngString = view.findViewById(R.id.dividingCharacter);
        db = FirebaseFirestore.getInstance(); //testing
        switchPressedOnce = false;


        /* RecyclerView */
        mRecyclerView = view.findViewById(R.id.recyclerViewZ);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = (int) ((float) displayMetrics.heightPixels * 0.7f);

        //from https://stackoverflow.com/questions/19805981/android-layout-view-height-is-equal-to-screen-size
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams();
        params.height = height;
        mRecyclerView.setLayoutParams(new RelativeLayout.LayoutParams(params));

        mZastupceArr = new ArrayList<>();
        /*mZastupceArr.add(new Zastupce(3, "Nazev", "Druh", "Kmen"));
        mZastupceArr.add(new Zastupce(3, "Plejtvak", "Ploutvoviti", "Ryba"));
        mZastupceArr.add(new Zastupce(3, "Nazev", "Druh", "Kmen"));*/

        mLManager = new LinearLayoutManager(getContext());
        //mAdapter = new ZastupceAdapter(mZastupceArr, PopActivity.userParametersCount); // Melo by se nastavit podle poctu parametru poznavacky --> PopActivity


        autoImportSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchPressedOnce = true;
                    autoImportIsChecked = true;
                    //mAdapter.setmParameters(PopActivity.userParametersCount);
                    Intent intent = new Intent(getContext(), PopActivity.class);
                    startActivity(intent);
                } else {
                    autoImportIsChecked = false;
                }
            }
        });

        btnCREATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(), "CREATE BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                final WikiSearch wikiSearch = new WikiSearch(CreateListFragment.this);

                //recyclerView getting user parameters into account
                if (autoImportIsChecked) {
                    mAdapter = new ZastupceAdapter(mZastupceArr, PopActivity.userParametersCount);
                    parameters = PopActivity.userParametersCount;
                } else {
                    mAdapter = new ZastupceAdapter(mZastupceArr, 1);
                    parameters = 1;
                }
                mRecyclerView.setLayoutManager(mLManager);
                mRecyclerView.setAdapter(mAdapter);

                wikiSearch.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                //wikiSearch.execute();
            }
        });


        btnSAVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = userInputTitle.getText().toString();
                dividingString = userDividngString.getText().toString();
                String rawRepresentatives = userInputRepresentatives.getText().toString();

                representatives = new ArrayList<>(Arrays.asList(rawRepresentatives.split("\\s*" + dividingString + "\\s*")));

          /*      for (int i = 0; i < representatives.size(); i++) {

                    //uploading poznavacka
                    Map<String, Object> representativeInfo = new HashMap<>();
                    representativeInfo.put(KEY_ZASTUPCE, representatives.get(i));
                    representativeInfo.put(KEY_IMGREF, "imageRef - cislo/hash?");
                    if (autoImportSwitch.isChecked()) {
                        representativeInfo.put(KEY_DRUH, "dohledany druh");
                        representativeInfo.put(KEY_RAD, "dohledany rad");
                    }

                    firestoreImpl.uploadRepresentative(title, representatives.get(i), representativeInfo);

                }*/
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


    //možná pomalý způsob, kdyztak -> https://stackoverflow.com/questions/33862336/how-to-extract-information-from-a-wikipedia-infobox
    //pridat if pokud je potreba jen obrazek
    private static class WikiSearch extends AsyncTask<Void, Void, Void> {

        private WeakReference<CreateListFragment> fragmentWeakReference;

        WikiSearch(CreateListFragment context) {
            fragmentWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final CreateListFragment fragment = fragmentWeakReference.get();

            //misto testInput -> representatives
            ArrayList<String> testInput = new ArrayList<>();
            testInput.add("Sýkora");
            testInput.add("Pes domácí");


            for (String representative :
                    testInput) {
                String searchText = representative.replace(" ", "_");

                Document doc = null;
                try {
                    //doc = Jsoup.connect("https://" + PopActivity.languageURL + ".wikipedia.org/api/rest_v1/page/html/" + URLEncoder.encode(searchText, "UTF-8")).userAgent("Mozilla").get();
                    doc = Jsoup.connect("https://" + PopActivity.languageURL + ".wikipedia.org/api/rest_v1/page/html/" + URLEncoder.encode(searchText, "UTF-8")).userAgent("Mozilla").get();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "couldn't connect to the site");
                    //fragment.mZastupceArr.add(new Zastupce("Error loading " + representative, "", ""));  // EDITED
                }

                ArrayList<String> scientificClasses = new ArrayList<>();
                ArrayList<String> newData = new ArrayList<>();
                String[] dataPair = new String[2];
                Drawable img = null;
                int dataCounter = 0;
                boolean scientificClassificationDetected = false;
                boolean imageDetected = false;
                int classificationPointer = 0;
                int trCounter = 0;

                Element infoBox = null;
                if (doc != null) {
                    infoBox = doc.getElementsByTag("table").first().selectFirst("tbody");
                    //Toast.makeText(fragment.getActivity(), "Wiki for " + representative + " could not be loaded", Toast.LENGTH_SHORT).show();
                    Elements trs = infoBox.select("tr");
                    for (Element tr :
                            trs) {
                        trCounter++;
                        Log.d(TAG, "current tr = " + trCounter);
                        if (!tr.getAllElements().hasAttr("colspan") && fragment.autoImportIsChecked && !(PopActivity.userScientificClassification.size() <= classificationPointer + 1)) {
                            dataPair = tr.wholeText().split("\n", 2);
                            for (int i = 0; i < 2; i++) {
                                dataPair[i] = dataPair[i].trim();
                                Log.d(TAG, "found " + dataPair[i]);
                            }

                            if (PopActivity.userScientificClassification.get(classificationPointer).equals(dataPair[0])) { //detected searched classification

                                newData.add(dataPair[1]); //adding the specific classification
                                Log.d(TAG, "adding new data to newData = " + dataPair[1]);
                                classificationPointer++;

                            } else if (PopActivity.userScientificClassification.contains(dataPair[0])) { //detected needed classification but some were empty (not there)
                                Log.d(TAG, "userScientificClassification contains " + dataPair[0]);
                                int tempPointer = classificationPointer;
                                classificationPointer = PopActivity.userScientificClassification.indexOf(dataPair[0]);
                                for (; tempPointer < classificationPointer; tempPointer++) {
                                    newData.add("placeholder");
                                    Log.d(TAG, "adding new data to newData = placeholder");
                                }


                                classificationPointer++;
                                newData.add(dataPair[1]);
                                Log.d(TAG, "adding new data to newData = " + dataPair[1]);
                            }


                            scientificClassificationDetected = true;
                        } else if (scientificClassificationDetected) {
                            break;
                        }

                        //get the image
                        else if (!imageDetected) {
                            Log.d(TAG, "ELSEimage");
                            if (tr.getAllElements().hasAttr("data-file-type")) {
                                Elements imgElement = tr.getElementsByAttribute("data-file-type");
                                Log.d(TAG, "has data-file-type src =  " + imgElement.attr("src"));
                                Log.d(TAG, "has type = " + imgElement.attr("data-file-type"));
                                if (imgElement.attr("data-file-type").equals("bitmap")) {
                                    Log.d(TAG, "has class image");
                                    String imageURL = tr.selectFirst("img").absUrl("src");
                                    Log.d(TAG, "IMAGEURL  ===   " + imageURL);

                                    try {
                                        img = drawable_from_url(imageURL);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d(TAG, "image downloaded");
                                    imageDetected = true;
                                }

                            }
                        }
                    }
                    Log.d(TAG, "stopped scraping");
                    if (fragment.loadingRepresentative) {
                        Log.d(TAG, "loading representative is true");
                    } else {
                        Log.d(TAG, "loading representative is false");
                    }
                    newData.add(representative);
                    Collections.reverse(newData);

                    //loading into mZastupceArr
                    if (fragment.loadingRepresentative) {
                        //loading representative
                        //fragment.mZastupceArr.add(new Zastupce(representative, newData.get(0)[1], newData.get(1)[1])); // EDITED
                        fragment.mZastupceArr.add(new Zastupce(PopActivity.userParametersCount, img, newData));
                        Log.d(TAG, "newData size for representative= " + newData.size() + "\n\n");

                    } else {
                        //loading classification
                        //fragment.mZastupceArr.add(new Zastupce("", PopActivity.userScientificClassification.get(0), PopActivity.userScientificClassification.get(1))); // EDITED
                        PopActivity.reversedUserScientificClassification.add(0, "");
                        fragment.mZastupceArr.add(new Zastupce(PopActivity.userParametersCount, PopActivity.reversedUserScientificClassification));
                        Log.d(TAG, "newData size for classification= " + newData.size() + "\n\n");
                        fragment.loadingRepresentative = true;
                    }
                } else {
                    //add an empty fragment only with representative

                    fragment.mZastupceArr.add(new Zastupce(PopActivity.userParametersCount, newData));
                    Log.d(TAG, "Wiki for " + representative + " doesn't exist or you might have misspelled");
                }
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        fragment.mAdapter.notifyDataSetChanged();
                    }
                });

            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CreateListFragment fragment = fragmentWeakReference.get();
            fragment.progressBar.setVisibility(View.VISIBLE);
            fragment.progressBar.startAnimation(AnimationUtils.loadAnimation(fragment.getContext(), android.R.anim.fade_in));
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            CreateListFragment fragment = fragmentWeakReference.get();
            fragment.progressBar.setVisibility(View.GONE);
            fragment.progressBar.startAnimation(AnimationUtils.loadAnimation(fragment.getContext(), android.R.anim.fade_out));
            fragment.mAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        Drawable drawable_from_url(String url) throws java.io.IOException {
            CreateListFragment fragment = fragmentWeakReference.get();

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-agent", "Mozilla/4.0");

            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return new BitmapDrawable(Objects.requireNonNull(fragment.getContext()).getResources(), bitmap);
        }
    }


}


