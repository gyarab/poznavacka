package com.example.timad.poznavacka.activities.lists;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import java.util.Collections;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.timad.poznavacka.activities.lists.PopActivity.reversedUserScientificClassification;
import static com.example.timad.poznavacka.activities.lists.PopActivity.userParametersCount;
import static com.example.timad.poznavacka.activities.lists.PopActivity.userScientificClassification;


public class CreateListFragment extends Fragment implements AdapterView.OnItemSelectedListener {
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
    static String exampleRepresentative;
    private EditText userDividngString;
    private Switch autoImportSwitch;
    private Spinner languageSpinner;

    static String languageURL;

    private boolean switchPressedOnce;
    private boolean loadingRepresentative;
    private boolean autoImportIsChecked;

    private FirestoreImpl firestoreImpl;
    private FirebaseFirestore db; //for testing

    private ArrayList<String> representatives;
    public ArrayList<String> exampleRepresentativeClassification;
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
        languageSpinner = view.findViewById(R.id.languageSpinner);
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

        mLManager = new LinearLayoutManager(getContext());

        //switch
        autoImportSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String rawRepresentative = userInputRepresentatives.getText().toString();
                if (isChecked) {
                    if (rawRepresentative.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter at least one representative..", Toast.LENGTH_LONG).show();
                        autoImportSwitch.setChecked(false);
                    } else {
                        dividingString = userDividngString.getText().toString().trim();
                        if (dividingString.isEmpty()) { //single
                            exampleRepresentative = rawRepresentative.trim();
                        } else { //multiple
                            if (rawRepresentative.contains(dividingString)) {
                                exampleRepresentative = rawRepresentative.substring(0, rawRepresentative.indexOf(dividingString)).trim();
                            } else {  //single, but dividing string was entered
                                exampleRepresentative = rawRepresentative.trim();
                            }

                        }
                        exampleRepresentative = exampleRepresentative.substring(0, 1).toUpperCase() + exampleRepresentative.substring(1).toLowerCase();
                        switchPressedOnce = true;
                        autoImportIsChecked = true;
                        Intent intent = new Intent(getContext(), PopActivity.class);
                        startActivity(intent);
                    }
                } else {
                    autoImportIsChecked = false;
                }
            }
        });

        //spinner
        languageURL = "en";
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.language_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
        languageSpinner.getBackground().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        languageSpinner.setOnItemSelectedListener(this);

        //create button
        btnCREATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               /* title = userInputTitle.getText().toString().trim();
                dividingString = userDividngString.getText().toString().trim();
                String rawRepresentatives = userInputRepresentatives.getText().toString();

                if (!(title.isEmpty() || dividingString.isEmpty() || rawRepresentatives.isEmpty())) {
                    representatives = new ArrayList<>(Arrays.asList(rawRepresentatives.split("\\s*" + dividingString + "\\s*")));
                    //capitalizing the first letter
                    if (!(languageURL.equals("ar") || languageURL.equals("zh") || languageURL.equals("kr"))) {
                        for (int i = 0; i < representatives.size(); i++) {
                            String withUpperLetter = representatives.get(i).substring(0,1).toUpperCase() + representatives.get(i).substring(1);
                            representatives.add(i, withUpperLetter);
                        }
                    }*/


                Toast.makeText(getActivity(), "Creating, please wait..", Toast.LENGTH_SHORT).show();
                final WikiSearchRepresentatives WikiSearchRepresentatives = new WikiSearchRepresentatives(CreateListFragment.this);

                //recyclerView getting user parameters into account
                if (autoImportIsChecked) {
                    mAdapter = new ZastupceAdapter(mZastupceArr, userParametersCount);
                    parameters = userParametersCount;
                    loadingRepresentative = false;
                } else {
                    mAdapter = new ZastupceAdapter(mZastupceArr, 1);
                    parameters = 1;
                    loadingRepresentative = true;
                }
                mRecyclerView.setLayoutManager(mLManager);
                mRecyclerView.setAdapter(mAdapter);

                //WikiSearchRepresentatives.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                WikiSearchRepresentatives.execute("");
                /*} else {
                    Toast.makeText(getActivity(), "Enter all info", Toast.LENGTH_SHORT).show();
                }*/

            }
        });


        btnSAVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //getting the language of wiki
        Object selectedLanguageSpinnerItem = parent.getItemAtPosition(position);
        String languageString = selectedLanguageSpinnerItem.toString();

        switch (languageString) {
            case "English":
                languageURL = "en";
                break;
            case "Czech":
                languageURL = "cs";
                break;
            case "French":
                languageURL = "fr";
                break;
            case "German":
                languageURL = "de";
                break;
            case "Spanish":
                languageURL = "es";
                break;
            case "Japanese":
                languageURL = "ja";
                break;
            case "Russian":
                languageURL = "ru";
                break;
            case "Italian":
                languageURL = "it";
                break;
            case "Chinese":
                languageURL = "zh";
                break;
            case "Portuguese":
                languageURL = "pt";
                break;
            case "Arabic":
                languageURL = "ar";
                break;
            case "Persian":
                languageURL = "fa";
                break;
            case "Polish":
                languageURL = "pl";
                break;
            case "Dutch":
                languageURL = "nl";
                break;
            case "Indonesian":
                languageURL = "id";
                break;
            case "Ukrainian":
                languageURL = "uk";
                break;
            case "Hebrew":
                languageURL = "he";
                break;
            case "Swedish":
                languageURL = "sv";
                break;
            case "Korean":
                languageURL = "ko";
                break;
            case "Vietnamese":
                languageURL = "vi";
                break;
            case "Finnish":
                languageURL = "fi";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    //možná pomalý způsob, kdyztak -> https://stackoverflow.com/questions/33862336/how-to-extract-information-from-a-wikipedia-infobox
    private static class WikiSearchRepresentatives extends AsyncTask<String, String, Void> {

        private WeakReference<CreateListFragment> fragmentWeakReference;

        WikiSearchRepresentatives(CreateListFragment context) {
            fragmentWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(String... args) {
            final CreateListFragment fragment = fragmentWeakReference.get();

            if (args[0].equals("classification")) {

            }
            //misto testInput -> representatives
            ArrayList<String> testInput = new ArrayList<>();
            testInput.add("Pes domácí");
            testInput.add("Pes domácí");
            testInput.add("Koira");
            testInput.add("Tasemnice bezbranná");
            testInput.add("Lýtková kost");
            testInput.add("Žula");


            for (String representative :
                    testInput) {
                Log.d(TAG, "------------------------------");
                Log.d(TAG, "current representative = " + representative);
                String searchText = representative.replace(" ", "_");

                Document doc = null;
                try {
                    doc = Jsoup.connect("https://" + languageURL + ".wikipedia.org/api/rest_v1/page/html/" + URLEncoder.encode(searchText, "UTF-8")).userAgent("Mozilla").get();
                } catch (IOException e) {
                    //not connected to internet
                    e.printStackTrace();
                    Log.d(TAG, "no wiki");
                    publishProgress(representative);
                }

                ArrayList<String> newData = new ArrayList<>();
                String[] dataPair;
                Drawable img = null;
                String imageURL = "";
                boolean scientificClassificationDetected = false;
                boolean imageDetected = false;
                int classificationPointer = 0;
                int trCounter = 0;
                Element infoBox;

                if (doc != null && doc.head().hasText()) {

                    infoBox = doc.getElementsByTag("table").first().selectFirst("tbody");
                    Elements trs = infoBox.select("tr");
                    for (Element tr :
                            trs) {

                        trCounter++;
                        Log.d(TAG, "current tr = " + trCounter);
                        if (!tr.getAllElements().hasAttr("colspan") && fragment.autoImportIsChecked && !(userScientificClassification.size() <= classificationPointer)) {
                            dataPair = tr.wholeText().split("\n", 2);
                            if (dataPair.length == 1) { //detected wrong table
                                Log.d(TAG, "different table");
                                for (int i = 0; i < userParametersCount - 1; i++) {
                                    newData.add("");
                                }
                                break;
                            }
                            for (int i = 0; i < 2; i++) {
                                dataPair[i] = dataPair[i].trim();
                                Log.d(TAG, "found " + dataPair[i]);
                            }

                            Log.d(TAG, "classPointer = " + classificationPointer);
                            Log.d(TAG, userScientificClassification.get(classificationPointer) + " ?equals = " + dataPair[0]);
                            Log.d(TAG, "userSciClass[0] = " + userScientificClassification.get(0));

                            if (userScientificClassification.get(classificationPointer).equals(dataPair[0])) { //detected searched classification
                                newData.add(dataPair[1]); //adding the specific classification
                                Log.d(TAG, "adding new data to newData = " + dataPair[1]);
                                classificationPointer++;

                            } else if (userScientificClassification.contains(dataPair[0])) { //detected needed classification but some were empty (not there)
                                Log.d(TAG, "userScientificClassification contains " + dataPair[0]);
                                int tempPointer = classificationPointer;
                                classificationPointer = userScientificClassification.indexOf(dataPair[0]);
                                for (; tempPointer < classificationPointer; tempPointer++) {
                                    newData.add("");
                                    Log.d(TAG, "adding new data to newData = empty (not detected)");
                                }


                                classificationPointer++;
                                newData.add(dataPair[1]);
                                Log.d(TAG, "adding new data to newData = " + dataPair[1]);
                            }


                            scientificClassificationDetected = true;
                        } else if (scientificClassificationDetected) {
                            if (newData.size() < (userParametersCount - 1)) { //the last parameter was not detected

                                newData.add("");
                            }
                            break;
                        }

                        //get the image
                        else if (!imageDetected) {
                            if (tr.getAllElements().hasAttr("data-file-type")) {
                                Elements imgElement = tr.getElementsByAttribute("data-file-type");
                                if (imgElement.attr("data-file-type").equals("bitmap")) {
                                    imageURL = tr.selectFirst("img").absUrl("src");
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
                        if (classificationPointer == 0) { //detected wrong table that doesn't contain any useful info
                            for (int i = 0; i < userParametersCount - 1; i++) {
                                newData.add("");
                            }
                        }
                        if (img == null) {
                            //get only the image
                            try {
                                Element imgElement = doc.getElementsByAttributeValueContaining("typeof", "Image/Thumb").first().getElementsByAttributeValue("data-file-type", "bitmap").first();
                                if (imgElement != null) {
                                    imageURL = imgElement.absUrl("src");
                                    Log.d(TAG, "getting only image - URL = " + imageURL);
                                    try {
                                        img = drawable_from_url(imageURL);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                fragment.mZastupceArr.add(new Zastupce(userParametersCount, newData));
                                continue;
                            }
                        }
                        fragment.mZastupceArr.add(new Zastupce(userParametersCount, img, imageURL, newData));
                        Log.d(TAG, "newData size for representative= " + newData.size() + "\n\n");

                    } else {
                        //loading classification
                        reversedUserScientificClassification.add(0, "");
                        fragment.mZastupceArr.add(new Zastupce(userParametersCount, reversedUserScientificClassification));
                        Log.d(TAG, "newData size for classification= " + newData.size() + "\n\n");
                        fragment.loadingRepresentative = true;
                    }
                } else {
                    //add an empty only with representative
                    fragment.mZastupceArr.add(new Zastupce(userParametersCount, representative));
                    Log.d(TAG, "Wiki for " + representative + " doesn't exist or you might have misspelled");
                }
                publishProgress("");
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
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            CreateListFragment fragment = fragmentWeakReference.get();
            if (!values[0].equals("")) {
                Toast.makeText(fragment.getActivity(), "No Wiki for " + values[0], Toast.LENGTH_SHORT).show();
            } else {
                fragment.mAdapter.notifyDataSetChanged();
            }
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

        public Drawable drawable_from_url(String url) throws java.io.IOException {
            CreateListFragment fragment = fragmentWeakReference.get();

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-agent", "Mozilla/4.0");

            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return new BitmapDrawable(Objects.requireNonNull(fragment.getContext()).getResources(), bitmap);
        }


/*        public boolean isInternetAvailable() {
            try {
                InetAddress ipAddr = InetAddress.getByName("google.com");
                //You can replace it with your name
                return !ipAddr.equals("");

            } catch (Exception e) {
                return false;
            }
        }*/
    }


}


