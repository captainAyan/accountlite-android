package com.github.captainayan.accountlite;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class Accountlite extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final String darkModeString = getString(R.string.dark_mode_pref_key);
        final String[] darkModeValues = getResources().getStringArray(R.array.dark_mode_pref_values);

        String a = PreferenceManager.getDefaultSharedPreferences(this).getString(darkModeString, darkModeValues[0]);

        if (darkModeValues[0].equals(a)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (darkModeValues[1].equals(a)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (darkModeValues[2].equals(a)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

}