package com.github.captainayan.accountlite;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.github.captainayan.accountlite.database.AppDatabase;
import com.github.captainayan.accountlite.database.EntryDao;
import com.github.captainayan.accountlite.database.LedgerDao;
import com.github.captainayan.accountlite.model.Ledger;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Objects;

public class CreateVoucherActivity extends AppCompatActivity implements View.OnClickListener {

    private Button receiptBtn;
    private Button paymentBtn;
    private MaterialToolbar toolbar;
    private AutoCompleteTextView accountEditText, headEditText;
    private EditText amountEditText, narrationEditText;

    private AppDatabase db;
    private LedgerDao ledgerDao;
    private EntryDao entryDao;

    // for new ledgers
    private String[] ledgerTypes;
    private ArrayList<Ledger> ledgerList;
    private ArrayList<String> accountNameList;
    private ArrayList<String> headNameList;
    private ArrayAdapter accountListArrayAdapter;
    private ArrayAdapter headListArrayAdapter;
    private int accountLedgerType = 0;
    private int headLedgerType = 0;
    private MaterialAlertDialogBuilder debitLedgerTypeDialog;
    private MaterialAlertDialogBuilder creditLedgerTypeDialog;

    /**
     * Account - The Asset/Liability involved
     * Head - The Revenue/Expenditure involved
     *
     * Case 1. Receipt
     * Account To. Head
     * e.g.
     * Bank A/c
     * To. Sales A/c
     *
     * Cash A/c
     * To. Sales A/c
     *
     * Debtor A/c
     * To. Sales A/c
     *
     * Case 2. Payment
     * Head To. Account
     * e.g.
     * Income Tax A/c
     * To. Bank A/c
     *
     * General Expenses A/c
     * To. Cash A/c
     *
     * Purchase A/c
     * To. Creditor A/c
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_voucher);

        db = AppDatabase.getAppDatabase(this);
        ledgerDao = db.ledgerDao();
        entryDao = db.entryDao();

        ledgerTypes = getResources().getStringArray(R.array.ledger_types);

        ledgerList = (ArrayList<Ledger>) ledgerDao.getAll();
        accountNameList = new ArrayList<String>();
        headNameList = new ArrayList<String>();

        for(Ledger l : ledgerList) {
            if (l.getType() == Ledger.Type.ASSET && l.getType() == Ledger.Type.LIABILITY) accountNameList.add(l.getName());
            if (l.getType() == Ledger.Type.REVENUE && l.getType() == Ledger.Type.EXPENDITURE) headNameList.add(l.getName());
        }

        accountListArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, accountNameList);
        headListArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, headNameList);

        accountEditText = (AutoCompleteTextView) findViewById(R.id.accountEditText);
        headEditText = (AutoCompleteTextView) findViewById(R.id.headEditText);
        accountEditText.setAdapter(accountListArrayAdapter);
        headEditText.setAdapter(headListArrayAdapter);

        amountEditText = (EditText) findViewById(R.id.amountEditText);
        narrationEditText = (EditText) findViewById(R.id.narrationEditText);
        receiptBtn = (Button) findViewById(R.id.receiptBtn);
        paymentBtn = (Button) findViewById(R.id.paymentBtn);

        receiptBtn.setOnClickListener(this);
        paymentBtn.setOnClickListener(this);

        toolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateVoucherActivity.this.finish();
            }
        });

    }

    @Override
    public void onClick(View view) {

    }

    private void resetForm() {
        accountEditText.setText("");
        headEditText.setText("");
        amountEditText.setText("");
        narrationEditText.setText("");
    }
}