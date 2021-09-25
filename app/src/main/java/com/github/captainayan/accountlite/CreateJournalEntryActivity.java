package com.github.captainayan.accountlite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.captainayan.accountlite.database.AppDatabase;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CreateJournalEntryActivity extends AppCompatActivity {

    Button submitBtn;
    MaterialToolbar toolbar;
    AutoCompleteTextView debitAccount, creditAccount;
    EditText amount, narration;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journal_entry);

        db = AppDatabase.getAppDatabase(this);


        // String[] ledgerList = {"Sales", "Accounts receivable", "Cash", "Purchases", "Accounts payable"};
        ArrayList<String> ledgerList = new ArrayList<String>(Arrays.asList("Sales", "Accounts receivable", "Cash", "Purchases", "Accounts payable"));
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ledgerList);

        debitAccount = (AutoCompleteTextView) findViewById(R.id.debitEditText);
        creditAccount = (AutoCompleteTextView) findViewById(R.id.creditEditText);
        debitAccount.setAdapter(arrayAdapter);
        creditAccount.setAdapter(arrayAdapter);

        submitBtn = (Button) findViewById(R.id.submit);

        toolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateJournalEntryActivity.this.finish();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CreateJournalEntryActivity.this, "Entry Created", Toast.LENGTH_SHORT).show();
            }
        });


    }
}