package com.example.timad.poznavacka.activities.lists.createList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timad.poznavacka.BuildConfig;
import com.example.timad.poznavacka.DBTestObject;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.Zastupce;
import com.example.timad.poznavacka.ZastupceAdapter;
import com.example.timad.poznavacka.activities.lists.MyExamsActivity;
import com.example.timad.poznavacka.activities.lists.MyListsActivity;
import com.example.timad.poznavacka.activities.lists.SharedListsActivity;
import com.example.timad.poznavacka.google_search_objects.GoogleItemObject;
import com.example.timad.poznavacka.google_search_objects.GoogleSearchObject;
import com.example.timad.poznavacka.google_search_objects.GoogleSearchObjectAutoCorrect;
import com.example.timad.poznavacka.google_search_objects.Spelling;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

import static com.example.timad.poznavacka.activities.lists.createList.CreateListActivity.languageURL;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GeneratedListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GeneratedListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneratedListFragment extends Fragment {
    private static final String TAG = "GeneratedListFragment";


    // the fragment initialization parameters
    private static final String ARG_REPRESENTATIVES = "representatives";
    private static final String ARG_AUTOIMPORTISCHECKED = "autoImportIsChecked";
    private static final String ARG_LANGUAGEURL = "languageURL";
    private static final String ARG_USERPARAMETERSCOUNT = "userParametersCount";
    private static final String ARG_USERSCIENTIFICCLASSIFICATION = "userScientificClassification";
    private static final String ARG_REVERSEDUSERSCIENTIFICCLASSIFICATION = "reversedUserScientificClassification";

    // parameters
    private ArrayList<String> representatives;
    private boolean autoImportIsChecked;
    private String languageURL;
    private int userParametersCount;
    private ArrayList<String> userScientificClassification;
    private ArrayList<String> originalUserScientificClassification;
    private ArrayList<String> reversedUserScientificClassification;

    private OnFragmentInteractionListener mListener;

    public static WikiSearchRepresentatives wikiSearchRepresentatives;

    private boolean loadingRepresentative;
    private boolean listCreated;

    private Button btnSAVE;
    private Button btnCancel;
    private ProgressBar progressBar;

    //RecyclerView
    private RecyclerView mRecyclerView;
    private ZastupceAdapter mAdapter;
    private RecyclerView.LayoutManager mLManager;
    private ArrayList<Zastupce> mZastupceArr;

    public GeneratedListFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param autoImportIsChecked Parameter 1.
     * @return A new instance of fragment GeneratedListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneratedListFragment newInstance(ArrayList<String> representatives, Boolean autoImportIsChecked, int userParametersCount, ArrayList<String> userScientificClassification, ArrayList<String> reversedUserScientificClassification, String languageURL) {
        GeneratedListFragment fragment = new GeneratedListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_REPRESENTATIVES, representatives);
        args.putBoolean(ARG_AUTOIMPORTISCHECKED, autoImportIsChecked);
        args.putString(ARG_LANGUAGEURL, languageURL);
        args.putInt(ARG_USERPARAMETERSCOUNT, userParametersCount);
        args.putStringArrayList(ARG_USERSCIENTIFICCLASSIFICATION, userScientificClassification);
        args.putStringArrayList(ARG_REVERSEDUSERSCIENTIFICCLASSIFICATION, reversedUserScientificClassification);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mZastupceArr = savedInstanceState.getParcelableArrayList("MZASTUPCEARR");
            //TODO restore
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("MZASTUPCEARR", mZastupceArr);
        //TODO save
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        if (getArguments() != null) {
            representatives = getArguments().getStringArrayList(ARG_REPRESENTATIVES);
            autoImportIsChecked = getArguments().getBoolean(ARG_AUTOIMPORTISCHECKED);
            languageURL = getArguments().getString(ARG_LANGUAGEURL);
            userParametersCount = getArguments().getInt(ARG_USERPARAMETERSCOUNT);
            originalUserScientificClassification = getArguments().getStringArrayList(ARG_USERSCIENTIFICCLASSIFICATION);
            reversedUserScientificClassification = getArguments().getStringArrayList(ARG_REVERSEDUSERSCIENTIFICCLASSIFICATION);
            Timber.d(TAG + "originalUserScientificClassification = " + originalUserScientificClassification.toString());
        }

        if (userParametersCount < 4) {
            Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onStart() {
        super.onStart();

        btnSAVE = Objects.requireNonNull(getView()).findViewById(R.id.button_save_new);
        if (listCreated) {
            btnSAVE.setVisibility(View.VISIBLE);
        } else {
            btnSAVE.setVisibility(View.INVISIBLE);
        }
        btnCancel = getView().findViewById(R.id.button_cancel_generated);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyListsActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                getActivity().finish();
            }
        });
        progressBar = getView().findViewById(R.id.progressBar_new);
        mRecyclerView = getView().findViewById(R.id.recyclerViewZ);

        btnSAVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(mZastupceArr);
            }
        });

        //force orientation

        /* RecyclerView */
        HorizontalScrollView scrollV = (HorizontalScrollView) mRecyclerView.getParent();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = (int) ((float) displayMetrics.heightPixels * 1.0f);
        //int width = (int) ((float) displayMetrics.widthPixels * 2f);

        //from https://stackoverflow.com/questions/19805981/android-layout-view-height-is-equal-to-screen-size
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) scrollV.getLayoutParams();
        params.height = height;
        scrollV.setLayoutParams(new RelativeLayout.LayoutParams(params));
        if (mZastupceArr == null) {
            mZastupceArr = new ArrayList<>();
            Toast.makeText(getActivity(), "Creating, please wait...", Toast.LENGTH_LONG).show();
        }
        mLManager = new LinearLayoutManager(getContext());

        //new generation?..
        //listCreated = false;

        Timber.d("automImportIsChecked is %s", autoImportIsChecked);
        if (autoImportIsChecked) {
            mAdapter = new ZastupceAdapter(mZastupceArr, userParametersCount);
            loadingRepresentative = false;
        } else {
            mAdapter = new ZastupceAdapter(mZastupceArr, 1);
            loadingRepresentative = true;
        }
        mRecyclerView.setLayoutManager(mLManager);
        mRecyclerView.setAdapter(mAdapter);
        setOnClickListener();

        /* generation */
        if (mZastupceArr.size() == 0) {
            wikiSearchRepresentatives = new WikiSearchRepresentatives(GeneratedListFragment.this);
            wikiSearchRepresentatives.execute();
        }

    }

    public static void cancelWikiSearchRepresentativesAsync() {
        wikiSearchRepresentatives.cancel(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_generated_list, container, false);
    }

    public void onButtonPressed(ArrayList<Zastupce> mZastupceArr) {
        if (mListener != null) {
            mListener.onSave(mZastupceArr);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    private String stringArrayToString(String[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i == 0) {
                sb.append(array[i]);
            } else {
                sb.append(", ").append(array[i]);
            }
        }
        return sb.toString();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onSave(ArrayList<Zastupce> mZastupceArr);
    }

    private class WikiSearchRepresentatives extends AsyncTask<Void, String, Void> {

        private WeakReference<GeneratedListFragment> fragmentWeakReference;

        WikiSearchRepresentatives(GeneratedListFragment context) {
            fragmentWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... args) {
            final GeneratedListFragment fragment = fragmentWeakReference.get();

            if (!fragment.loadingRepresentative) {
                fragment.mZastupceArr.add(new Zastupce(userParametersCount, reversedUserScientificClassification));
                Timber.d("Classification 1 added");
                fragment.loadingRepresentative = true;
                publishProgress("");
            }

            /*fragment.mZastupceArr.add(new Zastupce(userParametersCount, reversedUserScientificClassification));
            Timber.d("Classification 2 added");*/

            allRepresentatives:
            for (String representative :
                    representatives) {
                userScientificClassification = (ArrayList<String>) originalUserScientificClassification.clone();
                String searchText = representative.trim().replace(" ", "_");
                Drawable img = null;
                String imageURL = "";
                String googleSearchRepresentative = representative;
                boolean searchSuccessful = false;
                while (!searchSuccessful) {
                    searchSuccessful = true;
                    Timber.d("------------------------------");
                    Timber.d("current representative = %s", representative);
                    Timber.d("");

                    //Google search
                    String result = "";
                    String urlString = "https://www.googleapis.com/customsearch/v1/siterestrict?key=AIzaSyCaQxGMMIGJOj-XRgfzR6me0e70IZ8qR38&cx=011868713606192238742:phdn1jengcl&lr=lang_" + languageURL + "&q=" + googleSearchRepresentative;
                    URL url = null;
                    try {
                        url = new URL(urlString);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        Timber.d("no wiki");
                        fragment.mZastupceArr.add(new Zastupce(userParametersCount, getResources().getDrawable(R.drawable.ic_image_black_24dp), "", fragment.capitalize(representative, languageURL)));
                        publishProgress(representative);
                        continue;
                    }
                    HttpURLConnection conn = null;
                    try {
                        conn = (HttpURLConnection) url.openConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Timber.d("no wiki");
                        fragment.mZastupceArr.add(new Zastupce(userParametersCount, getResources().getDrawable(R.drawable.ic_image_black_24dp), "", fragment.capitalize(representative, languageURL)));
                        publishProgress(representative);
                        continue;
                    }
                    Integer responseCode = null;
                    String responseMessage = "";
                    try {
                        responseCode = conn.getResponseCode();
                        responseMessage = conn.getResponseMessage();
                    } catch (IOException e) {
                        Log.e(TAG, "Http getting response code ERROR " + e.toString());
                        Timber.d("no wiki");
                        fragment.mZastupceArr.add(new Zastupce(userParametersCount, getResources().getDrawable(R.drawable.ic_image_black_24dp), "", fragment.capitalize(representative, languageURL)));
                        publishProgress(representative);
                        continue;

                    }

                    Timber.d("Http response code =" + responseCode + " message=" + responseMessage);

                    try {
                        if (responseCode != null && responseCode == 200) {
                            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = rd.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            rd.close();
                            conn.disconnect();
                            result = sb.toString();
                            //Timber.d("result=" + result);
                        } else {
                            //response problem

                            String errorMsg = "Http ERROR response " + responseMessage + "\n" + "Are you online ? " + "\n" + "Make sure to replace in code your own Google API key and Search Engine ID";
                            Log.e(TAG, errorMsg);
                            Timber.d("no wiki");
                            fragment.mZastupceArr.add(new Zastupce(userParametersCount, getResources().getDrawable(R.drawable.ic_image_black_24dp), "", fragment.capitalize(representative, languageURL)));
                            publishProgress(representative);
                            continue;
                            //result = errorMsg;
                        }

                    } catch (IOException e) {
                        Log.e(TAG, "Http Response ERROR " + e.toString());
                        Timber.d("no wiki");
                        fragment.mZastupceArr.add(new Zastupce(userParametersCount, getResources().getDrawable(R.drawable.ic_image_black_24dp), "", fragment.capitalize(representative, languageURL)));
                        publishProgress(representative);
                        continue;
                    }


                    Gson gson = new Gson();
                    GoogleSearchObject googleSearchObject = gson.fromJson(result, GoogleSearchObject.class);
                    GoogleItemObject googleItemObject = new GoogleItemObject();
                    try {
                        googleItemObject = googleSearchObject.getItems().get(0);
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
                    String wikipeidaURL = googleItemObject.getFormattedUrl();
                    Timber.d("google test is = " + wikipeidaURL);
                    try {
                        imageURL = googleItemObject.getPagemap().getCse_image().get(0).getSrc();
                    } catch (Exception e) {
                        //get image from different search
                        String imgResult = "";
                        String imgUrlString = "https://www.googleapis.com/customsearch/v1?key=AIzaSyCaQxGMMIGJOj-XRgfzR6me0e70IZ8qR38&cx=011868713606192238742:xncw829zqmv&search_type=image&lr=lang_" + languageURL + "&q=" + googleSearchRepresentative;
                        URL imgUrl = null;
                        try {
                            imgUrl = new URL(imgUrlString);
                        } catch (MalformedURLException ex) {
                            ex.printStackTrace();
                        }
                        HttpURLConnection imgconn = null;
                        try {
                            imgconn = (HttpURLConnection) imgUrl.openConnection();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        try {
                            responseCode = imgconn.getResponseCode();
                            responseMessage = imgconn.getResponseMessage();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        Timber.d("Http response code =" + responseCode + " message=" + responseMessage);
                        try {
                            if (responseCode != null && responseCode == 200) {
                                BufferedReader rd = new BufferedReader(new InputStreamReader(imgconn.getInputStream()));
                                StringBuilder sb = new StringBuilder();
                                String line;
                                while ((line = rd.readLine()) != null) {
                                    sb.append(line + "\n");
                                }
                                rd.close();
                                imgconn.disconnect();
                                imgResult = sb.toString();
                                //Timber.d("result=" + imgResult);
                            } else {
                                //response problem
                                String errorMsg = "Http ERROR response " + responseMessage + "\n" + "Are you online ? " + "\n" + "Make sure to replace in code your own Google API key and Search Engine ID";
                                Log.e(TAG, errorMsg);
                                Timber.d("no wiki");
                                continue;
                            }
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                        e.printStackTrace();
                        GoogleSearchObject imggoogleSearchObject = gson.fromJson(imgResult, GoogleSearchObject.class);
                        GoogleItemObject imggoogleItemObject = imggoogleSearchObject.getItems().get(0);
                        imageURL = imggoogleItemObject.getLink();
                    }
                    try {
                        searchText = wikipeidaURL.substring(wikipeidaURL.indexOf("/wiki/") + 6);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue allRepresentatives;
                    }
                }

                try {
                    img = drawable_from_url(imageURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Document doc = null;
                try {
                    Timber.d("connecting after google to = " + "https://" + languageURL + ".wikipedia.org/api/rest_v1/page/html/" + searchText /*URLEncoder.encode(searchText, "UTF-8")*/ + "?redirect=true");
                    doc = Jsoup.connect("https://" + languageURL + ".wikipedia.org/api/rest_v1/page/html/" + URLEncoder.encode(searchText, "UTF-8") + "?redirect=true").userAgent("Mozilla").get();
                } catch (IOException e) {
                    //not connected to internet
                    e.printStackTrace();
                    Timber.d("no wiki");
                    fragment.mZastupceArr.add(new Zastupce(userParametersCount, fragment.capitalize(representative, languageURL)));
                    publishProgress(representative);
                    continue;
                }


                //checking for the right table
                if (doc != null && doc.head().hasText()) {
                    String[] newData = new String[userParametersCount];
                    ArrayList<Element> infoTables = getInfoTables(doc);
                    if (infoTables.size() == 0) {
                        continue;
                    } else {
                        newData = harvestInfo(infoTables);
                    }
                    Timber.d("1newData array is ->");
                    for (String data :
                            newData) {
                        Timber.d("1newData array is -> " + data);
                    }

                    //setting display name of representative
                    String displayNameOfRepresentative;
                    if (doc.title().toLowerCase().contains("image")) {
                        displayNameOfRepresentative = capitalize(representative, languageURL);
                    } else {
                        displayNameOfRepresentative = doc.title();
                        if (doc.title().contains("(")) {
                            displayNameOfRepresentative = doc.title().substring(0, doc.title().indexOf("(")).trim();
                        }
                    }
                    newData[userParametersCount - 1] = displayNameOfRepresentative;

                    if (img == null) {
                        //get only the image
                        ArrayList imgAndUrl = getImageFromSite(doc);
                        img = (Drawable) imgAndUrl.get(0);
                        imageURL = (String) imgAndUrl.get(1);
                    }

                    ArrayList<String> newDataArrayList = new ArrayList<>();
                    for (int i = userParametersCount - 1; i >= 0; i--) {
                        newDataArrayList.add(newData[i]);
                    }


                    if (img == null) {
                        fragment.mZastupceArr.add(new Zastupce(userParametersCount, newDataArrayList));
                    } else {
                        fragment.mZastupceArr.add(new Zastupce(userParametersCount, img, imageURL, newDataArrayList));
                    }
                    Timber.d("newData size for representative " + representative + "= " + newDataArrayList.size() + "\n\n");

                } else {
                    //add an empty only with representative
                    fragment.mZastupceArr.add(new Zastupce(userParametersCount, getResources().getDrawable(R.drawable.ic_image_black_24dp), "", fragment.capitalize(representative, languageURL)));
                    Timber.d("Wiki for " + representative + " doesn't exist or you might have misspelled");
                }
                publishProgress("");
            }

            return null;
        }

        private ArrayList redirect_getTable(Document doc) {
            ArrayList returnDocAndInfobox = new ArrayList();
            Element infoBox = null;
            GeneratedListFragment fragment = fragmentWeakReference.get();

            Timber.d("ROZCESTNÍK");
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
                        Timber.d("redirected and connecting to new wikipedia for %s", newSearchText);
                        doc = Jsoup.connect("https://" + languageURL + ".wikipedia.org/api/rest_v1/page/html/" + URLEncoder.encode(newSearchText, "UTF-8") + "?redirects=true").userAgent("Mozilla").get();

                        try {
                            Elements rawTables = doc.getElementsByTag("table");
                            for (Element table :
                                    rawTables) {

                                //cz and en infoTable
                                if (languageURL.equals("en") || languageURL.equals("cs")) {
                                    if (table.id().contains("info")) {
                                        Timber.d("does contain id info = " + table.id());
                                        infoBox = table.selectFirst("tbody");
                                        break;
                                    } else if (table.attr("class").contains("info")) {
                                        Timber.d("does contain class info = " + table.attr("class"));
                                        infoBox = table.selectFirst("tbody");
                                        break;
                                    }

                                    //de infoTable
                                } else if (languageURL.equals("de")) {

                                    if (table.id().contains("taxo") || table.id().contains("Taxo")) {
                                        Timber.d("does contain id info = " + table.id());
                                        infoBox = table.selectFirst("tbody");
                                        break;
                                    } else if (table.attr("class").contains("taxo")) {
                                        Timber.d("does contain class info = " + table.attr("class"));
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
                            Timber.d("no wiki (redirect)");
                        }

                    } catch (IOException er) {
                        //this probably won't happen
                        er.printStackTrace();
                        Timber.d("no wiki (redirect)");
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

        private String[] harvestInfo(ArrayList<Element> infoTables) {

            String[] newData = new String[userParametersCount];
            String[] dataPair = new String[2];
            GeneratedListFragment fragment = fragmentWeakReference.get();

            for (Element infoTable :
                    infoTables) {

                Elements trs = infoTable.select("tr");

                //actual scanning
                for (Element tr :
                        trs) {
                    if (tr.childrenSize() != 0 && tr.child(0).siblingElements().size() == 1 && fragment.autoImportIsChecked && !(userScientificClassification.size() == 0)) {
                        String th = tr.children().first().text();/*
                        if (languageURL.equals("de")) {
                            if (th.contains(":")) {
                                th = th.replace(":", "");
                            }
                        }*/
                        if (th.isEmpty()) continue;
                        if (th.trim().substring(th.length() - 1, th.length()).contentEquals(":")) {
                            th = th.replace(":", "");
                        }
                        dataPair[0] = th;
                        Log.d(TAG, "found " + th);
                        String td = tr.children().last().wholeText();
                        dataPair[1] = td;
                        Log.d(TAG, "found " + td);

                        dataPair[1] = beautifyValues(dataPair[1]);

                        if (userScientificClassification.contains(dataPair[0])) {
                            Timber.d("Adding classification at " + userScientificClassification.indexOf(dataPair[0]) + "with value " + dataPair[1]);
                            newData[userScientificClassification.indexOf(dataPair[0])] = dataPair[1];
                        }
                    }

                    Timber.d("newData array is ->");
                    for (String data :
                            newData) {
                        Timber.d("newData array is -> " + data);
                    }
                }
            }

            return newData;
        }

        private String beautifyValues(String s) {

            ArrayList<String> data = new ArrayList<>();

            //splitting by new lines
            for (String piece :
                    s.split("\n")) {
                if (!piece.trim().isEmpty()) {
                    if (piece.contains("(")) {
                        piece = piece.substring(0, piece.indexOf("("));
                    }
                    data.add(piece);

                }
            }

            StringBuilder values = new StringBuilder();
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).contains("[")) {
                    String editedString = data.get(i).substring(0, data.get(i).indexOf("[")).trim();
                    data.set(i, editedString);
                }
                if (i == 0) {
                    values.append(data.get(i));
                } else {
                    values.append(", ").append(data.get(i));
                }

            }
            return values.toString();
        }

        private ArrayList getImageFromSite(Document doc) {
            ArrayList imgAndURL = new ArrayList();
            Drawable img = null;
            String imageURL = null;
            try {
                Element imgElement = doc.getElementsByAttributeValueContaining("typeof", "Image/Thumb").first().getElementsByAttributeValue("data-file-type", "bitmap").first();
                if (imgElement != null) {
                    imageURL = imgElement.absUrl("src");
                    Timber.d("getting only image - URL = " + imageURL);
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
                    Timber.d("IMAGEURL  ===   " + imageURL);
                    try {
                        img = drawable_from_url(imageURL);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Timber.d("image downloaded");
                    //imageDetected = true;
                }
            }
            imgAndUrl.add(img);
            imgAndUrl.add(imageURL);
            return imgAndUrl;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            GeneratedListFragment fragment = fragmentWeakReference.get();
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
            setOnClickListener();
            listCreated = false;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            GeneratedListFragment fragment = fragmentWeakReference.get();
            if (!values[0].equals("")) {
                Toast.makeText(fragment.getActivity(), "No Wiki for " + values[0], Toast.LENGTH_SHORT).show();
                fragment.mAdapter.notifyDataSetChanged();
                fragment.mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
            } else {
                fragment.mAdapter.notifyDataSetChanged();
                fragment.mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            GeneratedListFragment fragment = fragmentWeakReference.get();
            fragment.progressBar.setVisibility(View.GONE);
            fragment.progressBar.startAnimation(AnimationUtils.loadAnimation(fragment.getContext(), android.R.anim.fade_out));
            fragment.mAdapter.notifyDataSetChanged();
            fragment.listCreated = true;
            fragment.btnSAVE.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            GeneratedListFragment fragment = fragmentWeakReference.get();
            fragment.mZastupceArr.clear();
        }

        Drawable drawable_from_url(String url) throws java.io.IOException {
            GeneratedListFragment fragment = fragmentWeakReference.get();

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-agent", "Mozilla");

            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return new BitmapDrawable(Objects.requireNonNull(fragment.getActivity()).getResources(), bitmap);
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
                } else if (languageURL.equals("fr") && table.selectFirst("tbody").child(0).childrenSize() == 2) {
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

    public void setOnClickListener() {
        mAdapter.setOnItemClickListener(new ZastupceAdapter.OnItemClickListener() {
            @Override
            public void onViewClick(final int position) {
                Zastupce currentZastupce = mZastupceArr.get(position);
                final String[] userImgURL = new String[1];

                if (SharedListsActivity.checkInternet(getActivity())) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Image for " + currentZastupce.getParameter(0))
                            .setIcon(R.drawable.ic_image_black_24dp)
                            .setItems(R.array.image_import_options, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            LayoutInflater factory = getLayoutInflater();
                                            View textEntryView = factory.inflate(R.layout.image_url_input_dialog, null);
                                            final EditText userURLInput = textEntryView.findViewById(R.id.url_input);
                                            AlertDialog.Builder URLInputBuilder = new AlertDialog.Builder(getActivity());
                                            URLInputBuilder.setView(textEntryView);
                                            URLInputBuilder.setTitle("Enter URL");
                                            URLInputBuilder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    userImgURL[0] = userURLInput.getText().toString().trim();

                                                    new DrawableFromURLAsync(userImgURL[0], position).execute();


                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            final AlertDialog alert = URLInputBuilder.create();
                                            alert.show();

                                            userURLInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                                @Override
                                                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                                                    if ((keyEvent != null) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) || (i == EditorInfo.IME_ACTION_DONE)) {
                                                        userImgURL[0] = userURLInput.getText().toString().trim();

                                                        new DrawableFromURLAsync(userImgURL[0], position).execute();

                                                        alert.dismiss();
                                                    }

                                                    return false;
                                                }
                                            });

                                            break;
                                        case 1:

                                            break;
                                    }
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Toast.makeText(getActivity(), "reconnect!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteClick(int position) {
                Zastupce currentZastupce = mZastupceArr.get(position);
                mZastupceArr.remove(position);
                mAdapter.notifyItemRemoved(position);
            }
        });
    }

    private class DrawableFromURLAsync extends AsyncTask<Void, Void, Void> {
        String imgURL;
        int position;
        boolean newImageImported;

        DrawableFromURLAsync(String imgURL, int position) {
            this.imgURL = imgURL;
            this.position = position;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Drawable img = null;
            try {
                img = drawable_from_url(imgURL);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            Timber.d("ImgURL is %s", imgURL);
            mZastupceArr.get(position).setImage(img);
            mZastupceArr.get(position).setImageURL(imgURL);
            newImageImported = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (newImageImported) {
                mAdapter.notifyItemChanged(position);
            } else {
                Toast.makeText(getActivity(), "Invalid URL", Toast.LENGTH_SHORT).show();
            }
        }

        Drawable drawable_from_url(String url) throws java.io.IOException {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-agent", "Mozilla");

            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            Timber.d("Obrazek stahnut");
            return new BitmapDrawable(Objects.requireNonNull(getActivity()).getResources(), bitmap);
        }
    }
}
