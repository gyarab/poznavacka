package com.example.timad.poznavacka.activities.lists;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.timad.poznavacka.PoznavackaInfo;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.RWAdapter;
import com.example.timad.poznavacka.Zastupce;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MyListsFragment extends Fragment {
    private static final String TAG = "ListsFragment";

    public static PoznavackaInfo sActivePoznavacka = null;
    public static ArrayList<PoznavackaInfo> sPoznavackaInfoArr;

    private RecyclerView mRecyclerView;
    private RWAdapter mAdapter;
    private RecyclerView.LayoutManager mLManager;
    public static int mPositionOfActivePoznavackaInfo;
    private String mPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        View view = inflater.inflate(R.layout.fragment_mylists, container, false);

        mPath = getContext().getFilesDir().getPath();

        if(sPoznavackaInfoArr == null){
            Gson gson = new Gson();
            String s = readFile(mPath + "/poznavacka.txt", true);
            /*File file2 = new File(mPath + "/poznavacka.txt");
            file2.delete();*/

            if (s != null) {
                if(!s.isEmpty()) {
                    Type cType = new TypeToken<ArrayList<PoznavackaInfo>>(){}.getType();
                    sPoznavackaInfoArr = gson.fromJson(s, cType);
                } else {
                    sPoznavackaInfoArr = new ArrayList<>();
                }
            } else {
                sPoznavackaInfoArr = new ArrayList<>();
            }
        }
        //createArr();

        /* RecyclerView */
        mRecyclerView = view.findViewById(R.id.recyclerViewL);
        mRecyclerView.setHasFixedSize(true);
        mLManager = new LinearLayoutManager(getContext());
        mAdapter = new RWAdapter(sPoznavackaInfoArr);

        mRecyclerView.setLayoutManager(mLManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RWAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                /* udělej něco s tim
                mAdaper.notify... */

                mPositionOfActivePoznavackaInfo = position;
                sActivePoznavacka = sPoznavackaInfoArr.get(mPositionOfActivePoznavackaInfo);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onShareClick(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.app_name);
                builder.setIcon(R.drawable.ic_share_black_24dp);
                builder.setMessage("Do you want to share " + sPoznavackaInfoArr.get(position).getName() + "?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Sdilet poznavacku TODO

                        Toast toast = Toast.makeText(getContext(), "Shared", Toast.LENGTH_SHORT);
                        toast.show();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

                Button btnPositive = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                Button btnNegative = alert.getButton(AlertDialog.BUTTON_NEGATIVE);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
                layoutParams.weight = 20;
                btnPositive.setLayoutParams(layoutParams);
                btnNegative.setLayoutParams(layoutParams);
            }

            @Override
            public void onDeleteClick(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.app_name);
                builder.setIcon(R.drawable.ic_delete);
                builder.setMessage("Do you really want to delete " + sPoznavackaInfoArr.get(position).getName() + "?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String path = mPath + "/" + sPoznavackaInfoArr.get(position).getId() + "/";
                        File dir = new File(path);
                        CreateListFragment.deletePoznavacka(dir);

                        sPoznavackaInfoArr.remove(position);
                        CreateListFragment.updatePoznavackaFile(mPath + "/poznavacka.txt", sPoznavackaInfoArr);

                        if(position <= mPositionOfActivePoznavackaInfo){
                            mPositionOfActivePoznavackaInfo -= 1;
                            if(sPoznavackaInfoArr.size() > 0) {
                                if(mPositionOfActivePoznavackaInfo < 0) {
                                    mPositionOfActivePoznavackaInfo = 0;
                                }
                                sActivePoznavacka = sPoznavackaInfoArr.get(mPositionOfActivePoznavackaInfo);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

                Button btnPositive = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                Button btnNegative = alert.getButton(AlertDialog.BUTTON_NEGATIVE);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
                layoutParams.weight = 20;
                btnPositive.setLayoutParams(layoutParams);
                btnNegative.setLayoutParams(layoutParams);
            }
        });

        return view;
    }

    /** Načte názvy poznávaček
     * Potřeba předělat na načítání názvů poznávaček ze souborů */
    public void createArr(){
        sPoznavackaInfoArr = new ArrayList<>();
        sPoznavackaInfoArr.add(new PoznavackaInfo("Line 1", "Line 2"));
        sPoznavackaInfoArr.add(new PoznavackaInfo("Line 3", "Line 4"));
    }

    /** Nvm */
    public void buildRW(){

    }

    /* Asi nebude potřeba */
    public void addItem(){
        sPoznavackaInfoArr.add(new PoznavackaInfo("novy poznavackaInfo", "..."));
        mAdapter.notifyDataSetChanged();
    }

    /* Ještě je potřeba implementovat smazání souboru */
    public void removeItem(int pos){
        sPoznavackaInfoArr.remove(pos);
        mAdapter.notifyDataSetChanged();
    }

    public static String readFile(String path, boolean create){
        ArrayList<PoznavackaInfo> arr = new ArrayList<>();
        File txtFile = new File(path);
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

    private static void createFile(File file){
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

}