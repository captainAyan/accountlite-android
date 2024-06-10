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

import com.github.captainayan.accountlite.adapter.TrialBalanceAdapter;
import com.github.captainayan.accountlite.database.AppDatabase;
import com.github.captainayan.accountlite.database.LedgerDao;
import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.utility.StringUtility;
import com.github.captainayan.accountlite.utility.statement.JournalEntriesCSVStatement;
import com.github.captainayan.accountlite.utility.statement.JournalEntriesHTMLStatement;
import com.github.captainayan.accountlite.utility.statement.TrialBalanceCSVStatement;
import com.github.captainayan.accountlite.utility.statement.TrialBalanceHTMLStatement;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class TrialBalanceActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;

    private AppDatabase db;
    private LedgerDao ledgerDao;

    private TextView emptyView;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private TrialBalanceAdapter adapter;
    private ArrayList<Ledger.LedgerWithBalance> ledgerWithBalanceList;

    private String dateFormat, dateSeparator;
    private long asOnDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial_balance);

        asOnDate = Calendar.getInstance().getTimeInMillis();

        db = AppDatabase.getAppDatabase(this);
        ledgerDao = db.ledgerDao();

        ledgerWithBalanceList = (ArrayList<Ledger.LedgerWithBalance>) ledgerDao
                .getLedgersWithBalance(asOnDate);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new TrialBalanceAdapter(this, ledgerWithBalanceList);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        emptyView = (TextView) findViewById(R.id.emptyView);
        if (!ledgerWithBalanceList.isEmpty()) emptyView.setVisibility(View.INVISIBLE);

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
                TrialBalanceActivity.this.finish();
            }
        });
        toolbar.setSubtitle(new StringBuilder()
                .append(getString(R.string.as_on_date))
                .append(" ")
                .append(StringUtility.dateFormat(asOnDate, dateFormat, dateSeparator))
                .toString());
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
        String html = new TrialBalanceHTMLStatement()
                .setAsOnDate(asOnDate)
                .setLedgerWithBalanceList(ledgerWithBalanceList)
                .build(this);

        String csv = new TrialBalanceCSVStatement()
                .setLedgerWithBalanceList(ledgerWithBalanceList)
                .build();

        Intent i = new Intent(this, StatementDownloadActivity.class);
        i.putExtra("html", html);
        i.putExtra("csv", csv);
        i.putExtra("filename", "trial_balance_" + asOnDate);
        startActivity(i);
    }
}