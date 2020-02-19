package com.example.timad.poznavacka.activities.lists;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timad.poznavacka.FirestoreImpl;
import com.example.timad.poznavacka.PoznavackaInfo;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.Zastupce;
import com.example.timad.poznavacka.ZastupceAdapter;
import com.example.timad.poznavacka.activities.test.PoznavackaDbObject;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.sephiroth.android.library.xtooltip.ClosePolicy;
import it.sephiroth.android.library.xtooltip.Tooltip;

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
    private EditText userDividngString;
    private Switch autoImportSwitch;
    private Spinner languageSpinner;
    private ImageView infoTip;
    private TextView infoTextHolder;

    static String languageURL;

    private boolean switchPressedOnce;
    private boolean loadingRepresentative;
    private boolean autoImportIsChecked;
    private boolean listCreated;

    private FirestoreImpl firestoreImpl;
    private FirebaseFirestore db; //for testing no longer

    static ArrayList<String> representatives;
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
        final View view = inflater.inflate(R.layout.fragment_createlist, container, false);
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
        infoTip = view.findViewById(R.id.infoTip);
        infoTextHolder = view.findViewById(R.id.infoTextHolder);
        db = FirebaseFirestore.getInstance(); //testing
        switchPressedOnce = false;


        //info
        infoTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpannableString tooltipText = SpannableString.valueOf(getResources().getString(R.string.create_fragment_text));
                //tooltipText.setSpan(new UnderlineSpan(), 0, 15, 0);
                final Tooltip tooltip = new Tooltip.Builder(getContext())
                        .anchor(infoTextHolder, 0, 0, false)
                        .closePolicy(ClosePolicy.Companion.getTOUCH_ANYWHERE_CONSUME())
                        .showDuration(0)
                        .text(tooltipText)
                        .arrow(false)
                        .create();
                tooltip.show(infoTextHolder, Tooltip.Gravity.CENTER, false);
            }
        });

        /* RecyclerView */
        mRecyclerView = view.findViewById(R.id.recyclerViewZ);
        HorizontalScrollView scrollV = (HorizontalScrollView) mRecyclerView.getParent();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = (int) ((float) displayMetrics.heightPixels * 0.7f);
        //int width = (int) ((float) displayMetrics.widthPixels * 2f);

        //from https://stackoverflow.com/questions/19805981/android-layout-view-height-is-equal-to-screen-size
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) scrollV.getLayoutParams();
        params.height = height;
        scrollV.setLayoutParams(new RelativeLayout.LayoutParams(params));

        /*NestedScrollView.LayoutParams params2 = (NestedScrollView.LayoutParams) scrollV.getChildAt(0).getLayoutParams();
        params2.width = width;
        scrollV.getChildAt(0).setLayoutParams(new NestedScrollView.LayoutParams(params2));*/

        mZastupceArr = new ArrayList<>();

        mLManager = new LinearLayoutManager(getContext());

        //spinner
        languageURL = "Select Language";
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.language_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
        languageSpinner.getBackground().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        languageSpinner.setOnItemSelectedListener(this);

        //switch
        autoImportSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String rawRepresentatives = userInputRepresentatives.getText().toString();
                    dividingString = userDividngString.getText().toString().trim().toLowerCase();
                    title = userInputTitle.getText().toString().trim();
                    if (!(title.isEmpty() || dividingString.isEmpty() || rawRepresentatives.isEmpty() || languageURL.equals("Select Language"))) {

                        representatives = new ArrayList<>(Arrays.asList(rawRepresentatives.split("\\s*" + (dividingString) + "\\s*")));
                       /* if (dividingString.isEmpty()) { //single
                            exampleRepresentative = rawRepresentatives.trim();
                        } else { //multiple
                            if (rawRepresentatives.contains(dividingString)) {
                                exampleRepresentative = rawRepresentatives.substring(0, rawRepresentatives.indexOf(dividingString)).trim();
                            } else {  //single, but dividing string was entered
                                exampleRepresentative = rawRepresentatives.trim();
                            }
                        }*/

                        //exampleRepresentative = capitalize(exampleRepresentative, languageURL);
                        switchPressedOnce = true;
                        autoImportIsChecked = true;
                        Intent intent = new Intent(getContext(), PopActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getContext(), "Fill all the info", Toast.LENGTH_SHORT).show();
                        autoImportSwitch.setChecked(false);
                        autoImportIsChecked = false;
                    }
                } else {
                    autoImportIsChecked = false;
                }
            }
        });


        final WikiSearchRepresentatives[] WikiSearchRepresentatives = {new WikiSearchRepresentatives(CreateListFragment.this)};
        //create button
        btnCREATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                title = userInputTitle.getText().toString().trim();
                dividingString = userDividngString.getText().toString().trim().toLowerCase();
                String rawRepresentatives = userInputRepresentatives.getText().toString();

                if (!(title.isEmpty() || dividingString.isEmpty() || rawRepresentatives.isEmpty() || languageURL.equals("Select Language"))) {
                    representatives = new ArrayList<>(Arrays.asList(rawRepresentatives.split("\\s*" + dividingString + "\\s*")));
                    listCreated = false;
                    WikiSearchRepresentatives[0].cancel(true);
                    Toast.makeText(getActivity(), "Creating, please wait..", Toast.LENGTH_SHORT).show();
                    //final WikiSearchRepresentatives WikiSearchRepresentatives = new WikiSearchRepresentatives(CreateListFragment.this);

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
                    WikiSearchRepresentatives[0] = new WikiSearchRepresentatives(CreateListFragment.this);

                    WikiSearchRepresentatives[0].execute();
                } else {
                    Toast.makeText(getContext(), "Insert all info", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnSAVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listCreated) {

                    title = userInputTitle.getText().toString();

                    // Store images
                    Gson gson = new Gson();
                    Context context = getContext();
                    String uuid = UUID.randomUUID().toString();
                    String path = uuid + "/";
                    File dir = new File(context.getFilesDir().getPath() + "/" + path);

                    // Create folder
                    try {
                        dir.mkdir();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Failed to save " + title, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        return;
                    }

                    // Saves images locally
                    for (Zastupce z : mZastupceArr) {
                        if (z.getImage() != null) {
                            if (! MyListsFragment.getSMC(context).saveDrawable(z.getImage(), path, z.getParameter(0))) {
                                Toast.makeText(getActivity(), "Failed to save " + title, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            // TODO exception for first thing

                            /*Toast.makeText(getActivity(), "Failed to save " + title, Toast.LENGTH_SHORT).show(); EDIT
                            deletePoznavacka(dir);
                            return;*/
                        }
                        z.setImage(null);
                    }

                    // Saving mZastupceArr
                    String json = gson.toJson(mZastupceArr);
                    //add to file
                    String userName = "user";
                    // Add to database
                    PoznavackaDbObject item = new PoznavackaDbObject(title, uuid, json,userName);
                    SharedListsFragment.addToFireStore("Poznavacka", item, db);

                    //Log.d("Files", json);
                    if(! MyListsFragment.getSMC(context).createAndWriteToFile(path, uuid, json)){
                        Toast.makeText(getActivity(), "Failed to save " + title, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String pathPoznavacka = "poznavacka.txt";
                    if (MyListsFragment.sPoznavackaInfoArr == null) {
                        MyListsFragment.getSMC(context).readFile(pathPoznavacka, true);
                    }
                    MyListsFragment.sPoznavackaInfoArr.add(new PoznavackaInfo(title, uuid, userName, mZastupceArr.get(1).getParameter(0), mZastupceArr.get(1).getImageURL()));
                    MyListsFragment.getSMC(context).updatePoznavackaFile(pathPoznavacka, MyListsFragment.sPoznavackaInfoArr);

                    Log.d("Files", "Saved successfully");
                    Toast.makeText(getActivity(), "Successfully saved " + title, Toast.LENGTH_SHORT).show();

                    // Deletes everything base in folder
                /*File[] files = c.getFilesDir().listFiles();
                for (int i = 0; i < files.length; i++)
                {
                    if(files[i].isDirectory()){
                        Log.d("Files", files[i].getPath() + " : " + files[i].getName());
                        File[] files2 = files[i].listFiles();
                        for (int x = 0; x < files2.length; x++) {
                            //files2[x].delete();
                        }
                    }
                    //files[i].delete();
                }
                Log.d("Files", "Deleted "+ files.length + " files");*/

                /*for (int i = 0; i < representatives.size(); i++) {

                    // Uploading poznavacka
                    Map<String, Object> representativeInfo = new HashMap<>();
                    representativeInfo.put(KEY_ZASTUPCE, representatives.get(i));
                    representativeInfo.put(KEY_IMGREF, "imageRef - cislo/hash?");
                    if (autoImportSwitch.isChecked()) {
                        representativeInfo.put(KEY_DRUH, "dohledany druh");
                        representativeInfo.put(KEY_RAD, "dohledany rad");
                    }

                    firestoreImpl.uploadRepresentative(title, representatives.get(i), representativeInfo);

                }*/
                } else {
                    Toast.makeText(getContext(), "Create list first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //getting the language of wiki
        Object selectedLanguageSpinnerItem = parent.getItemAtPosition(position);
        String languageString = selectedLanguageSpinnerItem.toString();

        switch (languageString) {
            case "Select Language":
                languageURL = "Select Language";
                break;
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
        Toast.makeText(getContext(), "Select language", Toast.LENGTH_SHORT).show();
    }


    private static class WikiSearchRepresentatives extends AsyncTask<Void, String, Void> {

        private WeakReference<CreateListFragment> fragmentWeakReference;

        WikiSearchRepresentatives(CreateListFragment context) {
            fragmentWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... args) {
            final CreateListFragment fragment = fragmentWeakReference.get();

            if (!fragment.loadingRepresentative) {
                fragment.mZastupceArr.add(new Zastupce(userParametersCount, reversedUserScientificClassification));
                Log.d(TAG, "Classification added");
                fragment.loadingRepresentative = true;
                publishProgress("");
            }


            for (String representative :
                    representatives) {
                Log.d(TAG, "------------------------------");
                Log.d(TAG, "current representative = " + representative);
                String searchText = representative.replace(" ", "_");

                Document doc = null;
                try {
                    doc = Jsoup.connect("https://" + languageURL + ".wikipedia.org/api/rest_v1/page/html/" + URLEncoder.encode(searchText, "UTF-8") + "?redirect=true").userAgent("Mozilla").get();
                } catch (IOException e) {
                    //not connected to internet
                    e.printStackTrace();
                    Log.d(TAG, "no wiki");
                    publishProgress(representative);
                }


                //checking for the right table
                ArrayList<String> newData = new ArrayList<>();
                int classificationPointer = 0;
                Drawable img = null;
                String imageURL = "";

                Element infoBox = null;

                if (doc != null && doc.head().hasText()) {
                    boolean redirects = false;
                    try {
                        Elements rawTables = doc.getElementsByTag("table");
                        redirects = true;
                        for (Element table :
                                rawTables) {
                            if (languageURL.equals("en") || languageURL.equals("cs")) {
                                if (table.id().contains("info")) {
                                    Log.d(TAG, "does contain id info = " + table.id());
                                    infoBox = table.selectFirst("tbody");
                                    redirects = false;
                                    break;
                                } else if (table.attr("class").contains("info")) {
                                    Log.d(TAG, "does contain class info = " + table.attr("class"));
                                    infoBox = table.selectFirst("tbody");
                                    redirects = false;
                                    break;
                                }

                            } else if (languageURL.equals("de")) {

                                if (table.id().contains("taxo") || table.id().contains("Taxo")) {
                                    Log.d(TAG, "does contain id info = " + table.id());
                                    infoBox = table.selectFirst("tbody");
                                    redirects = false;
                                    break;
                                } else if (table.attr("class").contains("taxo")) {
                                    Log.d(TAG, "does contain class info = " + table.attr("class"));
                                    infoBox = table.selectFirst("tbody");
                                    redirects = false;
                                    break;
                                }
                            }

                        }

                    } catch (NullPointerException e) {
                        //rozcestník
                        redirects = true;
                        e.printStackTrace();
                    }


                    //if it is a redirecting site
                    if (redirects) {
                        ArrayList redirectedSiteAndTable = redirect_getTable(doc);
                        if (redirectedSiteAndTable.get(2).equals(false)) { //if new site is not found
                            fragment.mZastupceArr.add(new Zastupce(userParametersCount, fragment.capitalize(representative, languageURL)));
                            publishProgress(representative);
                            continue;
                        } else {
                            doc = (Document) redirectedSiteAndTable.get(0);
                            infoBox = (Element) redirectedSiteAndTable.get(1);
                        }
                    }

                    //harvesting the infoBox
                    if (infoBox != null) {
                        ArrayList harvestedInfoBox = harvestInfo(infoBox);
                        newData = (ArrayList<String>) harvestedInfoBox.get(0);
                        classificationPointer = (int) harvestedInfoBox.get(1);
                        img = (Drawable) harvestedInfoBox.get(2);
                        imageURL = (String) harvestedInfoBox.get(3);
                    }


                    newData.add(doc.title());
                    Collections.reverse(newData);

                    //loading into mZastupceArr

                    //loading representative
                    if (detectedWrongTable(classificationPointer)) { //if detected wrong table but on the correct site
                        for (int i = 0; i < userParametersCount - 1; i++) {
                            newData.add("");
                        }
                    }
                    if (img == null) {
                        //get only the image
                        ArrayList imgAndUrl = getImageFromSite(doc);
                        img = (Drawable) imgAndUrl.get(0);
                        imageURL = (String) imgAndUrl.get(1);
                    }

                    if (img == null) {
                        fragment.mZastupceArr.add(new Zastupce(userParametersCount, newData));
                    } else {
                        fragment.mZastupceArr.add(new Zastupce(userParametersCount, img, imageURL, newData));
                    }
                    Log.d(TAG, "newData size for representative= " + newData.size() + "\n\n");

                } else {
                    //add an empty only with representative
                    fragment.mZastupceArr.add(new Zastupce(userParametersCount, fragment.capitalize(representative, languageURL)));
                    Log.d(TAG, "Wiki for " + representative + " doesn't exist or you might have misspelled");
                }
                publishProgress("");
            }
            return null;
        }

        private ArrayList redirect_getTable(Document doc) {
            ArrayList returnDocAndInfobox = new ArrayList();
            Element infoBox = null;
            CreateListFragment fragment = fragmentWeakReference.get();

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
                                        Log.d(TAG, "does contain id info = " + table.id());
                                        infoBox = table.selectFirst("tbody");
                                        break;
                                    } else if (table.attr("class").contains("info")) {
                                        Log.d(TAG, "does contain class info = " + table.attr("class"));
                                        infoBox = table.selectFirst("tbody");
                                        break;
                                    }
                                    //de infoTable
                                } else if (languageURL.equals("de")) {

                                    if (table.id().contains("taxo") || table.id().contains("Taxo")) {
                                        Log.d(TAG, "does contain id info = " + table.id());
                                        infoBox = table.selectFirst("tbody");
                                        break;
                                    } else if (table.attr("class").contains("taxo")) {
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

        private ArrayList<String> harvestInfo(Element infoBox) {

            ArrayList returnList = new ArrayList();

            ArrayList<String> newData = new ArrayList<>();
            Drawable img = null;
            String imageURL = "";
            int classificationPointer = 0;

            String[] dataPair;
            boolean imageDetected = false;
            int trCounter = 0;
            CreateListFragment fragment = fragmentWeakReference.get();


            //Element infoBox = doc.getElementsByTag("table").first().selectFirst("tbody");
            Elements trs = infoBox.select("tr");
            int trsWihtoutColspan = 0;
            int currentTrsWihtoutColspan = 0;
            for (Element tr :
                    trs) {
                if (!tr.getAllElements().hasAttr("colspan")) trsWihtoutColspan++;
            }
            for (Element tr :
                    trs) {

                trCounter++;
                Log.d(TAG, "current tr = " + trCounter);
                if (!tr.getAllElements().hasAttr("colspan") && fragment.autoImportIsChecked && !(userScientificClassification.size() <= classificationPointer)) {
                    trsWihtoutColspan++;
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

                    //LEFT OFF, u Marsu naor, class, colspan=2, class.. prestane prirazovat klasifikaci

                    Log.d(TAG, "classPointer = " + classificationPointer);
                    Log.d(TAG, userScientificClassification.get(classificationPointer) + " ?equals = " + dataPair[0]);
                    Log.d(TAG, "userSciClass[0] = " + userScientificClassification.get(0));

                    if (userScientificClassification.get(classificationPointer).equals(dataPair[0])) { //detected searched classification
                        if (dataPair[1].contains("(")) {
                            dataPair[1] = dataPair[1].substring(0, dataPair[1].indexOf("(")).trim();
                        }
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
                        if (dataPair[1].contains("(")) {
                            dataPair[1] = dataPair[1].substring(0, dataPair[1].indexOf("(")).trim();
                        }
                        newData.add(dataPair[1]);
                        Log.d(TAG, "adding new data to newData = " + dataPair[1]);
                    }

                } else if (currentTrsWihtoutColspan == trsWihtoutColspan) { //current tr is the last one with information
                    if (newData.size() == (userParametersCount - 2)) { //the last parameter was not detected
                        Log.d(TAG, "last parameter not detected");
                        newData.add("");
                        break;
                    }
                }


                //get the image
                else if (img == null) {
                    ArrayList imgAndUrl = getImageFromTr(tr);
                    img = (Drawable) imgAndUrl.get(0);
                    imageURL = (String) imgAndUrl.get(1);
                }
            }
            returnList.add(newData);
            returnList.add(classificationPointer);
            returnList.add(img);
            returnList.add(imageURL);
            return returnList;
        }

        private ArrayList getImageFromSite(Document doc) {
            ArrayList imgAndURL = new ArrayList();
            Drawable img = null;
            String imageURL = null;
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
                e.printStackTrace();
            }
            imgAndURL.add(img);
            imgAndURL.add(imageURL);
            return imgAndURL;
        }

        //checks for indicators of wrong table
        private boolean detectedWrongTable(int classificationPointer) {
            if (classificationPointer == 0) {
                return true;
            }
            return false;
        }

        private ArrayList getImageFromTr(Element tr) {
            ArrayList imgAndUrl = new ArrayList();
            Drawable img = null;
            String imageURL = null;
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
                    //imageDetected = true;
                }
            }
            imgAndUrl.add(img);
            imgAndUrl.add(imageURL);
            return imgAndUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CreateListFragment fragment = fragmentWeakReference.get();
            fragment.progressBar.setVisibility(View.VISIBLE);
            fragment.progressBar.startAnimation(AnimationUtils.loadAnimation(fragment.getContext(), android.R.anim.fade_in));

            fragment.mZastupceArr = new ArrayList<>();
            fragment.mLManager = new LinearLayoutManager(fragment.getContext());
            if (fragment.autoImportIsChecked) {
                fragment.mAdapter = new ZastupceAdapter(fragment.mZastupceArr, userParametersCount);
            } else {
                fragment.mAdapter = new ZastupceAdapter(fragment.mZastupceArr, 1);
            }
            fragment.mRecyclerView.setLayoutManager(fragment.mLManager);
            fragment.mRecyclerView.setAdapter(fragment.mAdapter);

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            CreateListFragment fragment = fragmentWeakReference.get();
            if (!values[0].equals("")) {
                Toast.makeText(fragment.getActivity(), "No Wiki for " + values[0], Toast.LENGTH_SHORT).show();
                fragment.mAdapter.notifyDataSetChanged();
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
            fragment.listCreated = true;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            CreateListFragment fragment = fragmentWeakReference.get();
            fragment.mZastupceArr.clear();
        }

        Drawable drawable_from_url(String url) throws java.io.IOException {
            CreateListFragment fragment = fragmentWeakReference.get();

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-agent", "Mozilla");

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

    public ArrayList<String> capitalize(ArrayList<String> representatives, String languageURL) {
        if (!(languageURL.equals("ar") || languageURL.equals("kr") || languageURL.equals("ru") || languageURL.equals("vi"))) {
            for (int i = 0; i < representatives.size(); i++) {
                String withUpperLetter = representatives.get(i).substring(0, 1).toUpperCase() + representatives.get(i).substring(1).toLowerCase();
                representatives.add(i, withUpperLetter);
            }
        } else {
            Locale locale = Locale.getDefault();
            switch (languageURL) {
                case "ar":
                    locale = new Locale("ar");
                    break;
                case "kr":
                    locale = new Locale("kr");
                    break;
                case "ru":
                    locale = new Locale("ru");
                    break;
                case "vi":
                    locale = new Locale("vi");
                    break;
            }
            for (int i = 0; i < representatives.size(); i++) {
                String withUpperLetter = representatives.get(i).substring(0, 1).toUpperCase(locale) + representatives.get(i).substring(1).toLowerCase();
                representatives.add(i, withUpperLetter);
            }
        }
        return representatives;
    }

    private String capitalize(String representative, String languageURL) {
        if (!(languageURL.equals("ar") || languageURL.equals("kr") || languageURL.equals("ru") || languageURL.equals("vi"))) {
            representative = representative.substring(0, 1).toUpperCase() + representative.substring(1).toLowerCase();
        } else {
            Locale locale = Locale.getDefault();
            switch (languageURL) {
                case "ar":
                    locale = new Locale("ar");
                    break;
                case "kr":
                    locale = new Locale("kr");
                    break;
                case "ru":
                    locale = new Locale("ru");
                    break;
                case "vi":
                    locale = new Locale("vi");
                    break;
            }
            representative = representative.substring(0, 1).toUpperCase(locale) + representative.substring(1).toLowerCase();
        }
        return representative;
    }
}


