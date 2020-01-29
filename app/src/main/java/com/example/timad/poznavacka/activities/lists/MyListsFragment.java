package com.example.timad.poznavacka.activities.lists;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.timad.poznavacka.PoznavackaInfo;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.RWAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MyListsFragment extends Fragment {
    private static final String TAG = "ListsFragment";

    public static PoznavackaInfo sActivePoznavacka = null;

    private RecyclerView mRecyclerView;
    private RWAdapter mAdapter;
    private RecyclerView.LayoutManager mLManager;
    private ArrayList<PoznavackaInfo> mPoznavackaInfoArr;
    public static int mPositionOfActivePoznavackaInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        View view = inflater.inflate(R.layout.fragment_mylists, container, false);

        createArr();

        /* RecyclerView */
        mRecyclerView = view.findViewById(R.id.recyclerViewL);
        mRecyclerView.setHasFixedSize(true);
        mLManager = new LinearLayoutManager(getContext());
        mAdapter = new RWAdapter(mPoznavackaInfoArr);

        mRecyclerView.setLayoutManager(mLManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RWAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                /* udělej něco s tim
                mAdaper.notify... */

                mPositionOfActivePoznavackaInfo = position;
                sActivePoznavacka = mPoznavackaInfoArr.get(mPositionOfActivePoznavackaInfo);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onShareClick(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.app_name);
                builder.setIcon(R.drawable.ic_share_black_24dp);
                builder.setMessage("Do you really want to share " + mPoznavackaInfoArr.get(position).getName() + "?");
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
                builder.setMessage("Do you really want to delete " + mPoznavackaInfoArr.get(position).getName() + "?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Vymazani ze zarizeni TODO
                        removeItem(position);
                        if(position <= mPositionOfActivePoznavackaInfo){
                            mPositionOfActivePoznavackaInfo -= 1;
                            sActivePoznavacka = mPoznavackaInfoArr.get(mPositionOfActivePoznavackaInfo);
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
        mPoznavackaInfoArr = new ArrayList<>();
        mPoznavackaInfoArr.add(new PoznavackaInfo("Line 1", "Line 2"));
        mPoznavackaInfoArr.add(new PoznavackaInfo("Line 3", "Line 4"));

        if(mPoznavackaInfoArr.size() > 0){

        }
    }

    /** Nvm */
    public void buildRW(){

    }

    /* Asi nebude potřeba */
    public void addItem(){
        mPoznavackaInfoArr.add(new PoznavackaInfo("novy poznavackaInfo", "..."));
        mAdapter.notifyDataSetChanged();
    }

    /* Ještě je potřeba implementovat smazání souboru */
    public void removeItem(int pos){
        mPoznavackaInfoArr.remove(pos);
        mAdapter.notifyDataSetChanged();
    }

}