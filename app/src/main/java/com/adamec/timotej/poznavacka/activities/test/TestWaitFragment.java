package com.adamec.timotej.poznavacka.activities.test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adamec.timotej.poznavacka.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class TestWaitFragment extends Fragment {
    private static final String TAG = "TestWaitFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_wait, container, false);


        return view;
    }
}