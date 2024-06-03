package com.github.captainayan.accountlite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.captainayan.accountlite.adapter.JournalAdapter;
import com.github.captainayan.accountlite.database.AppDatabase;
import com.github.captainayan.accountlite.database.EntryDao;
import com.github.captainayan.accountlite.model.Journal;
import com.github.captainayan.accountlite.utility.StringUtility;
import com.github.captainayan.accountlite.utility.TimeUtility;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Objects;

public class JournalEntriesActivity extends AppCompatActivity {

    private static final String TAG = "JOURNAL_ENTRIES_ACT";
    private MaterialToolbar toolbar;

    private AppDatabase db;
    private EntryDao entryDao;

    private TextView emptyView;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private JournalAdapter adapter;
    private ArrayList<Journal> journalList;

    private String dateFormat, dateSeparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_entries);

        Intent i = getIntent();
        TimeUtility.ToAndFromDate toAndFromDate = TimeUtility.getToAndFromDateTimestampFromIntent(i);

        db = AppDatabase.getAppDatabase(this);
        entryDao = db.entryDao();

        journalList = (ArrayList<Journal>) entryDao.getJournals(toAndFromDate.fromDateTimestamp, toAndFromDate.toDateTimestamp);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new JournalAdapter(this, journalList);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        emptyView = (TextView) findViewById(R.id.emptyView);
        if (!journalList.isEmpty()) emptyView.setVisibility(View.INVISIBLE);

        dateFormat = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getResources().getString(R.string.date_format_pref_key),
                getResources().getString(R.string.date_format_default_value));

        dateSeparator = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getResources().getString(R.string.date_separator_pref_key),
                getResources().getString(R.string.date_separator_default_value));

        toolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JournalEntriesActivity.this.finish();
            }
        });
        toolbar.setSubtitle(new StringBuilder()
                .append(StringUtility.dateFormat(toAndFromDate.fromDateTimestamp, dateFormat, dateSeparator))
                .append(" - ")
                .append(StringUtility.dateFormat(toAndFromDate.toDateTimestamp, dateFormat, dateSeparator)).toString());
    }
}