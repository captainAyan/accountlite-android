package com.github.captainayan.accountlite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.captainayan.accountlite.adapter.BalanceAdapter;
import com.github.captainayan.accountlite.database.AppDatabase;
import com.github.captainayan.accountlite.database.LedgerDao;
import com.github.captainayan.accountlite.fragment.DateRangeSelectionBottomSheetFragment;
import com.github.captainayan.accountlite.fragment.LedgerSelectionBottomSheetFragment;
import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.model.TestBalance;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    public static final String TAG = "MAIN_ACT";

    private MaterialToolbar toolbar;
    private FloatingActionButton fab;

    private CardView trialBalanceButton, ledgerAccountButton, journalEntriesButton, finalStatementButton;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private BalanceAdapter adapter;
    private ArrayList<TestBalance> balances;

    private LedgerDao ledgerDao;

    private DateRangeSelectionBottomSheetFragment dateRangeSelectionBottomSheetFragment;
    private LedgerSelectionBottomSheetFragment ledgerSelectionBottomSheetFragment;

    // preference related
    private String defaultDateAsTodayKey ;
    private String defaultRangeKey;
    private String defaultDateRangeValues;

    private Calendar calendar = Calendar.getInstance();

    ArrayList<Ledger> ledgerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ledgerDao = AppDatabase.getAppDatabase(this).ledgerDao();

        defaultDateAsTodayKey = getResources().getString(R.string.default_date_as_today_pref_key);
        defaultRangeKey = getResources().getString(R.string.default_date_range_pref_key);
        defaultDateRangeValues = getResources().getString(R.string.default_date_range_default_values);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        balances = new ArrayList<TestBalance>();
        balances.add(new TestBalance("Rs.10,000", "Assets"));
        balances.add(new TestBalance("Rs.400", "Liabilities"));
        balances.add(new TestBalance("Rs.900", "Equity"));
        balances.add(new TestBalance("Rs.14,500", "Revenue"));
        balances.add(new TestBalance("Rs.2,000", "Expenditure"));

        adapter = new BalanceAdapter(this, balances);
        manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        fab.setOnClickListener(this);

        finalStatementButton = (CardView) findViewById(R.id.final_statement_button);
        finalStatementButton.setOnClickListener(this);
        finalStatementButton.setOnLongClickListener(this);

        trialBalanceButton = (CardView) findViewById(R.id.trial_balance_button);
        trialBalanceButton.setOnClickListener(this);
        trialBalanceButton.setOnLongClickListener(this);

        ledgerAccountButton = (CardView) findViewById(R.id.ledger_account_button);
        ledgerAccountButton.setOnClickListener(this);
        ledgerAccountButton.setOnLongClickListener(this);

        journalEntriesButton = (CardView) findViewById(R.id.journal_entries_button);
        journalEntriesButton.setOnClickListener(this);
        journalEntriesButton.setOnLongClickListener(this);

        toolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        dateRangeSelectionBottomSheetFragment = new DateRangeSelectionBottomSheetFragment();
        ledgerSelectionBottomSheetFragment = new LedgerSelectionBottomSheetFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ledgerList = (ArrayList<Ledger>) ledgerDao.getAll();
    }

    // adding menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.main_menu_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.floating_action_button) {
            startActivity(new Intent(MainActivity.this, CreateJournalEntryActivity.class));
        } else if(id == R.id.final_statement_button) {
            startActivity(new Intent(MainActivity.this, FinalStatementActivity.class));
        } else if (id == R.id.trial_balance_button) {
            startActivity(new Intent(MainActivity.this, TrialBalanceActivity.class));
        } else if (id == R.id.ledger_account_button) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(defaultDateAsTodayKey, false)) {
                Intent i = new Intent(this, LedgerAccountActivity.class);
                addDefaultExtraToIntent(i);
                ledgerSelection(i);
            } else onClickLedgerAccountButton();
        } else if (id == R.id.journal_entries_button) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(defaultDateAsTodayKey, false)) {
                Intent i = new Intent(this, JournalEntriesActivity.class);
                addDefaultExtraToIntent(i);
                startActivity(i);
            } else onClickJournalEntriesButton();
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.ledger_account_button) {
            onClickLedgerAccountButton();
            return true;
        } else if (view.getId() == R.id.journal_entries_button) {
            onClickJournalEntriesButton();
            return true;
        }
        return false;
    }

    /**
     * show date selector THEN start journal entries activity
     */
    private void onClickJournalEntriesButton() {
        if (!dateRangeSelectionBottomSheetFragment.isAdded()) {
            dateRangeSelectionBottomSheetFragment.show(getSupportFragmentManager(), dateRangeSelectionBottomSheetFragment.getTag());
            dateRangeSelectionBottomSheetFragment.setOnTimeSelectListener(intent -> {
                intent.setClass(MainActivity.this, JournalEntriesActivity.class);
                dateRangeSelectionBottomSheetFragment.dismiss();
                startActivity(intent);
            });
        }
    }

    /**
     * show date selector THEN show ledger selector
     */
    private void onClickLedgerAccountButton() {
        if (!dateRangeSelectionBottomSheetFragment.isAdded()) {
            dateRangeSelectionBottomSheetFragment.show(getSupportFragmentManager(), dateRangeSelectionBottomSheetFragment.getTag());
            dateRangeSelectionBottomSheetFragment.setOnTimeSelectListener(intent -> {
                intent.setClass(MainActivity.this, LedgerAccountActivity.class);
                dateRangeSelectionBottomSheetFragment.dismiss();
                ledgerSelection(intent);
            });
        }
    }

    /**
     * Adds default (preference values) period and date to the intent.
     * @param i
     */
    private void addDefaultExtraToIntent(Intent i) {
        i.putExtra("day", calendar.get(Calendar.DAY_OF_MONTH));
        i.putExtra("month", calendar.get(Calendar.MONTH));
        i.putExtra("year", calendar.get(Calendar.YEAR));
        i.putExtra("duration", PreferenceManager.getDefaultSharedPreferences(this)
                .getString(defaultRangeKey, defaultDateRangeValues));
    }

    /**
     * Shows a bottom sheet with ledgers, and then starts LedgerAccountActivity with the selected
     * ledger's id.
     * @param i
     */
    private void ledgerSelection(Intent i) {
        if (!ledgerSelectionBottomSheetFragment.isAdded()) {
            ledgerSelectionBottomSheetFragment.setLedgerNameList(ledgerList);
            ledgerSelectionBottomSheetFragment.show(getSupportFragmentManager(), ledgerSelectionBottomSheetFragment.getTag());
            ledgerSelectionBottomSheetFragment.setOnLedgerSelectListener(new LedgerSelectionBottomSheetFragment.OnLedgerSelectListener() {
                @Override
                public void onSelect(int selectedLedgerId) {
                    //Toast.makeText(MainActivity.this, ""+checkedChipId, Toast.LENGTH_SHORT).show();
                    i.putExtra("ledger_id", selectedLedgerId);
                    ledgerSelectionBottomSheetFragment.dismiss();
                    startActivity(i);
                }
            });
        }
    }


}