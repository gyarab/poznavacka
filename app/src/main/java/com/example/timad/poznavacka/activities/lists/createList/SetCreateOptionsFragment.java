package com.example.timad.poznavacka.activities.lists.createList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.timad.poznavacka.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;


public class SetCreateOptionsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters


    private OnFragmentInteractionListener mListener;

    private Switch autoImportSwitch;
    private ExtendedFloatingActionButton btnNext;

    private boolean autoImportIsChecked;

    private ArrayList<String> representatives;
    private String languageURL;

    public SetCreateOptionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SetTitleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetCreateOptionsFragment newInstance(String param1) {
        SetCreateOptionsFragment fragment = new SetCreateOptionsFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_REPRESENTATIVES, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            representatives = getArguments().getStringArrayList("ARG_REPRESENTATIVES");
            languageURL = getArguments().getString("ARG_LANGUAGEURL");
        }
    }

    //the actual fragment operations
    @Override
    public void onStart() {
        super.onStart();

        //button
        btnNext = getView().findViewById(R.id.button_create_new);
        btnNext.hide();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //switch
        autoImportSwitch = getView().findViewById(R.id.generateSwitch);
        autoImportSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                        autoImportIsChecked = true;
                        Intent intent = new Intent(getContext(), PopActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getContext(), "Select language first", Toast.LENGTH_SHORT).show();
                        autoImportSwitch.setChecked(false);
                        autoImportIsChecked = false;
                    }
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("SCOF", "inflating setCreateOptionsFragment");
        return inflater.inflate(R.layout.fragment_set_create_options, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int userParametersCount, ArrayList<String> userScientificClassification) {
        if (mListener != null) {
            //mListener.updateCreateOptions(userParametersCount, userScientificClassification);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void updateCreateOptions(String languageURL, ArrayList<String> representatives);
    }


}
