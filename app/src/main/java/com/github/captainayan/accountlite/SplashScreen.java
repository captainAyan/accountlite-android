package com.github.captainayan.accountlite;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;
        if(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getResources().getString(R.string.first_time_launch_pref_key),true))
            intent = new Intent(this, SetupActivity.class);
        else intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
