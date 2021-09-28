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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class CreateJournalEntryActivity extends AppCompatActivity {

    public static final String TAG = "CREATE_ENTRY_ACT";

    private Button submitBtn;
    private MaterialToolbar toolbar;
    private AutoCompleteTextView debitAccount, creditAccount;
    private EditText amount, narration;

    private AppDatabase db;
    private LedgerDao ledgerDao;
    private EntryDao entryDao;

    // for new ledgers
    private String[] ledgerTypes;
    private ArrayList<Ledger> ledgerList;
    private ArrayList<String> ledgerNameList;
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
        ledgerNameList = new ArrayList<>();
        for(Ledger l : ledgerList) { ledgerNameList.add(l.getName()); }
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ledgerNameList);

        debitAccount = (AutoCompleteTextView) findViewById(R.id.debitEditText);
        creditAccount = (AutoCompleteTextView) findViewById(R.id.creditEditText);
        debitAccount.setAdapter(arrayAdapter);
        creditAccount.setAdapter(arrayAdapter);

        amount = (EditText) findViewById(R.id.amountEditText);
        narration = (EditText) findViewById(R.id.narrationEditText);
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

        debitLedgerTypeDialog = new MaterialAlertDialogBuilder(this)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        ledgerDao.insert(new Ledger(debitAccount.getText().toString().trim().toLowerCase(), debitLedgerType));
                        createEntry(debitAccount.getText().toString().trim().toLowerCase(),
                                creditAccount.getText().toString().trim().toLowerCase());
                })
                .setSingleChoiceItems(ledgerTypes, 0, (dialogInterface, i) -> debitLedgerType = i)
                .setCancelable(false);

        creditLedgerTypeDialog = new MaterialAlertDialogBuilder(this)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        ledgerDao.insert(new Ledger(creditAccount.getText().toString().trim().toLowerCase(), creditLedgerType));
                        createEntry(debitAccount.getText().toString().trim().toLowerCase(),
                                creditAccount.getText().toString().trim().toLowerCase());
                })
                .setSingleChoiceItems(ledgerTypes, 0, (dialogInterface, i) -> creditLedgerType = i)
                .setCancelable(false);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String debitAccountName = debitAccount.getText().toString().trim().toLowerCase();
                String creditAccountName = creditAccount.getText().toString().trim().toLowerCase();

                if(debitAccountName.isEmpty() || creditAccountName.isEmpty()) {
                    Toast.makeText(CreateJournalEntryActivity.this, "Accounts cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(debitAccountName.equals(creditAccountName)) {
                    Toast.makeText(CreateJournalEntryActivity.this, "Debit and Credit account cannot be the same.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(amount.getText().toString().isEmpty()) {
                    Toast.makeText(CreateJournalEntryActivity.this, "Invalid Amount.", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (Integer.parseInt(amount.getText().toString()) == 0) {
                        Toast.makeText(CreateJournalEntryActivity.this, "Invalid Amount.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                catch(NumberFormatException e) {
                    Toast.makeText(CreateJournalEntryActivity.this, "Invalid Amount.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(narration.getText().toString().isEmpty()) {
                    Toast.makeText(CreateJournalEntryActivity.this, "Narration cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // create new ledger
                debitLedgerTypeDialog.setTitle("Select Account Type of " + debitAccountName);
                creditLedgerTypeDialog.setTitle("Select Account Type of " + creditAccountName);
                if(!ledgerNameList.contains(debitAccountName) && !ledgerNameList.contains(creditAccountName)) {
                    debitLedgerTypeDialog.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        ledgerDao.insert(new Ledger(debitAccountName, debitLedgerType));
                        creditLedgerTypeDialog.show();
                    }).show();
                } else if(!ledgerNameList.contains(debitAccountName)) debitLedgerTypeDialog.show();
                else if(!ledgerNameList.contains(creditAccountName)) creditLedgerTypeDialog.show();
                else createEntry(debitAccountName, creditAccountName);

            }
        });


    }

    private void createEntry(String debitAccountName, String creditAccountName) {
        ledgerList = (ArrayList<Ledger>) ledgerDao.getAll();
        ledgerNameList.clear();
        for(Ledger l : ledgerList) { ledgerNameList.add(l.getName()); }

        entryDao.insert(new Entry(
                ledgerList.get(ledgerNameList.indexOf(debitAccountName)).getId(),
                ledgerList.get(ledgerNameList.indexOf(creditAccountName)).getId(),
                Integer.parseInt(amount.getText().toString()),
                Calendar.getInstance().getTimeInMillis(),
                narration.getText().toString()
        ));

        Toast.makeText(CreateJournalEntryActivity.this, "Saved Entry", Toast.LENGTH_SHORT).show();
        finish();
    }

}