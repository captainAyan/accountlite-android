package com.github.captainayan.accountlite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.captainayan.accountlite.database.AppDatabase;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
            Context c = getContext();
            if (preference.getTitle() == getResources().getString(R.string.reset_books)) {
                new MaterialAlertDialogBuilder(c)
                        .setTitle(R.string.reset_books_dialog_title)
                        .setMessage(R.string.reset_books_dialog_message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AppDatabase db = AppDatabase.getAppDatabase(c);
                                db.delete(c);
                                Toast.makeText(c, getResources().getString(R.string.reset_books_dialog_toast), Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create().show();
            }
            else if (preference.getTitle() == getResources().getString(R.string.developer_label)) {
                String url = c.getResources().getString(R.string.developer_url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
            else if (preference.getTitle() == getResources().getString(R.string.send_feedback_label)) {
                String url = c.getResources().getString(R.string.send_feedback_url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
            return super.onPreferenceTreeClick(preference);
        }
    }
}