package com.github.captainayan.accountlite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
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
    public long toDateTimestamp, fromDateTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger_account);

        Intent i = getIntent();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, i.getIntExtra("day", 0));
        c.set(Calendar.MONTH, i.getIntExtra("month", 0));
        c.set(Calendar.YEAR, i.getIntExtra("year", 0));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        toDateTimestamp = c.getTimeInMillis();

        c.set(Calendar.DAY_OF_MONTH, i.getIntExtra("day", 0));
        c.set(Calendar.MONTH, i.getIntExtra("month", 0));
        c.set(Calendar.YEAR, i.getIntExtra("year", 0));
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        switch (i.getStringExtra("duration")) {
            case "week":
                c.add(Calendar.WEEK_OF_MONTH, -1);
                break;
            case "fortnite":
                c.add(Calendar.WEEK_OF_MONTH, -2);
                break;
            case "month":
                c.add(Calendar.MONTH, -1);
                break;
        }
        fromDateTimestamp = c.getTimeInMillis();

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
                .append(StringUtility.dateFormat(fromDateTimestamp, dateFormat, dateSeparator))
                .append(" - ")
                .append(StringUtility.dateFormat(toDateTimestamp, dateFormat, dateSeparator)).toString());

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
    public void onBackPressed() {
        if (viewPager.getCurrentItem() != 0) viewPager.setCurrentItem(0, true);
        else finish();
    }
}