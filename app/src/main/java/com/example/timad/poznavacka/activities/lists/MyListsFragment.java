package com.example.timad.poznavacka.activities.lists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.timad.poznavacka.PoznavackaInfo;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.RWAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MyListsFragment extends Fragment {
    private static final String TAG = "ListsFragment";

    private RecyclerView mRecyclerView;
    private RWAdapter mAdapter;
    private RecyclerView.LayoutManager mLManager;
    private ArrayList<PoznavackaInfo> mPoznavackaInfoArr;
    public static int mActivePoznavackaInfo;

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

                mActivePoznavackaInfo = position;
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
                if(position <= mActivePoznavackaInfo){
                    mActivePoznavackaInfo -= 1;
                } else if(mActivePoznavackaInfo > mAdapter.getItemCount()){
                    mActivePoznavackaInfo = 0;
                }
                mAdapter.notifyDataSetChanged();
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