package com.example.timad.poznavacka.activities.lists.createList;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.timad.poznavacka.R;
import com.example.timad.poznavacka.activities.lists.MyListsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import androidx.fragment.app.Fragment;


public class SetRepresentativesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    private EditText representativesInput;
    public static FloatingActionButton btnNext;
    private Button btnCancel;

    private boolean enteredTextIsValid;

    public SetRepresentativesFragment() {
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
    public static SetRepresentativesFragment newInstance(String param1, String param2) {
        SetRepresentativesFragment fragment = new SetRepresentativesFragment();
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
    }

    //the actual fragment operations
    @Override
    public void onStart() {
        super.onStart();
        representativesInput = getView().findViewById(R.id.representatives_input);
        btnNext = getView().findViewById(R.id.button_next_representatives);
        representativesInput.requestFocus();
        final InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.showSoftInput(representativesInput, InputMethodManager.SHOW_FORCED);
        //imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

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
                    btnNext.show();
                } else {
                    enteredTextIsValid = false;
                    btnNext.hide();
                }

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCancel.setVisibility(View.GONE);
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    representativesInput.clearFocus();
                    imgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                btnNext.setVisibility(View.GONE);

                ArrayList<String> representatives = new ArrayList<>(Arrays.asList(representativesInput.getText().toString().split("\\s*" + "," + "\\s*")));

                onButtonPressed(representatives);
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_representatives, container, false);
    }

    public void onButtonPressed(ArrayList<String> representatives) {
        if (mListener != null) {
            mListener.updateRepresentatives(representatives);
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
        void updateRepresentatives(ArrayList<String> representatives);
    }


}
