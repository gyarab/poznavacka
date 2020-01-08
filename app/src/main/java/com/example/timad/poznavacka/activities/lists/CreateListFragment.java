package com.example.timad.poznavacka.activities.lists;

import android.content.Intent;
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
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

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

    /*// Pattern for recognizing a URL, based off RFC 3986
    private static final Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createlist, container, false);
        firestoreImpl = new FirestoreImpl();
        btnCREATE = view.findViewById(R.id.createButton);
        btnSAVE = view.findViewById(R.id.saveButton);
        progressBar = view.findViewById(R.id.progressBar);
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
        /*mZastupceArr.add(new Zastupce("Nazev", "Druh", "Kmen"));
        mZastupceArr.add(new Zastupce("Plejtvak", "Ploutvoviti", "Ryba"));
        mZastupceArr.add(new Zastupce("Nazev", "Druh", "Kmen"));
        mZastupceArr.add(new Zastupce("Nazev", "Druh", "Kmen"));
        mZastupceArr.add(new Zastupce("Plejtvak", "Ploutvoviti", "Ryba"));
        mZastupceArr.add(new Zastupce("Nazev", "Druh", "Kmen"));
        mZastupceArr.add(new Zastupce("Plejtvak", "Ploutvoviti", "Ryba"));
        mZastupceArr.add(new Zastupce("Nazev", "Druh", "Kmen"));
        mZastupceArr.add(new Zastupce("Nazev", "Druh", "Kmen"));
        mZastupceArr.add(new Zastupce("Plejtvak", "Ploutvoviti", "Ryba"));
        mZastupceArr.add(new Zastupce("Nazev", "Druh", "Kmen"));
        mZastupceArr.add(new Zastupce("Nazev", "Druh", "Kmen"));
        mZastupceArr.add(new Zastupce("Plejtvak", "Ploutvoviti", "Ryba"));
        mZastupceArr.add(new Zastupce("Nazev", "Druh", "Kmen"));
        mZastupceArr.add(new Zastupce("Plejtvak", "Ploutvoviti", "Ryba"));
        mZastupceArr.add(new Zastupce("Nazev", "Druh", "Kmen"));
        mZastupceArr.add(new Zastupce("Nazev", "Druh", "Kmen"));
        mZastupceArr.add(new Zastupce("Plejtvak", "Ploutvoviti", "Ryba"));*/

        mLManager = new LinearLayoutManager(getContext());
        mAdapter = new ZastupceAdapter(mZastupceArr);

        mRecyclerView.setLayoutManager(mLManager);
        mRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.setVisibility(View.INVISIBLE);

        //final WikiSearch wikiSearch = new WikiSearch(this);


        autoImportSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !switchPressedOnce) {
                    switchPressedOnce = true;
                    Intent intent = new Intent(getContext(), PopActivity.class);
                    startActivity(intent);
                } else {

                }
            }
        });

        btnCREATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(), "CREATE BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                final WikiSearch wikiSearch = new WikiSearch(CreateListFragment.this);
                //testing
                wikiSearch.execute();
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


    //ŠPATNÝ ZPŮSOB, AŽ BUDE ČAS, PŘEDĚLAT -> https://stackoverflow.com/questions/33862336/how-to-extract-information-from-a-wikipedia-infobox
    private static class WikiSearch extends AsyncTask<Void, Void, Void> {

        private WeakReference<CreateListFragment> fragmentWeakReference;

        WikiSearch(CreateListFragment context) {
            fragmentWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            CreateListFragment fragment = fragmentWeakReference.get();

            //misto testInput -> representatives
            ArrayList<String> testInput = new ArrayList<>();
            testInput.add("Sýkora");
            //testInput.add("Pes domácí");


            for (String representative :
                    testInput) {
                String searchText = representative.replace(" ", "_");

                Document doc = null;
                try {
                    doc = Jsoup.connect("https://" + PopActivity.languageURL + ".wikipedia.org/api/rest_v1/page/html/" + URLEncoder.encode(searchText, "UTF-8")).userAgent("Mozilla").get();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "couldn't connect to the site");
                    fragment.mZastupceArr.add(new Zastupce("Error loading " + representative, "", ""));
                }

                ArrayList<String> scientificClasses = new ArrayList<>();
                ArrayList<String[]> newData = new ArrayList<>();
                String[] dataPair = new String[2];
                int dataCounter = 0;
                boolean scientificClassificationDetected = false;

                Element infoBox = null;
                if (doc != null) {
                    infoBox = doc.getElementsByTag("table").first().selectFirst("tbody");
                    //Toast.makeText(fragment.getActivity(), "Wiki for " + representative + " could not be loaded", Toast.LENGTH_SHORT).show();
                    Elements trs = infoBox.select("tr");
                    for (Element tr :
                            trs) {
                        if (!tr.getAllElements().hasAttr("colspan")) {
                            dataPair = tr.wholeText().split("\n", 2);
                            for (int i = 0; i < 2; i++) {
                                dataPair[i] = dataPair[i].trim();
                                Log.d(TAG, dataPair[i]);
                            }
                            newData.add(dataPair);


                            scientificClassificationDetected = true;
                        } else if (scientificClassificationDetected) {
                            break;
                        }
                    }
                    //loading into mZastupceArr
                    if (fragment.loadingRepresentative) {
                        //loading representative
                        fragment.mZastupceArr.add(new Zastupce(newData.get(0)[1], newData.get(1)[1], newData.get(2)[1]));

                    } else {
                        //loading classification
                        //fragment.mZastupceArr.add(new Zastupce(data[0], data[2], data[4]));
                        fragment.mZastupceArr.add(new Zastupce(newData.get(0)[0], newData.get(1)[0], newData.get(2)[0]));
                        Log.d(TAG, newData.size() + "\n\n");
                        fragment.loadingRepresentative = true;
                    }
                } else {
                    Log.d(TAG, "Wiki for " + representative + " doesn't exist or you might have misspelled");
                }
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CreateListFragment fragment = fragmentWeakReference.get();
            /*fragment.progressBar.setVisibility(View.VISIBLE);
            fragment.progressBar.startAnimation(AnimationUtils.loadAnimation(fragment.getContext(), android.R.anim.fade_in));*/
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
    }
}


