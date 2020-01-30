package com.example.timad.poznavacka.activities.lists;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.timad.poznavacka.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

public class PopActivity extends Activity {

    private static final String TAG = "PopActivity";

    Button DONEButton;
    ProgressBar progressBar;

    LinearLayout layout1;
    ViewGroup viewGroup;
    ScrollView sv;

    static ArrayList<String> userScientificClassification;
    static ArrayList<String> reversedUserScientificClassification;

    static int userParametersCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        DONEButton = findViewById(R.id.PopDONEButton);
        progressBar = findViewById(R.id.progressBar2);

        userScientificClassification = new ArrayList<>();
        reversedUserScientificClassification = new ArrayList<>();

        viewGroup = findViewById(R.id.layout_id);
        sv = new ScrollView(this);
        layout1 = new LinearLayout(this);
        layout1.setOrientation(LinearLayout.VERTICAL);
        sv.addView(layout1);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.8));

        //getting the scientific classification selected by the user
        //needs to be added from broad to specific

        final PopActivity.WikiSearchClassification WikiSearchClassification = new PopActivity.WikiSearchClassification(PopActivity.this);
        WikiSearchClassification.execute();
/*
        //REPLACE with classific selected by user
        userScientificClassification.add("Říše");
        userScientificClassification.add("Kmen");
        userScientificClassification.add("Třída");
        userScientificClassification.add("Řád");
        userScientificClassification.add("Čeleď");
        userScientificClassification.add("Rod");
        userScientificClassification.add("Druh");
        userParametersCount = 8; //REPLACE with number of params selected by user + 1
 */

        //DONEButton
        DONEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                for (int i = 0; i < layout1.getChildCount(); i++) {
                    View nextChild = layout1.getChildAt(i);

                    if (nextChild instanceof CheckBox) {

                        CheckBox check = (CheckBox) nextChild;
                        if (check.isChecked()) {
                            Log.d(TAG, "checbox text to be added = " + check.getText().toString());
                            userScientificClassification.add(check.getText().toString());
                            userParametersCount++;
                        }
                    }
                }
            }
        });

        reversedUserScientificClassification.addAll(userScientificClassification);
        Collections.reverse(reversedUserScientificClassification);

    }

    private static class WikiSearchClassification extends AsyncTask<Void, String, Void> {

        private WeakReference<PopActivity> fragmentWeakReference;
        private boolean checkboxAdded = false;

        WikiSearchClassification(PopActivity context) {
            fragmentWeakReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PopActivity fragment = fragmentWeakReference.get();
            fragment.progressBar.setVisibility(View.VISIBLE);
            fragment.progressBar.startAnimation(AnimationUtils.loadAnimation(fragment.getApplicationContext(), android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            PopActivity fragment = fragmentWeakReference.get();
            Log.d(TAG, "AsyncTask finished");
            fragment.progressBar.setVisibility(View.GONE);
            fragment.progressBar.startAnimation(AnimationUtils.loadAnimation(fragment.getApplicationContext(), android.R.anim.fade_out));
            fragment.viewGroup.removeAllViews();
            fragment.viewGroup.addView(fragment.sv);
            if (!checkboxAdded) {
                Log.d(TAG, "checkboxAdded is false");
                Toast.makeText(fragment, "Check the language", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "checkboxAdded is true");
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            PopActivity fragment = fragmentWeakReference.get();
            checkboxAdded = true;
            CheckBox ch = new CheckBox(fragment.getApplicationContext());
            ch.setText(values[0]);
            ch.setTextColor(Color.WHITE);
            fragment.layout1.addView(ch);

            Log.d(TAG, "Checkbox " + values[0] + " added");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            PopActivity fragment = fragmentWeakReference.get();
            String representative = CreateListFragment.exampleRepresentative;
            String searchText = representative.replace(" ", "_");

            Log.d(TAG, "Connecting to site");
            Document doc = null;
            try {
                doc = Jsoup.connect("https://" + CreateListFragment.languageURL + ".wikipedia.org/api/rest_v1/page/html/" + URLEncoder.encode(searchText, "UTF-8")).userAgent("Mozilla").get();
            } catch (IOException e) {
                //not connected to internet
                e.printStackTrace();
                Log.d(TAG, "no wiki");
                return null;
            }

            String[] dataPair;
            String classData;
            boolean scientificClassificationDetected = false;
            Element infoBox;

            if (doc != null && doc.head().hasText()) {
                Log.d(TAG, "Connected successfully");
                infoBox = doc.getElementsByTag("table").first().selectFirst("tbody");
                Elements trs = infoBox.select("tr");
                for (Element tr :
                        trs) {
                    Log.d(TAG, "foring in trs");
                    if (!tr.getAllElements().hasAttr("colspan")) {
                        dataPair = tr.wholeText().split("\n", 2);
                        if (dataPair.length == 1) { //detected wrong table
                            Log.d(TAG, "different table");
                            return null;
                        }
                        classData = dataPair[0].trim();
                        Log.d(TAG, "found " + dataPair[0]);

                        publishProgress(classData); //adding checkbox in progressUpdate

                        scientificClassificationDetected = true;
                    } else if (scientificClassificationDetected) {
                        Log.d(TAG, "Scientific classification async task successful");
                        return null;
                    }
                }
            } else {
                return null;
            }


            return null;
        }
    }


}
