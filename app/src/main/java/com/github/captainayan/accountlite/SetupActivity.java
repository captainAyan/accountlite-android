package com.github.captainayan.accountlite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetupActivity extends AppCompatActivity {

    private EditText userName, businessName;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        userName = (EditText) findViewById(R.id.userNameEditText);
        businessName = (EditText) findViewById(R.id.businessNameEditText);
        submitBtn = (Button) findViewById(R.id.submit);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SetupActivity.this);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString(getResources().getString(R.string.user_name_pref_key), userName.getText().toString());
                editor.putString(getResources().getString(R.string.business_name_pref_key), businessName.getText().toString());
                editor.putBoolean(getResources().getString(R.string.first_time_launch_pref_key), false);

                editor.apply();

                Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}