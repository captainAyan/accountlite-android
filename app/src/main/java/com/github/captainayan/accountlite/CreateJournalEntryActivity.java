package com.github.captainayan.accountlite;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.captainayan.accountlite.database.AppDatabase;
import com.github.captainayan.accountlite.model.Entry;
import com.github.captainayan.accountlite.database.EntryDao;
import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.database.LedgerDao;
import com.github.captainayan.accountlite.utility.StringUtility;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class CreateJournalEntryActivity extends AppCompatActivity {

    public static final String TAG = "CREATE_ENTRY_ACT";

    private Button submitBtn;
    private MaterialToolbar toolbar;
    private AutoCompleteTextView debitAccountEditText, creditAccountEditText;
    private EditText amountEditText, narrationEditText;

    private AppDatabase db;
    private LedgerDao ledgerDao;
    private EntryDao entryDao;

    // for new ledgers
    private String[] ledgerTypes;
    private ArrayList<Ledger> ledgerList;
    private ArrayList<String> ledgerNameList;
    private ArrayAdapter<String> ledgerListArrayAdapter;
    private int debitLedgerType = 0;
    private int creditLedgerType = 0;
    private MaterialAlertDialogBuilder debitLedgerTypeDialog;
    private MaterialAlertDialogBuilder creditLedgerTypeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journal_entry);

        db = AppDatabase.getAppDatabase(this);
        ledgerDao = db.ledgerDao();
        entryDao = db.entryDao();

        ledgerTypes = getResources().getStringArray(R.array.ledger_types);

        ledgerList = (ArrayList<Ledger>) ledgerDao.getAll();
        ledgerNameList = (ArrayList<String>) ledgerDao.getAllNames();
        ledgerListArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ledgerNameList);

        debitAccountEditText = (AutoCompleteTextView) findViewById(R.id.debitEditText);
        creditAccountEditText = (AutoCompleteTextView) findViewById(R.id.creditEditText);
        debitAccountEditText.setAdapter(ledgerListArrayAdapter);
        creditAccountEditText.setAdapter(ledgerListArrayAdapter);

        amountEditText = (EditText) findViewById(R.id.amountEditText);
        narrationEditText = (EditText) findViewById(R.id.narrationEditText);
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

        debitLedgerTypeDialog = new MaterialAlertDialogBuilder(this,
                R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {;
                        createEntry(debitAccountEditText.getText().toString().trim(), creditAccountEditText.getText().toString().trim());
                })
                .setSingleChoiceItems(ledgerTypes, 0, (dialogInterface, i) -> debitLedgerType = i);

        creditLedgerTypeDialog = new MaterialAlertDialogBuilder(this,
                R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        createEntry(debitAccountEditText.getText().toString().trim(), creditAccountEditText.getText().toString().trim());
                })
                .setSingleChoiceItems(ledgerTypes, 0, (dialogInterface, i) -> creditLedgerType = i);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String debitAccountName = debitAccountEditText.getText().toString().trim();
                String creditAccountName = creditAccountEditText.getText().toString().trim();

                if(debitAccountName.isEmpty() || creditAccountName.isEmpty()) {
                    Toast.makeText(CreateJournalEntryActivity.this, R.string.error_message_empty_accounts, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(debitAccountName.equals(creditAccountName)) {
                    Toast.makeText(CreateJournalEntryActivity.this, R.string.error_message_same_account, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(amountEditText.getText().toString().isEmpty()) {
                    Toast.makeText(CreateJournalEntryActivity.this, R.string.error_message_invalid_amount, Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (Integer.parseInt(amountEditText.getText().toString()) == 0) {
                        Toast.makeText(CreateJournalEntryActivity.this, R.string.error_message_invalid_amount, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                catch(NumberFormatException e) {
                    Toast.makeText(CreateJournalEntryActivity.this, R.string.error_message_invalid_amount, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(narrationEditText.getText().toString().isEmpty()) {
                    Toast.makeText(CreateJournalEntryActivity.this, R.string.error_message_empty_narration, Toast.LENGTH_SHORT).show();
                    return;
                }

                // create new ledger
                debitLedgerTypeDialog.setTitle("Select Account Type of " + StringUtility.accountNameFormat(debitAccountName));
                creditLedgerTypeDialog.setTitle("Select Account Type of " + StringUtility.accountNameFormat(creditAccountName));
                if(!ledgerNameList.contains(debitAccountName) && !ledgerNameList.contains(creditAccountName)) {
                    debitLedgerTypeDialog.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {;
                        creditLedgerTypeDialog.show();
                    }).show();
                } else if(!ledgerNameList.contains(debitAccountName)) debitLedgerTypeDialog.show();
                else if(!ledgerNameList.contains(creditAccountName)) creditLedgerTypeDialog.show();
                else createEntry(debitAccountName, creditAccountName);

            }
        });
    }

    private void createEntry(String debitAccountName, String creditAccountName) {

        if (!ledgerNameList.contains(debitAccountName)) ledgerDao.insert(new Ledger(debitAccountName, debitLedgerType));
        if (!ledgerNameList.contains(creditAccountName)) ledgerDao.insert(new Ledger(creditAccountName, creditLedgerType));

        ledgerList = (ArrayList<Ledger>) ledgerDao.getAll();
        ledgerNameList.clear();
        ledgerNameList = (ArrayList<String>) ledgerDao.getAllNames();
        ledgerListArrayAdapter.clear();
        ledgerListArrayAdapter.addAll(ledgerNameList);
        ledgerListArrayAdapter.notifyDataSetChanged();

        if(entryDao.getLatestEntryTimestamp() < Calendar.getInstance().getTimeInMillis()) {
            entryDao.insert(new Entry(
                    ledgerList.get(ledgerNameList.indexOf(debitAccountName)).getId(),
                    ledgerList.get(ledgerNameList.indexOf(creditAccountName)).getId(),
                    Integer.parseInt(amountEditText.getText().toString()),
                    Calendar.getInstance().getTimeInMillis(),
                    narrationEditText.getText().toString()
            ));
            Toast.makeText(CreateJournalEntryActivity.this, R.string.journal_entry_create_success, Toast.LENGTH_SHORT).show();
            resetForm();
        } else Toast.makeText(CreateJournalEntryActivity.this, R.string.error_message_invalid_date_error, Toast.LENGTH_LONG).show();
    }

    private void resetForm() {
        debitAccountEditText.setText("");
        creditAccountEditText.setText("");
        amountEditText.setText("");
        narrationEditText.setText("");
    }

}