package com.example.timad.poznavacka.activities.lists;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.timad.poznavacka.StorageManagerClass;
import com.example.timad.poznavacka.activities.PracticeActivity;
import com.example.timad.poznavacka.activities.test.PoznavackaDbObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
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
    public static StorageManagerClass sSMC;

    public static PoznavackaInfo sActivePoznavacka = null;
    public static ArrayList<PoznavackaInfo> sPoznavackaInfoArr;

    private RecyclerView mRecyclerView;
    public static RWAdapter mAdapter;
    private RecyclerView.LayoutManager mLManager;
    public static int mPositionOfActivePoznavackaInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        View view = inflater.inflate(R.layout.fragment_mylists, container, false);

        if(sPoznavackaInfoArr == null){
            Gson gson = new Gson();
            String s = getSMC(getContext()).readFile("poznavacka.txt", true);
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
            public void onPracticeClick(final int position) {
                mPositionOfActivePoznavackaInfo = position;
                sActivePoznavacka = sPoznavackaInfoArr.get(mPositionOfActivePoznavackaInfo);
                mAdapter.notifyDataSetChanged();

                // TODO switch to PracticeActivity
                Context context = getContext();
                Intent myIntent = new Intent(context, PracticeActivity.class);
                context.startActivity(myIntent);
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
                        //

                        String s = MyListsFragment.getSMC(getContext()).readFile(sPoznavackaInfoArr.get(position).getId() + "/" + sPoznavackaInfoArr.get(position).getId() + ".txt", false);
                        SharedListsFragment.addToFireStore("Poznavacka",new PoznavackaDbObject(sPoznavackaInfoArr.get(position).getName(),sPoznavackaInfoArr.get(position).getId(),s,sPoznavackaInfoArr.get(position).getAuthor()));

                        //
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
                        Context context = getContext();

                        getSMC(context).deletePoznavacka(sPoznavackaInfoArr.get(position).getId() + "/");

                        sPoznavackaInfoArr.remove(position);
                        getSMC(context).updatePoznavackaFile("poznavacka.txt", sPoznavackaInfoArr);

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

    public static StorageManagerClass getSMC(Context context){
        if(sSMC == null){
            sSMC = new StorageManagerClass(context.getFilesDir().getPath());
        }

        return sSMC;
    }

    /* Ještě je potřeba implementovat smazání souboru */
    public void removeItem(int pos){
        sPoznavackaInfoArr.remove(pos);
        mAdapter.notifyDataSetChanged();
    }
}