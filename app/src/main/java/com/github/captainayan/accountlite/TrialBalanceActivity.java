package com.github.captainayan.accountlite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.captainayan.accountlite.adapter.TrialBalanceAdapter;
import com.github.captainayan.accountlite.database.AppDatabase;
import com.github.captainayan.accountlite.database.LedgerDao;
import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.utility.StringUtility;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial_balance);

        double asOnDate = Calendar.getInstance().getTimeInMillis();

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
                .append("As On Date ")
                .append(StringUtility.dateFormat(asOnDate))
                .toString());
    }
}