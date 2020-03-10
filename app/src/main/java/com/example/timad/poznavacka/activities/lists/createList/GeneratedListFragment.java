package com.example.timad.poznavacka.activities.lists.createList;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.timad.poznavacka.BuildConfig;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.Zastupce;
import com.example.timad.poznavacka.ZastupceAdapter;
import com.example.timad.poznavacka.google_search_objects.GoogleItemObject;
import com.example.timad.poznavacka.google_search_objects.GoogleSearchObject;
import com.example.timad.poznavacka.google_search_objects.GoogleSearchObjectAutoCorrect;
import com.example.timad.poznavacka.google_search_objects.Spelling;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

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
    private ArrayList<String> reversedUserScientificClassification;

    private OnFragmentInteractionListener mListener;

    private boolean loadingRepresentative;
    private boolean listCreated;

    private Button btnSAVE;
    private ProgressBar progressBar;

    //RecyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
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
            userScientificClassification = getArguments().getStringArrayList(ARG_USERSCIENTIFICCLASSIFICATION);
            reversedUserScientificClassification = getArguments().getStringArrayList(ARG_REVERSEDUSERSCIENTIFICCLASSIFICATION);
            Timber.d(TAG + "userScientificClassification = " + userScientificClassification.toString());
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
        btnSAVE.setVisibility(View.INVISIBLE);
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

        /* generation */
        final GeneratedListFragment.WikiSearchRepresentatives[] WikiSearchRepresentatives = {new WikiSearchRepresentatives(GeneratedListFragment.this)};

        //new generation?..
        listCreated = false;
        WikiSearchRepresentatives[0].cancel(true);

        Toast.makeText(getActivity(), "Creating, please wait..", Toast.LENGTH_SHORT).show();

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

        WikiSearchRepresentatives[0] = new WikiSearchRepresentatives(GeneratedListFragment.this);
        WikiSearchRepresentatives[0].execute();
        /*try {
            WikiSearchRepresentatives[0].execute().wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

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
                //HERE LEFT OFF classification not loading
            }

            /*fragment.mZastupceArr.add(new Zastupce(userParametersCount, reversedUserScientificClassification));
            Timber.d("Classification 2 added");*/

            allRepresentatives:
            for (String representative :
                    representatives) {
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
                        fragment.mZastupceArr.add(new Zastupce(userParametersCount, fragment.capitalize(representative, languageURL)));
                        publishProgress(representative);
                        continue;
                    }
                    HttpURLConnection conn = null;
                    try {
                        conn = (HttpURLConnection) url.openConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Timber.d("no wiki");
                        fragment.mZastupceArr.add(new Zastupce(userParametersCount, fragment.capitalize(representative, languageURL)));
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
                        fragment.mZastupceArr.add(new Zastupce(userParametersCount, fragment.capitalize(representative, languageURL)));
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
                            Timber.d("result=" + result);
                        } else {
                            //response problem

                            String errorMsg = "Http ERROR response " + responseMessage + "\n" + "Are you online ? " + "\n" + "Make sure to replace in code your own Google API key and Search Engine ID";
                            Log.e(TAG, errorMsg);
                            Timber.d("no wiki");
                            fragment.mZastupceArr.add(new Zastupce(userParametersCount, fragment.capitalize(representative, languageURL)));
                            publishProgress(representative);
                            continue;
                            //result = errorMsg;
                        }

                    } catch (IOException e) {
                        Log.e(TAG, "Http Response ERROR " + e.toString());
                        Timber.d("no wiki");
                        fragment.mZastupceArr.add(new Zastupce(userParametersCount, fragment.capitalize(representative, languageURL)));
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
                                Timber.d("result=" + imgResult);
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
                    searchText = wikipeidaURL.substring(wikipeidaURL.indexOf("/wiki/") + 6);
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
                ArrayList<String> newData = new ArrayList<>();
                int classificationPointer = 0;

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
                                    Timber.d("does contain id info = " + table.id());
                                    infoBox = table.selectFirst("tbody");
                                    redirects = false;
                                    break;
                                } else if (table.attr("class").contains("info")) {
                                    Timber.d("does contain class info = " + table.attr("class"));
                                    infoBox = table.selectFirst("tbody");
                                    redirects = false;
                                    break;
                                }

                            } else if (languageURL.equals("de")) {

                                if (table.id().contains("taxo") || table.id().contains("Taxo")) {
                                    Timber.d("does contain id info = " + table.id());
                                    infoBox = table.selectFirst("tbody");
                                    redirects = false;
                                    break;
                                } else if (table.attr("class").contains("taxo")) {
                                    Timber.d("does contain class info = " + table.attr("class"));
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
                            /*img = (Drawable) harvestedInfoBox.get(2);
                            imageURL = (String) harvestedInfoBox.get(3);*/
                    }

                    String displayNameOfRepresentative = doc.title();
                    if (doc.title().contains("(")) {
                        displayNameOfRepresentative = doc.title().substring(0, doc.title().indexOf("(")).trim();
                    }
                    newData.add(displayNameOfRepresentative);
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
                    Timber.d("newData size for representative " + representative + "= " + newData.size() + "\n\n");

                } else {
                    //add an empty only with representative
                    fragment.mZastupceArr.add(new Zastupce(userParametersCount, fragment.capitalize(representative, languageURL)));
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
                        doc = Jsoup.connect("https://" + languageURL + ".wikipedia.org/api/rest_v1/page/html/" + URLEncoder.encode(newSearchText, "UTF-8") + "?redirect=true").userAgent("Mozilla").get();

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

        private ArrayList<String> harvestInfo(Element infoBox) {

            ArrayList returnList = new ArrayList();

            ArrayList<String> newData = new ArrayList<>();
           /* Drawable img = null;
            String imageURL = "";*/
            int classificationPointer = 0;

            String[] dataPair = new String[2];
            boolean imageDetected = false;
            int trCounter = 0;
            GeneratedListFragment fragment = fragmentWeakReference.get();


            //Element infoBox = doc.getElementsByTag("table").first().selectFirst("tbody");
            Elements trs = infoBox.select("tr");
            int trsWithoutColspan = 0;
            int currentTrsWihtoutColspan = 0;

            //to check if all the info has been scanned through
            for (Element tr :
                    trs) {
                if (!tr.getAllElements().hasAttr("colspan")) trsWithoutColspan++;
            }

            //actual scanning
            for (Element tr :
                    trs) {

                trCounter++;
                Timber.d("current tr = %s", trCounter);
                if (!tr.getAllElements().hasAttr("colspan") && fragment.autoImportIsChecked && !(userScientificClassification.size() <= classificationPointer)) {
                    currentTrsWihtoutColspan++;
                    String[] rawData = tr.wholeText().split("\n");

                    if (rawData.length == 1) { //detected wrong table
                        Timber.d("different table");
                        for (int i = 0; i < userParametersCount - 1; i++) {
                            newData.add("");
                        }
                        break;
                    }

                    //cleaning rawData to data
                    ArrayList<String> data = new ArrayList<>();
                    for (String piece :
                            rawData) {
                        if (!piece.trim().isEmpty()) data.add(piece);
                    }
                    Timber.d("data = %s", data.toString());

                    //assigning the dataPair
                    dataPair[0] = data.get(0);
                    StringBuilder values = new StringBuilder();
                    for (int i = 1; i < data.size(); i++) {
                        if (data.get(i).contains("[")) {
                            String editedString = data.get(i).substring(0, data.get(i).indexOf("[")).trim();
                            data.set(i, editedString);
                        }
                        if (i == 1) {
                            values.append(data.get(i));
                            Timber.d("adding value = %s", data.get(i));
                        } else {
                            values.append(", ").append(data.get(i));
                            Timber.d("adding value = " + ", " + data.get(i));
                        }

                    }
                    dataPair[1] = values.toString();

/*                    if (dataPair[0].trim().isEmpty()) { //if first line is empty, idk why
                        Timber.d("first line is empty");
                        dataPair = dataPair.clone()[1].split("\n");
                }*/
                    for (int i = 0; i < 2; i++) {
                        dataPair[i] = dataPair[i].trim();
                        Timber.d("found " + dataPair[i]);
                    }

                    Timber.d("classPointer = " + classificationPointer);
                    Timber.d(userScientificClassification.get(classificationPointer) + " ?equals = " + dataPair[0]);
                    Timber.d("userSciClass[0] = " + userScientificClassification.get(0));

                    if (userScientificClassification.get(classificationPointer).equals(dataPair[0])) { //detected searched classification
                        //trim latin etc.
                        if (dataPair[1].contains("(")) {
                            dataPair[1] = dataPair[1].substring(0, dataPair[1].indexOf(")") + 1).trim(); //trims after latin
                        }
                        newData.add(dataPair[1]); //adding the specific classification
                        Timber.d("adding new data to newData = " + dataPair[1]);
                        classificationPointer++;

                    } else if (userScientificClassification.contains(dataPair[0])) { //detected needed classification but some were empty (not there)
                        Timber.d("userScientificClassification contains " + dataPair[0]);
                        int tempPointer = classificationPointer;
                        classificationPointer = userScientificClassification.indexOf(dataPair[0]);
                        for (; tempPointer < classificationPointer; tempPointer++) {
                            newData.add("");
                            Timber.d("adding new data to newData = empty (not detected)");
                        }


                        classificationPointer++;
                        if (dataPair[1].contains("(")) {
                            dataPair[1] = dataPair[1].substring(0, dataPair[1].indexOf("(")).trim();
                        }
                        newData.add(dataPair[1]);
                        Timber.d("adding new data to newData = " + dataPair[1]);
                    }
                    Timber.d("currentTrsWithoutColspan = " + currentTrsWihtoutColspan + ", trsWithoutColspan = " + trsWithoutColspan);

                } else if (currentTrsWihtoutColspan == trsWithoutColspan) { //current tr is the last one with information
                    Timber.d("Current tr is the last one with information.");
                    //if (newData.size() <= (userParametersCount - 2)) {
                    for (int i = newData.size(); i < userParametersCount - 1; i++) { //one of the last parameters was not detected
                        Timber.d("one of last parameters not detected");
                        newData.add("");
                        //}
                        /*Timber.d("last parameter not detected");
                        newData.add("");*/
                        break;
                    }
                }


                /*//get the image
                else if (img == null) {
                    ArrayList imgAndUrl = getImageFromTr(tr);
                    img = (Drawable) imgAndUrl.get(0);
                    imageURL = (String) imgAndUrl.get(1);
                }*/
            }
            returnList.add(newData);
            returnList.add(classificationPointer);
            /*returnList.add(img);
            returnList.add(imageURL);*/
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
            //TODO adapter cannot set for some reason on item click listener

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            GeneratedListFragment fragment = fragmentWeakReference.get();
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
}
