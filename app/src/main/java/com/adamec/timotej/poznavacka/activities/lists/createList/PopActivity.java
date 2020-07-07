package com.adamec.timotej.poznavacka.activities.lists.createList;

import android.app.Activity;
import android.content.Intent;
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

import com.adamec.timotej.poznavacka.BuildConfig;
import com.adamec.timotej.poznavacka.R;
import com.adamec.timotej.poznavacka.google_search_objects.GoogleItemObject;
import com.adamec.timotej.poznavacka.google_search_objects.GoogleSearchObject;
import com.adamec.timotej.poznavacka.google_search_objects.GoogleSearchObjectAutoCorrect;
import com.adamec.timotej.poznavacka.google_search_objects.Spelling;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

import timber.log.Timber;

import static com.adamec.timotej.poznavacka.activities.lists.createList.CreateListActivity.languageURL;

public class PopActivity extends Activity {

    private static final String TAG = "PopActivity";

    Button DONEButton;
    ProgressBar progressBar;

    LinearLayout layout1;
    ViewGroup viewGroup;
    ScrollView sv;

    public ArrayList<String> userScientificClassification;
    public ArrayList<String> reversedUserScientificClassification;

    public int userParametersCount = 1;

    Integer responseCode = null;
    String responseMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
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

        final PopActivity.WikiSearchClassification WikiSearchClassification = new PopActivity.WikiSearchClassification(PopActivity.this);
        WikiSearchClassification.execute();

