package com.example.timad.poznavacka;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.timad.poznavacka.PoznavackaInfo;
import com.example.timad.poznavacka.activities.lists.MyListsFragment;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class StorageManagerClass {
    private final String ENV_PATH;
    private final Gson GSON;

    public StorageManagerClass(String envPath){
        ENV_PATH = envPath + "/";
        GSON = new Gson();
    }

    public String readFile(String path, boolean create){
        ArrayList<PoznavackaInfo> arr = new ArrayList<>();
        File txtFile = new File(ENV_PATH + path);
        String s = "";
        String line;
        FileReader fr;
        BufferedReader br;

        try {
            fr = new FileReader(txtFile);
            br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                s += line;
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            if(create) {
                createFile(txtFile);
            }
        }

        return s.trim();
    }

    private void createFile(File file){
        FileWriter fw = null;

        try {
            fw = new FileWriter(file);
            fw.write("");
            fw.flush();
            fw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void updatePoznavackaFile(String path, ArrayList<PoznavackaInfo> arr) {
        File file = new File(ENV_PATH + path);
        FileWriter fw;
        String s;

        if (arr.size() <= 0) {
            s = "";
        } else {
            s = GSON.toJson(arr);
        }

        try {
            fw = new FileWriter(file);
            fw.write(s);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(MyListsFragment.mAdapter != null){
            synchronized (MyListsFragment.mAdapter) {
                MyListsFragment.mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void deletePoznavacka(String path) {
        File directory = new File(ENV_PATH + path);

        try {
            File[] files = directory.listFiles();
            for (int x = 0; x < files.length; x++) {
                files[x].delete();
            }
            directory.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean saveDrawable(Drawable drawable, String path, String name){
        BitmapDrawable bitmapDraw = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDraw.getBitmap();
        FileOutputStream fos;

        try {
            fos = new FileOutputStream(ENV_PATH + path + name + ".png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            deletePoznavacka(path);
            return false;
        }
        return true;
    }

    // https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
    public Drawable readDrawable(String path, String name, Context context) {
        Bitmap bitmap = null;

        try {
            File file =new File(ENV_PATH + path + name + ".png");
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public boolean createAndWriteToFile(String path, String name ,String json){
        File txtFile = new File(ENV_PATH + path + name + ".txt");
        FileWriter fw;

        try {
            fw = new FileWriter(txtFile);
            fw.write(json);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            txtFile.delete();
            e.printStackTrace();
            deletePoznavacka(path);
            return false;
        }
        return true;
    }
}
