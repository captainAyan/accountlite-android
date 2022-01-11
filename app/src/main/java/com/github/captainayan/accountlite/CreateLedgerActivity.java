package com.github.captainayan.accountlite;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.captainayan.accountlite.database.AppDatabase;
import com.github.captainayan.accountlite.database.LedgerDao;
import com.github.captainayan.accountlite.model.Ledger;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Objects;

public class CreateLedgerActivity extends AppCompatActivity {

    private Button submitBtn;
    private MaterialToolbar toolbar;
    private ArrayAdapter<String> ledgerTypesArrayAdapter;
    private AutoCompleteTextView ledgerTypeEditText;
    private EditText ledgerNameEditText;

    private AppDatabase db;
    private LedgerDao ledgerDao;

    private ArrayList<String> ledgerNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ledger);

        db = AppDatabase.getAppDatabase(this);
        ledgerDao = db.ledgerDao();

        ledgerNameList = (ArrayList<String>) ledgerDao.getAllNames();

        toolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateLedgerActivity.this.finish();
            }
        });

        ledgerNameEditText = (EditText) findViewById(R.id.nameEditText);
        ledgerTypeEditText = (AutoCompleteTextView) findViewById(R.id.typeEditText);

        ledgerTypesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.ledger_types));
        ledgerTypeEditText.setAdapter(ledgerTypesArrayAdapter);
        // ledgerTypeEditText.setText(ledgerTypesArrayAdapter.getItem(0));

        submitBtn = (Button) findViewById(R.id.submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ledgerNameEditText.getText().toString().trim();
                int type = ledgerTypesArrayAdapter.getPosition(String.valueOf(ledgerTypeEditText.getText()));
                if(!name.isEmpty() && !ledgerNameList.contains(name)) {
                    if (type != -1) {
                        ledgerDao.insert(new Ledger(name, type));
                        Toast.makeText(CreateLedgerActivity.this, R.string.ledger_create_success, Toast.LENGTH_SHORT).show();
                        ledgerNameList.add(name);
                        resetForm();
                    }
                    else Toast.makeText(CreateLedgerActivity.this, R.string.error_message_invalid_ledger_type, Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(CreateLedgerActivity.this, R.string.error_message_invalid_ledger_name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetForm() {
        ledgerNameEditText.setText("");
        ledgerTypeEditText.setText("");
    }

}