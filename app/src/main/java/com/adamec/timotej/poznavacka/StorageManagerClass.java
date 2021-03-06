package com.adamec.timotej.poznavacka;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.adamec.timotej.poznavacka.activities.lists.MyListsActivity;
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

/**
 * Tříd starající se o soubory v zařízení.
 */
public class StorageManagerClass {
    private final String ENV_PATH;
    private final Gson GSON;

    /**
     * Konstruktor
     * @param envPath - cesta, kde jsou uloženy soubory
     */
    public StorageManagerClass(String envPath) {
        ENV_PATH = envPath + "/";
        GSON = new Gson();
    }

    /**
     * Přečte soubor
     * @param path cesta k souboru
     * @param create zda má vytvořit soubor, když neexistuje
     * @return vrací obsah souboru v podobě Stringu
     */
    public String readFile(String path, boolean create) {
        File txtFile = new File(ENV_PATH + path);
        String s = "";
        String line;
        FileReader fr = null;
        BufferedReader br = null;

        try {
            try {
                fr = new FileReader(txtFile);
            } catch (FileNotFoundException e) {
                return null;
            }
            br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                s += line;
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (create) {
                createFile(txtFile);
            }
        }

        return s.trim();
    }

    /**
     * Vytvoří soubor
     * @param file soubor, který je potřeba vytvořit
     */
    private void createFile(File file) {
        FileWriter fw = null;

        try {
            fw = new FileWriter(file);
            fw.write("");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Aktualizuje informace o poznávačce
     * @param path cesta k poznávačce
     * @param arrRaw
     */
    public void updatePoznavackaFile(String path, ArrayList<Object> arrRaw) {
        ArrayList arr = new ArrayList();
        for (Object object :
                arrRaw) {
            if (object instanceof PoznavackaInfo) {
                arr.add(object);
            }
        }

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

        if (MyListsActivity.mAdapter != null) {
            synchronized (MyListsActivity.mAdapter) {
                MyListsActivity.mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Upraví obsah souboru.
     * @param path cesta k souboru
     * @param name název souboru
     * @param arr pole, které je potřeba uložit
     */
    public void updateFile(String path, String name, ArrayList<Integer> arr) {
        File file = new File(ENV_PATH + path + name);
        FileWriter fw;
        String s;

        s = GSON.toJson(arr);

        try {
            fw = new FileWriter(file);
            fw.write(s);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Smaže poznávačku ze zařízení.
     * @param path cesta
     */
    public void deletePoznavacka(String path) {
        File directory = new File(ENV_PATH + path);

        try {
            File[] files = directory.listFiles();
            for (File file : files) {
                file.delete();
            }
            directory.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Uloží obrázek. Zdroj: https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
     * @param drawable obrázek, který má být uložen
     * @param path cesta
     * @param name název
     * @return vrací true, když obrázek byl uložen, jinak false
     */
    public boolean saveDrawable(Drawable drawable, String path, String name) {
        BitmapDrawable bitmapDraw = (BitmapDrawable) drawable;
        if (bitmapDraw == null) {
            NullPointerException nullPointerException = new NullPointerException();
            nullPointerException.printStackTrace();
            deletePoznavacka(path);
            return false;
        }
        Bitmap bitmap = bitmapDraw.getBitmap();
        FileOutputStream fos;

        try {
            fos = new FileOutputStream(ENV_PATH + path + name + ".png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            deletePoznavacka(path);
            return false;
        }
        return true;
    }

    /**
     * Přečte obrázek z úložiště. Zdroj: https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
     * @param path cesta
     * @param name název obrázku
     * @param context
     * @return vrací obrázek načtený z úložiště
     */
    public Drawable readDrawable(String path, String name, Context context) {
        Bitmap bitmap = null;

        try {
            File file = new File(ENV_PATH + path + name + ".png");
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    /**
     * Vytvoří soubor a zapíše do něj data.
     * @param path cesta k souboru
     * @param name název souboru
     * @param json String, která chceme uložit v souboru
     * @return
     */
    public boolean createAndWriteToFile(String path, String name, String json) {
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
