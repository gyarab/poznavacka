package com.example.timad.poznavacka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MyListsFragment extends Fragment {
    private static final String TAG = "ListsFragment";

    private RecyclerView mRecyclerView;
    private RWAdapter mAdapter;
    private RecyclerView.LayoutManager mLManager;
    private ArrayList<Zastupce> mZastupceArr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mylists, container, false);

        createArr();

        /* RecyclerView */
        mRecyclerView = view.findViewById(R.id.recyclerViewL);
        mRecyclerView.setHasFixedSize(true);
        mLManager = new LinearLayoutManager(getContext());
        mAdapter = new RWAdapter(mZastupceArr);

        mRecyclerView.setLayoutManager(mLManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RWAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                /* udělej něco s tim
                mZastupceArr.get(position);
                mAdaper.notify */
            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });

        return view;
    }

    /** Načte názvy poznávaček
     * Potřeba předělat na načítání názvů poznávaček ze souborů */
    public void createArr(){
        mZastupceArr = new ArrayList<>();
        mZastupceArr.add(new Zastupce("Line 1", "Line 2"));
        mZastupceArr.add(new Zastupce("Line 3", "Line 4"));
    }

    /** Nvm */
    public void buildRW(){

    }

    /* Asi nebude potřeba */
    public void addItem(){
        mZastupceArr.add(new Zastupce("novy zastupce", "..."));
        mAdapter.notifyDataSetChanged();
    }

    /* Ještě je potřeba implementovat smazání souboru */
    public void removeItem(int pos){
        mZastupceArr.remove(pos);
        mAdapter.notifyDataSetChanged();
    }
}