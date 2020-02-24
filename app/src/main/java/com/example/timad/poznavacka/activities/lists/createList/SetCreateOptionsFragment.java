package com.example.timad.poznavacka.activities.lists.createList;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.timad.poznavacka.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import androidx.fragment.app.Fragment;


public class SetCreateOptionsFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RAWREPRESENTATIVES = "param1";

    // TODO: Rename and change types of parameters
    private String rawRepresentatives;

    private OnFragmentInteractionListener mListener;

    private Spinner languageSpinner;
    private Switch autoImportSwitch;
    private ExtendedFloatingActionButton btnNext;

    private String languageURL;
    private boolean switchPressedOnce;
    private boolean autoImportIsChecked;

    private ArrayList<String> representatives;

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
        args.putString(ARG_RAWREPRESENTATIVES, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rawRepresentatives = getArguments().getString(ARG_RAWREPRESENTATIVES);
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
                if (!languageURL.equals("Select Language")) {
                    btnNext.setVisibility(View.GONE);
                    onButtonPressed(languageURL, representatives);
                } else {
                    Toast.makeText(getContext(), "Select Language", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //spinner
        languageSpinner = getView().findViewById(R.id.languageSpinnerNew);
        languageURL = "Select Language";
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.language_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
        languageSpinner.getBackground().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        languageSpinner.setOnItemSelectedListener(this);

        //switch
        switchPressedOnce = false;
        autoImportSwitch = getView().findViewById(R.id.generateSwitch);
        autoImportSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!languageURL.equals("Select Language")) {
                        if (rawRepresentatives.contains(",")) {
                            representatives = new ArrayList<>(Arrays.asList(rawRepresentatives.split("\\s*" + "," + "\\s*")));
                        } else {
                            representatives = new ArrayList<>(Collections.singletonList(rawRepresentatives.trim()));
                        }


                        switchPressedOnce = true;
                        autoImportIsChecked = true;
                        Intent intent = new Intent(getContext(), PopActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getContext(), "Select language first", Toast.LENGTH_SHORT).show();
                        autoImportSwitch.setChecked(false);
                        autoImportIsChecked = false;
                    }
                } else {
                    autoImportIsChecked = false;
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //getting the language of wiki
        Object selectedLanguageSpinnerItem = parent.getItemAtPosition(position);
        String languageString = selectedLanguageSpinnerItem.toString();
        boolean showButton = true;

        switch (languageString) {
            case "Select Language":
                languageURL = "Select Language";
                showButton = false;
                break;
            case "English (Latin)":
                languageURL = "en";
                break;
            case "Czech":
                languageURL = "cs";
                break;
            case "French":
                languageURL = "fr";
                break;
            case "German":
                languageURL = "de";
                break;
            case "Spanish":
                languageURL = "es";
                break;
            case "Japanese":
                languageURL = "ja";
                break;
            case "Russian":
                languageURL = "ru";
                break;
            case "Italian":
                languageURL = "it";
                break;
            case "Portuguese":
                languageURL = "pt";
                break;
            case "Arabic":
                languageURL = "ar";
                break;
            case "Persian":
                languageURL = "fa";
                break;
            case "Polish":
                languageURL = "pl";
                break;
            case "Dutch":
                languageURL = "nl";
                break;
            case "Indonesian":
                languageURL = "id";
                break;
            case "Ukrainian":
                languageURL = "uk";
                break;
            case "Hebrew":
                languageURL = "he";
                break;
            case "Swedish":
                languageURL = "sv";
                break;
            case "Korean":
                languageURL = "ko";
                break;
            case "Vietnamese":
                languageURL = "vi";
                break;
            case "Finnish":
                languageURL = "fi";
                btnNext.show();
                break;
        }

        if (showButton) {
            btnNext.show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Toast.makeText(getContext(), "Select language", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("SCOF", "inflating setCreateOptionsFragment");
        return inflater.inflate(R.layout.fragment_set_create_options, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String languageURL, ArrayList<String> representatives) {
        if (mListener != null) {
            mListener.updateCreateOptions(languageURL, representatives);
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
