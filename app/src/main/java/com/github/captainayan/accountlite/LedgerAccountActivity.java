package com.github.captainayan.accountlite;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.captainayan.accountlite.adapter.LedgerAccountViewPagerAdapter;
import com.github.captainayan.accountlite.database.AppDatabase;
import com.github.captainayan.accountlite.database.EntryDao;
import com.github.captainayan.accountlite.database.LedgerDao;
import com.github.captainayan.accountlite.fragment.LedgerDetailsFragment;
import com.github.captainayan.accountlite.fragment.LedgerEntriesFragment;
import com.github.captainayan.accountlite.model.Journal;
import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.utility.StringUtility;
import com.github.captainayan.accountlite.utility.TimeUtility;
import com.github.captainayan.accountlite.utility.statement.LedgerAccountHTMLStatement;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LedgerAccountActivity extends AppCompatActivity {

    private static final String TAG = "LEDGER_ACCOUNT_ACT";
    private MaterialToolbar toolbar;

    // tablayout and viewpager
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private LedgerAccountViewPagerAdapter fragmentAdapter;

    private LedgerEntriesFragment ledgerEntriesFragment;
    private LedgerDetailsFragment ledgerDetailsFragment;

    private String dateFormat, dateSeparator;

    public int ledgerId;
    public TimeUtility.ToAndFromDate toAndFromDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger_account);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (viewPager.getCurrentItem() != 0) viewPager.setCurrentItem(0, true);
                else finish();
            }
        });

        Intent i = getIntent();
        toAndFromDate = TimeUtility.getToAndFromDateTimestampFromIntent(i);

        ledgerId = i.getIntExtra("ledger_id", 1);

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
                LedgerAccountActivity.this.onBackPressed();
            }
        });
        toolbar.setSubtitle(new StringBuilder()
                .append(StringUtility.dateFormat(toAndFromDate.fromDateTimestamp, dateFormat, dateSeparator))
                .append(" - ")
                .append(StringUtility.dateFormat(toAndFromDate.toDateTimestamp, dateFormat, dateSeparator)).toString());

        // tablayout and viewpager
        ledgerEntriesFragment = new LedgerEntriesFragment();
        ledgerDetailsFragment = new LedgerDetailsFragment();

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager2) findViewById(R.id.viewPager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setOffscreenPageLimit(2);
        fragmentAdapter = new LedgerAccountViewPagerAdapter(fragmentManager, getLifecycle(),
                ledgerEntriesFragment, ledgerDetailsFragment);
        viewPager.setAdapter(fragmentAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                tabLayout.setScrollPosition(position, positionOffset, false);
            }
        });
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
        EntryDao entryDao = AppDatabase.getAppDatabase(this).entryDao();;
        LedgerDao ledgerDao = AppDatabase.getAppDatabase(this).ledgerDao();

        Ledger ledger = ledgerDao.getLedgerById(ledgerId);
        ArrayList<Journal> journalList = (ArrayList<Journal>) entryDao.getJournalsByLedger(
                ledgerId, toAndFromDate.fromDateTimestamp, toAndFromDate.toDateTimestamp);

        int openingBalance = ledgerDao.getLedgerBalance(ledger.getId(), toAndFromDate.fromDateTimestamp);
        int closingBalance = ledgerDao.getLedgerBalance(ledger.getId(), toAndFromDate.toDateTimestamp);

        String html = new LedgerAccountHTMLStatement()
                .setLedger(ledger)
                .setJournalList(journalList)
                .setDateRange(toAndFromDate.toDateTimestamp, toAndFromDate.fromDateTimestamp)
                .setOpeningBalance(openingBalance)
                .setClosingBalance(closingBalance)
                .build(this);

        Intent i = new Intent(this, StatementDownloadActivity.class);
        i.putExtra("html", html);
        i.putExtra("filename", ledger.getName() + "_ledger_" + toAndFromDate.fromDateTimestamp
                + "_" + toAndFromDate.toDateTimestamp);
        startActivity(i);
    }
}