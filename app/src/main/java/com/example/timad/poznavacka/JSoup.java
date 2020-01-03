package com.example.timad.poznavacka;

import android.os.AsyncTask;
import android.util.Log;

import com.example.timad.poznavacka.activities.lists.CreateListFragment;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class JSoup extends AsyncTask<String, Void, String> {

    private static final String TAG = "JSoup";

    private String url = "https://cs.wikipedia.org/wiki/";


    @Override
    protected String doInBackground(String... representative) {
/*
        //vytvareni url
        StringBuilder builder = new StringBuilder();
        for (String value : representative) {
            builder.append(value);
        }
        String text = builder.toString();

        url = url + text;
        Log.d(TAG, url);

        //spojovani
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


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
            return doc.outerHtml();
        }


        return "done";
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        CreateListFragment.testString = s;
    }
}