        //DONEButton
        DONEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("DONE Clicked");
                finishPop();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishPop();
    }

    private void finishPop() {
        userParametersCount = 1;
        for (int i = 0; i < layout1.getChildCount(); i++) {
            View nextChild = layout1.getChildAt(i);

            if (nextChild instanceof CheckBox) {

                CheckBox check = (CheckBox) nextChild;
                if (check.isChecked()) {
                    Timber.d("checbox text to be added = %s", check.getText().toString());
                    userScientificClassification.add(check.getText().toString());
                    userParametersCount++;
                }
            }
        }

        reversedUserScientificClassification.addAll(userScientificClassification);
        Collections.reverse(reversedUserScientificClassification);
        //reversedUserScientificClassification.add(0, "");

        Intent returnIntent = new Intent();
        returnIntent.putExtra("userParametersCount", userParametersCount);
        returnIntent.putExtra("userScientificClassification", userScientificClassification);
        returnIntent.putExtra("reversedUserScientificClassification", reversedUserScientificClassification);
        setResult(Activity.RESULT_OK, returnIntent);
        Timber.d("passing PopValues");

        finish();
    }

    private class WikiSearchClassification extends AsyncTask<Void, String, Void> {

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
        protected void onProgressUpdate(String... trimmedValues) {
            super.onProgressUpdate(trimmedValues);
            PopActivity fragment = fragmentWeakReference.get();
            checkboxAdded = true;

            for (String value :
                    trimmedValues) {
                CheckBox ch = new CheckBox(fragment.getApplicationContext());
                ch.setText(value);
                ch.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                fragment.layout1.addView(ch);

                Log.d(TAG, "Checkbox " + trimmedValues[0] + " added");
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            PopActivity fragment = fragmentWeakReference.get();

            //old Create
            //ArrayList<String> representatives = CreateListFragment.representatives;

            ArrayList<String> representatives = CreateListActivity.representatives;
            Timber.d("PopRepresentatives are %s", representatives.toString());

            allRepresentatives:
            for (String representative :
                    representatives) {
                String searchText = representative.trim().replace(" ", "_");
                String googleSearchRepresentative = representative;
                boolean searchSuccessful = false;
                while (!searchSuccessful) {
                    searchSuccessful = true;
                    //Google search
                    String result = "";
                    String urlString = "https://www.googleapis.com/customsearch/v1/siterestrict?key=AIzaSyCaQxGMMIGJOj-XRgfzR6me0e70IZ8qR38&cx=011868713606192238742:phdn1jengcl&lr=lang_" + languageURL + "&q=" + googleSearchRepresentative;
                    URL url = null;
                    try {
                        url = new URL(urlString);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    HttpURLConnection conn = null;
                    try {
                        conn = (HttpURLConnection) url.openConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        responseCode = conn.getResponseCode();
                        responseMessage = conn.getResponseMessage();
                    } catch (IOException e) {
                        Log.e(TAG, "Http getting response code ERROR " + e.toString());
                        break allRepresentatives;
                    }

                    Timber.d("Http response code =" + responseCode + " message=" + responseMessage);
                    Timber.d("Current PopRepresentative is %s", representative);

                    try {
                        if (responseCode != null && responseCode == 200) {
                            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = rd.readLine()) != null) {
                                sb.append(line).append("\n");
                            }
                            rd.close();
                            conn.disconnect();
                            result = sb.toString();
                            Log.d(TAG, "result=" + result);
                        } else {
                            //response problem

                            String errorMsg = "Http ERROR response " + responseMessage + "\n" + "Are you online ? " + "\n" + "Make sure to replace in code your own Google API key and Search Engine ID";
                            Log.e(TAG, errorMsg);
                            //result = errorMsg;
                        }

                    } catch (IOException e) {
                        Log.e(TAG, "Http Response ERROR " + e.toString());
                    }
                    Log.d(TAG, "and google result is = " + result);


                    Gson gson = new Gson();
                    try {
                        GoogleSearchObject googleSearchObject = gson.fromJson(result, GoogleSearchObject.class);
                        Timber.d(googleSearchObject.toString());
                        GoogleItemObject myGoogleSearchObject = googleSearchObject.getItems().get(0);
                        String wikipeidaURL = myGoogleSearchObject.getFormattedUrl();
                        Timber.d("google test is = %s", wikipeidaURL);
                        searchText = wikipeidaURL.substring(wikipeidaURL.indexOf("/wiki/") + 6);
                    } catch (Exception e) {
                        //no results
                        e.printStackTrace();
                        searchSuccessful = false;
                        GoogleSearchObjectAutoCorrect googleSearchObjectAutoCorrect = gson.fromJson(result, GoogleSearchObjectAutoCorrect.class);
                        Timber.d(googleSearchObjectAutoCorrect.toString());
                        //if corrects spelling
                        if (googleSearchObjectAutoCorrect.getSpelling() != null) {
                            Spelling spelling = googleSearchObjectAutoCorrect.getSpelling();
                            googleSearchRepresentative = spelling.getCorrectedQuery();
                        } else {
                            continue allRepresentatives;
                        }
                    }
                }

                Document doc = null;
                try {
                    Timber.d("connecting after google to = " + "https://" + languageURL + ".wikipedia.org/api/rest_v1/page/html/" + searchText /*URLEncoder.encode(searchText, "UTF-8")*/ + "?redirect=true");
                    doc = Jsoup.connect("https://" + languageURL + ".wikipedia.org/api/rest_v1/page/html/" + URLEncoder.encode(searchText, "UTF-8") + "?redirect=true").userAgent("Mozilla").get();
                } catch (IOException e) {
                    //not connected to internet
                    e.printStackTrace();
                    Timber.d("Classification - no wiki for %s", representative);
                    continue;
                }

                if (doc != null && doc.head().hasText()) {
                    String[] newData;
                    ArrayList<Element> infoTables = getInfoTables(doc);
                    if (infoTables.size() == 0) continue;
                    newData = harvestClassification(infoTables);

                    String[] trimmedValues = trimStringArray(newData);
                    if (trimmedValues.length == 0) continue;

                    publishProgress(trimmedValues);
                    break;
                }

                //for russian sites
/*
                for (Element tr : trs) {
                    Log.d(TAG, "foring in russian style trs :))");
                    if (tr.getElementsByAttribute("class").first().val().equals("")) { //detected tr for classification
                        Log.d(TAG, "whole text = " + tr.wholeText());
                        Elements elementsOfText = tr.child(1).children();
                        for (Element textElement :
                                elementsOfText) {
                            String text = textElement.wholeText();
                            Log.d(TAG, "russian text = " + text);
                        }
                        return null;
                    }
                }
*/

            }


            return null;
        }

        private ArrayList redirect_getTable(Document doc) {
            ArrayList returnDocAndInfobox = new ArrayList();
            Element infoBox = null;

            Log.d(TAG, "ROZCESTNÍK");
            Elements linkElements = doc.getElementsByAttributeValue("rel", "mw:WikiLink");
            boolean newSiteFound = false;
            for (Element linkElement :
                    linkElements) {
                if (!linkElement.hasClass("new")) {
                    newSiteFound = true;
                    //found element with the link
                    String newWikipediaURLsuffix = linkElement.attr("href");
                    String newSearchText = newWikipediaURLsuffix.substring(2);
                    try {
                        Log.d(TAG, "redirected and connecting to new wikipedia for " + newSearchText);
                        doc = Jsoup.connect("https://" + languageURL + ".wikipedia.org/api/rest_v1/page/html/" + URLEncoder.encode(newSearchText, "UTF-8") + "?redirect=true").userAgent("Mozilla").get();

                        try {
                            Elements rawTables = doc.getElementsByTag("table");
                            for (Element table :
                                    rawTables) {
                                //cz and en infoTable
                                if (languageURL.equals("en") || languageURL.equals("cs")) {
                                    if (table.id().contains("info")) {
                                        Log.d(TAG, "doesn't contain id info = " + table.id());
                                        infoBox = table.selectFirst("tbody");
                                        break;
                                    } else if (table.attr("class").contains("info")) {
                                        Log.d(TAG, "doesn't contain class info = " + table.attr("class"));
                                        infoBox = table.selectFirst("tbody");
                                        break;
                                    }
                                    //de infoTable
                                } else if (languageURL.equals("de")) {

                                    if (table.id().contains("taxo") || table.id().contains("Taxo")) {
                                        Log.d(TAG, "doesn't contain id info = " + table.id());
                                        infoBox = table.selectFirst("tbody");
                                        break;
                                    } else if (table.attr("class").contains("taxo")) {
                                        Log.d(TAG, "doesn't contain class info = " + table.attr("class"));
                                        infoBox = table.selectFirst("tbody");
                                        break;
                                    } else if (table.id().toLowerCase().contains("info")) {
                                        Log.d(TAG, "does contain id info = " + table.id());
                                        infoBox = table.selectFirst("tbody");
                                        break;
                                    } else if (table.attr("class").toLowerCase().contains("info")) {
                                        Log.d(TAG, "does contain class info = " + table.attr("class"));
                                        infoBox = table.selectFirst("tbody");
                                        break;
                                    }
                                }

                            }

                        } catch (NullPointerException e) {
                            //rozcestník
                            e.printStackTrace();
                            Log.d(TAG, "no wiki (redirect)");
                        }

                    } catch (IOException er) {
                        //this probably won't happen
                        er.printStackTrace();
                        Log.d(TAG, "no wiki (redirect)");
                        newSiteFound = false;
                    }
                    break;
                }
            }
            returnDocAndInfobox.add(doc);
            returnDocAndInfobox.add(infoBox);
            returnDocAndInfobox.add(newSiteFound);
            return returnDocAndInfobox;
        }

        private String[] harvestClassification(ArrayList<Element> infoTables) {

            ArrayList returnList = new ArrayList();

            String[] newData = new String[50];
            int classificationPointer = 0;
            int trCounter = 0;

            for (Element infoTable :
                    infoTables) {

                Elements trs = infoTable.select("tr");

                for (Element tr :
                        trs) {

                    trCounter++;
                    Log.d(TAG, "current tr = " + trCounter);
                    if (tr.childrenSize() != 0 && tr.child(0).siblingElements().size() == 1) {
                        Timber.d("Sibling size = %s", tr.child(0).siblingElements().size());
                        String th = tr.children().first().text();
                        Log.d(TAG, "found " + th);
                        String td = tr.children().last().text();
                        Log.d(TAG, "found " + td);
                        Log.d(TAG, "classPointer = " + classificationPointer);

                        if (th.isEmpty()) continue;
                        if (th.trim().substring(th.length() - 1, th.length()).contentEquals(":")) {
                            th = th.replace(":", "");
                        }


                        newData[classificationPointer] = th;
                        classificationPointer++;
                        //newData.add(dataPair[0]);
                        Log.d(TAG, "adding new data to newData = " + th);
                    }
                }
                //returnList.add(newData);
            }
            //returnList.add(classificationPointer);
            return newData;
        }
    }

    private String[] trimStringArray(String[] newData) {
        //trimming the array
        int count = 0;
        for (String value :
                newData) {
            if (value != null) {
                count++;
            }
        }
        String[] trimmedValues = new String[count];
        int index = 0;
        for (String value :
                newData) {
            if (value != null) {
                trimmedValues[index++] = value;
            }
        }
        return trimmedValues;
    }


    private ArrayList<Element> getInfoTables(Document doc) {
        ArrayList<Element> infoTables = new ArrayList<>();
        boolean addTable = false;
        Element classTable = null;
        try {
            Elements rawTables = doc.getElementsByTag("table");
            for (Element table :
                    rawTables) {

                if (table.id().toLowerCase().contains("info")) {
                    Log.d(TAG, "does contain id info = " + table.id());
                    addTable = true;
                    classTable = table;
                    break;
                } else if (table.attr("class").toLowerCase().contains("info")) {
                    Log.d(TAG, "does contain class info = " + table.attr("class"));
                    addTable = true;
                    classTable = table;
                    break;
                } else if (table.id().toLowerCase().contains("taxo")) {
                    Log.d(TAG, "doesn't contain id info = " + table.id());
                    addTable = true;
                    classTable = table;
                    break;
                } else if (table.attr("class").toLowerCase().contains("taxo")) {
                    Log.d(TAG, "doesn't contain class info = " + table.attr("class"));
                    addTable = true;
                    classTable = table;
                    break;
                } else if (table.attr("class").toLowerCase().contains("sinottico")) {
                    Log.d(TAG, "doesn't contain class info = " + table.attr("class"));
                    addTable = true;
                    classTable = table;
                    break;
                } else if (table.id().toLowerCase().contains("sinottico")) {
                    Log.d(TAG, "doesn't contain id info = " + table.id());
                    addTable = true;
                    classTable = table;
                    break;
                } else if (languageURL.equals("fr") && table.selectFirst("tbody").child(0).child(0).siblingElements().size() == 1) {
                    infoTables.add(table.selectFirst("tbody"));
                }
            }

            if (addTable) {
                Element tableToBeAdded = classTable.selectFirst("tbody");
                if (tableToBeAdded.getElementsByTag("table") != null && tableToBeAdded.getElementsByTag("table").size() != 0) {
                    Elements infoboxes = tableToBeAdded.getElementsByTag("table");
                    infoTables.add(infoboxes.get(0));
                }
                infoTables.add(classTable.selectFirst("tbody"));

            }

        } catch (NullPointerException e) {
            //rozcestník
            e.printStackTrace();
        }

        return infoTables;
    }


}