package com.github.captainayan.accountlite;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.github.captainayan.accountlite.utility.Parser;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class JournalEntriesActivity extends AppCompatActivity {

    private static final String TAG = "JOURNAL_ENTRIES_ACT";
    private MaterialToolbar toolbar;
    String input = "#BUSINESS=lol co.\n#CURRENCY=Rs.\n#CURRENCY_FORMAT=ind\n#NAME=lol cat\n1,500,1421113156,cash,bank,being cash withdrawn from bank\n2,300,1621193156,bank,cash,being cash deposited into bank";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_entries);

        toolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JournalEntriesActivity.this.finish();
            }
        });

        ArrayList<Parser.Journal> journalList = new ArrayList<>();
        HashMap<String, String> metaDataMap = new HashMap<>();
        Parser.parse(input, journalList, metaDataMap);

    }
}