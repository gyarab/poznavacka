package com.example.timad.poznavacka;

import android.os.AsyncTask;
import android.util.Log;

import com.example.timad.poznavacka.activities.lists.CreateListFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JSoup extends AsyncTask<String, Void, String> {

    private static final String TAG = "JSoup";

    private String url = "https://cs.wikipedia.org/wiki/";


    @Override
    protected String doInBackground(String... representative) {

        StringBuilder builder = new StringBuilder();
        for (String value : representative) {
            builder.append(value);
        }
        String text = builder.toString();

        url = url + text;
        Log.d(TAG, url);

        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
        for (Element a :
                document.select()) {

        }*/
        return document.outerHtml();
    }

    @Override
    protected void onPostExecute(String s) {
        CreateListFragment.testString = s;
    }
}
