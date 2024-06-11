package com.github.captainayan.accountlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.captainayan.accountlite.adapter.JournalAdapter;
import com.github.captainayan.accountlite.database.AppDatabase;
import com.github.captainayan.accountlite.database.EntryDao;
import com.github.captainayan.accountlite.model.Journal;
import com.github.captainayan.accountlite.utility.StringUtility;
import com.github.captainayan.accountlite.utility.TimeUtility;
import com.github.captainayan.accountlite.utility.statement.JournalEntriesCSVStatement;
import com.github.captainayan.accountlite.utility.statement.JournalEntriesHTMLStatement;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JournalEntriesActivity extends AppCompatActivity {

    private static final String TAG = "JOURNAL_ENTRIES_ACT";
    private MaterialToolbar toolbar;

    private AppDatabase db;
    private EntryDao entryDao;

    private TextView emptyView;

    private ChipGroup chipGroup;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private JournalAdapter adapter;
    private ArrayList<Journal> journalList;

    private String dateFormat, dateSeparator;

    private TimeUtility.ToAndFromDate toAndFromDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_entries);

        Intent i = getIntent();
        toAndFromDate = TimeUtility.getToAndFromDateTimestampFromIntent(i);

        db = AppDatabase.getAppDatabase(this);
        entryDao = db.entryDao();

        journalList = (ArrayList<Journal>) entryDao.getJournals(toAndFromDate.fromDateTimestamp, toAndFromDate.toDateTimestamp);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new JournalAdapter(this, journalList);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        chipGroup = (ChipGroup) findViewById(R.id.chipGroup);
        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                if ((journalList.size() > 2) &&
                        ((journalList.get(0).getTimestamp() > journalList.get(1).getTimestamp() // newest first
                        && checkedIds.get(0) == R.id.oldestFirstChip)
                        || (journalList.get(0).getTimestamp() < journalList.get(1).getTimestamp() // oldest first
                        && checkedIds.get(0) == R.id.newestFirstChip))
                ) {
                    // newest first
                    Collections.reverse(journalList);
                    adapter.notifyDataSetChanged();
                }
            }
        });

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.statement_download_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.statement_download_menu_save) {
            downloadFile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void downloadFile() {
        String html = new JournalEntriesHTMLStatement()
                .setJournalList(journalList)
                .setDateRange(toAndFromDate.toDateTimestamp, toAndFromDate.fromDateTimestamp)
                .build(this);

        String csv = new JournalEntriesCSVStatement()
                .setJournalList(journalList)
                .build();

        Intent i = new Intent(this, StatementDownloadActivity.class);
        i.putExtra("html", html);
        i.putExtra("csv", csv);
        i.putExtra("filename", "journal_entries_" + toAndFromDate.fromDateTimestamp + "_" + toAndFromDate.toDateTimestamp);
        startActivity(i);
    }
}