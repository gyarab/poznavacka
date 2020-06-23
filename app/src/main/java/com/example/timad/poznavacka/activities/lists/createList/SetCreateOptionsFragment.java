package com.example.timad.poznavacka.activities.lists.createList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.timad.poznavacka.BuildConfig;
import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.activities.lists.MyListsActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import timber.log.Timber;


public class SetCreateOptionsFragment extends Fragment {
    private String TAG = "SetCreateOptionsFragment";

    private OnFragmentInteractionListener mListener;

    private Switch autoImportSwitch;
    public static ExtendedFloatingActionButton btnNext;
    public static Button btnCancel;

    private boolean autoImportIsChecked;

    private ArrayList<String> representatives;
    private String languageURL;

    private ArrayList<String> userScientificClassification = new ArrayList<>();
    private ArrayList<String> reversedUserScientificClassification = new ArrayList<>();
    private int userParametersCount = 1;

    public SetCreateOptionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param languageURL     Parameter 1.
     * @param representatives Parameter 2.
     * @return A new instance of fragment SetTitleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetCreateOptionsFragment newInstance(String languageURL, ArrayList<String> representatives) {
        SetCreateOptionsFragment fragment = new SetCreateOptionsFragment();
        Bundle args = new Bundle();
        args.putString("ARG_LANGUAGEURL", languageURL);
        args.putStringArrayList("ARG_REPRESENTATIVES", representatives);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        if (getArguments() != null) {
            languageURL = getArguments().getString("ARG_LANGUAGEURL");
            representatives = getArguments().getStringArrayList("ARG_REPRESENTATIVES");
        }

    }


    //the actual in-fragment operations
    @Override
    public void onStart() {
        super.onStart();

        Timber.d(TAG + " userParametersCount = " + userParametersCount);
        /*userScientificClassification = new ArrayList<>();
        reversedUserScientificClassification = new ArrayList<>();*/

        //button
        btnNext = requireView().findViewById(R.id.button_create_new);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCancel.setVisibility(View.GONE);
                Timber.d(TAG + " btnNext userParametersCount = " + userParametersCount);
                Timber.d(TAG + " btnNext userScientificClassification = " + userScientificClassification.toString());
                btnNext.setVisibility(View.GONE);
                onButtonPressed(autoImportIsChecked, userParametersCount, userScientificClassification, reversedUserScientificClassification);
            }
        });

        btnCancel = getView().findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyListsActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
                getActivity().finish();
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
                    startActivityForResult(intent, 1);
                } else {
                    autoImportSwitch.setChecked(false);
                    autoImportIsChecked = false;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Timber.d("onActivityResult");
        userParametersCount = data.getIntExtra("userParametersCount", 1);
        userScientificClassification = data.getStringArrayListExtra("userScientificClassification");
        reversedUserScientificClassification = data.getStringArrayListExtra("reversedUserScientificClassification");

        if (userScientificClassification == null || userScientificClassification.size() == 0) {
            autoImportSwitch.setChecked(false);
            autoImportIsChecked = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("SCOF", "inflating setCreateOptionsFragment");
        return inflater.inflate(R.layout.fragment_set_create_options, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(boolean autoImportIsChecked, int userParametersCount, ArrayList<String> userScientificClassification, ArrayList<String> reversedUserScientificClassification) {
        if (mListener != null) {
            mListener.updateCreateOptions(autoImportIsChecked, userParametersCount, userScientificClassification, reversedUserScientificClassification);
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
        void updateCreateOptions(boolean autoImportIsChecked, int userParametersCount, ArrayList<String> userScientificClassification, ArrayList<String> reversedUserScientificClassification);
    }


}
