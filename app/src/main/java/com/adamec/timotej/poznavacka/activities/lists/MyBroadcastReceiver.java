package com.adamec.timotej.poznavacka.activities.lists;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import timber.log.Timber;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName clickedComponent = intent.getParcelableExtra(Intent.EXTRA_CHOSEN_COMPONENT);

        if (clickedComponent != null) {
            Timber.d("Clicked component: %s", clickedComponent.getPackageName());

            //MyListsActivity myListsActivity = new MyListsActivity();
            //myListsActivity.upload();

        } else {
            Timber.d("Clicked component is null");
        }
    }
}
