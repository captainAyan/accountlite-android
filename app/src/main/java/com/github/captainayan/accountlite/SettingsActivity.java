package com.github.captainayan.accountlite;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.captainayan.accountlite.database.AppDatabase;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SETTINGS_ACT";
    private MaterialToolbar toolbar;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();
            }
        });

        if (findViewById(R.id.settingsFragmentContainer)!=null) {
            if (savedInstanceState!=null) {
                return;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.settingsFragmentContainer, new SettingsFragment()).commit();
        }

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                String darkModeString = getString(R.string.dark_mode_pref_key);

                final String[] darkModeValues = getResources().getStringArray(R.array.dark_mode_pref_values);

                if(s != null && s.equals(darkModeString)) {
                    String a = sharedPreferences.getString(darkModeString, darkModeValues[0]);
                    if (darkModeValues[0].equals(a)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    } else if (darkModeValues[1].equals(a)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else if (darkModeValues[2].equals(a)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else if (darkModeValues[3].equals(a)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                    }
                }
            }
        };

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(listener);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            if (preference.getTitle() == getResources().getString(R.string.reset_books)) {
                AppDatabase db = AppDatabase.getAppDatabase(this.getContext());
                db.delete(getContext());
                Toast.makeText(this.getContext(), "Restart the app to view effect", Toast.LENGTH_LONG).show();
            }
            return super.onPreferenceTreeClick(preference);
        }
    }
}