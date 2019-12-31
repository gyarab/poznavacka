package com.example.timad.poznavacka.activities.lists;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.example.timad.poznavacka.FirestoreImpl;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.Zastupce;
import com.example.timad.poznavacka.ZastupceAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    private Switch autoRadDruhSwitch;

    private FirestoreImpl firestoreImpl;
    private FirebaseFirestore db; //for testing

    private ArrayList<String> representatives;
    private String title;
    private String dividingString;

    public static String testString;

    /* RecyclerView */
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLManager;
    private ArrayList<Zastupce> mZastupceArr;

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
        autoRadDruhSwitch = view.findViewById(R.id.autoRadDruhSwitch);
        userDividngString = view.findViewById(R.id.dividingCharacter);
        db = FirebaseFirestore.getInstance(); //testing


        //jenom testovani
        try {
            getWikipediaApiJSONcs("dog");
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* RecyclerView */
        mZastupceArr = new ArrayList<>();
        mZastupceArr.add(new Zastupce("Nazev", "Druh", "Kmen"));
        mZastupceArr.add(new Zastupce("Plejtvak", "Ploutvoviti", "Ryba"));

        mRecyclerView = view.findViewById(R.id.recyclerViewZ);
        mRecyclerView.setHasFixedSize(false);
        mLManager = new LinearLayoutManager(getContext());
        mAdapter = new ZastupceAdapter(mZastupceArr);

        mRecyclerView.setLayoutManager(mLManager);
        mRecyclerView.setAdapter(mAdapter);

        btnCREATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = userInputTitle.getText().toString();
                dividingString = userDividngString.getText().toString();
                String rawRepresentatives = userInputRepresentatives.getText().toString();

                representatives = new ArrayList<>(Arrays.asList(rawRepresentatives.split("\\s*" + dividingString + "\\s*")));

                for (int i = 0; i < representatives.size(); i++) {

                    //uploading poznavacka
                    Map<String, Object> representativeInfo = new HashMap<>();
                    representativeInfo.put(KEY_ZASTUPCE, representatives.get(i));
                    representativeInfo.put(KEY_IMGREF, "imageRef - cislo/hash?");
                    if (autoRadDruhSwitch.isChecked()) {
                        representativeInfo.put(KEY_DRUH, "dohledany druh");
                        representativeInfo.put(KEY_RAD, "dohledany rad");
                    }

                    firestoreImpl.uploadRepresentative(title, representatives.get(i), representativeInfo);

                }


                Toast.makeText(getActivity(), "CREATE BUTTON CLICKED", Toast.LENGTH_SHORT).show();
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


    private class JSoupNew extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            String urlString = "https://www.seznam.cz/";
            Connection con = Jsoup.connect(urlString)
                    .userAgent("Mozilla");
            Document doc = null;
            try {
                doc = con.get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (doc != null) {
                Log.d(TAG, doc.title());
                Log.d(TAG, doc.text());
                Elements links = doc.select("a");
                for (Element link : links) {
                    Log.d(TAG, "Link:" + link);
                    Log.d(TAG, "Text:" + link.text());
                }

            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }


    private String getWikipediaApiJSONcs(String searchText) throws IOException {
        searchText += " Wikipedia";
        //Document google = Jsoup.connect("https://www.google.com/search?q=" + URLEncoder.encode(searchText, "UTF-8")).userAgent("Mozilla").get();
        Document google = Jsoup.connect("https://www.google.com/search?q=" + searchText).userAgent("Mozilla").get();
        String wikipediaURL = google.getElementsByTag("cite").get(0).text();
        String wikipediaApiJSON = "https://www.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles="
                + URLEncoder.encode(wikipediaURL.substring(wikipediaURL.lastIndexOf("/") + 1, wikipediaURL.length()), "UTF-8");
        Log.d("wikiseatch", wikipediaURL);
        Log.d("wikiseatch", wikipediaApiJSON);

        return "";
    }
}



