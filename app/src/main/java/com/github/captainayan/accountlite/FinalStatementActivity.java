package com.github.captainayan.accountlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.captainayan.accountlite.adapter.FinalStatementViewPagerAdapter;
import com.github.captainayan.accountlite.database.AppDatabase;
import com.github.captainayan.accountlite.database.LedgerDao;
import com.github.captainayan.accountlite.fragment.BalanceSheetFragment;
import com.github.captainayan.accountlite.fragment.IncomeStatementFragment;
import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.utility.StringUtility;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class FinalStatementActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;

    // tablayout and viewpager
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FinalStatementViewPagerAdapter fragmentAdapter;

    private IncomeStatementFragment incomeStatementFragment;
    private BalanceSheetFragment balanceSheetFragment;

    // data
    private double asOnDate;
    public ArrayList<Ledger.LedgerWithBalance> ledgerWithBalanceList; // accessed by fragments

    // database
    LedgerDao ledgerDao;

    private String dateFormat, dateSeparator;

    private static final int CREATE_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_statement);

        ledgerDao = AppDatabase.getAppDatabase(this).ledgerDao();

        asOnDate = Calendar.getInstance().getTimeInMillis();

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
                FinalStatementActivity.this.onBackPressed();
            }
        });
        toolbar.setSubtitle(new StringBuilder()
                .append(getString(R.string.as_on_date))
                .append(" ")
                .append(StringUtility.dateFormat(asOnDate, dateFormat, dateSeparator))
                .toString());

        ledgerWithBalanceList = (ArrayList<Ledger.LedgerWithBalance>) ledgerDao.getLedgersWithBalance(asOnDate);

        // tablayout and viewpager
        incomeStatementFragment = new IncomeStatementFragment();
        balanceSheetFragment = new BalanceSheetFragment();

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager2) findViewById(R.id.viewPager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setOffscreenPageLimit(2);
        fragmentAdapter = new FinalStatementViewPagerAdapter(fragmentManager, getLifecycle(),
                incomeStatementFragment, balanceSheetFragment);
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

    // adding menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.final_statement_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.final_statement_menu_save) {
            // createFile();
            return true;
        }
        else return false;
    }

    /*private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_TITLE, "Final Statement.html");
        intent.setType("text/html");
        startActivityForResult(intent, CREATE_FILE);
    }*/

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            BufferedOutputStream bos = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    bos = new BufferedOutputStream(getContentResolver().openOutputStream(uri));
                    bos.write( null
                            // new FinalStatementToHTML(this).print().getBytes(StandardCharsets.UTF_8)
                            );
                    bos.close();
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() != 0) viewPager.setCurrentItem(0, true);
        else finish();
    }

}