package com.example.timad.poznavacka.activities.test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.timad.poznavacka.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class TestPINFragment extends Fragment {
    private static final String TAG = "TestWaitFragment";

    Button enterPinButton;
    EditText pinInput;

    String PIN;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_pin, container, false);
        enterPinButton = view.findViewById(R.id.enter_pin_button);
        pinInput = view.findViewById(R.id.pin_input);

        enterPinButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                PIN = String.valueOf(pinInput.getText());
                /*

                verify the PIN

                 */
                TestActivity.mViewPager.setCurrentItem(1); //if verified go wait

            }
        });


        return view;
    }
}