package com.example.timad.poznavacka.activities.lists.createList;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.timad.poznavacka.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import androidx.fragment.app.Fragment;


public class SetLanguageRepresentativesFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    private Spinner languageSpinner;
    private EditText representativesInput;
    private FloatingActionButton btnNext;

    private String languageURL;

    private boolean languageSelected;
    private boolean enteredTextIsValid;

    public SetLanguageRepresentativesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetTitleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetLanguageRepresentativesFragment newInstance(String param1, String param2) {
        SetLanguageRepresentativesFragment fragment = new SetLanguageRepresentativesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

/*        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getFragmentManager().popBackStackImmediate();
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);*/
    }

    //the actual fragment operations
    @Override
    public void onStart() {
        super.onStart();
        representativesInput = getView().findViewById(R.id.representatives_input);
        btnNext = getView().findViewById(R.id.button_next_representatives);
        representativesInput.requestFocus();
        final InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        //btnNext.setVisibility(View.INVISIBLE);
        btnNext.hide();

        representativesInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().trim().isEmpty() && s.toString().contains(",")) {
                    enteredTextIsValid = true;
                    if (languageSelected) {
                        btnNext.show();
                    }
                } else {
                    enteredTextIsValid = false;
                    btnNext.hide();
                }

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgr.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                btnNext.setVisibility(View.GONE);

                ArrayList<String> representatives = new ArrayList<>(Arrays.asList(representativesInput.getText().toString().split("\\s*" + "," + "\\s*")));

                onButtonPressed(languageURL, representatives);
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

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //getting the language of wiki
        Object selectedLanguageSpinnerItem = parent.getItemAtPosition(position);
        String languageString = selectedLanguageSpinnerItem.toString();
        languageSelected = true;

        switch (languageString) {
            case "Select Language":
                languageURL = "Select Language";
                languageSelected = false;
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
                break;
        }

        if (languageSelected && enteredTextIsValid) {
            btnNext.show();
        } else {
            btnNext.hide();
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
        return inflater.inflate(R.layout.fragment_set_language_representatives, container, false);
    }

    public void onButtonPressed(String languageURL, ArrayList<String> representatives) {
        if (mListener != null) {
            mListener.updateLanguageAndRepresentatives(languageURL, representatives);
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
        void updateLanguageAndRepresentatives(String languageURL, ArrayList<String> representatives);
    }


}
