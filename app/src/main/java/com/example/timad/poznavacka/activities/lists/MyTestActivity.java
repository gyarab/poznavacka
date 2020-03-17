package com.example.timad.poznavacka.activities.lists;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.timad.poznavacka.R;

public class MyTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_test);



    }
    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(getApplication(), MyListsActivity.class);
        startActivity(intent1);
        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
        finish();
    }
}
