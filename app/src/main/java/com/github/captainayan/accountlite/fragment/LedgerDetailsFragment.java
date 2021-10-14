package com.github.captainayan.accountlite.fragment;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.captainayan.accountlite.LedgerAccountActivity;
import com.github.captainayan.accountlite.R;
import com.github.captainayan.accountlite.database.AppDatabase;
import com.github.captainayan.accountlite.database.LedgerDao;
import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.utility.StringUtility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class LedgerDetailsFragment extends Fragment implements View.OnClickListener {

    // views
    private TextView accountOpeningBalanceTv, accountClosingBalanceTv, accountNameTv, accountTypeTv;
    private CardView changeNameButton, changeTypeButton;

    // ledger account type change
    private MaterialAlertDialogBuilder ledgerTypeChangeDialog;
    private int selectedLedgerType = 0;

    // ledger account name change
    private MaterialAlertDialogBuilder ledgerNameChangeDialog;
    private View v;
    private TextView newAccountNameEditText;
    private ArrayList<String> ledgerNameList;

    // preferences
    private String currencyFormat, currencySymbol, currencySymbolPosition;

    // other displayed info
    private int openingBalance, closingBalance;
    private Ledger ledger;

    // database
    private LedgerDao ledgerDao;

    public LedgerDetailsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ledger_details, container, false);

        // preferences
        currencyFormat = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(
                getContext().getResources().getString(R.string.currency_format_pref_key),
                getContext().getResources().getString(R.string.currency_format_default_value));
        currencySymbol = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(
                getContext().getResources().getString(R.string.currency_symbol_pref_key),
                getContext().getResources().getString(R.string.currency_symbol_default_value));
        currencySymbolPosition = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(
                getContext().getResources().getString(R.string.currency_symbol_position_pref_key),
                getContext().getResources().getString(R.string.currency_symbol_position_default_value));

        // get values
        LedgerAccountActivity l = (LedgerAccountActivity) getActivity();
        ledgerDao = AppDatabase.getAppDatabase(getContext()).ledgerDao();

        ledger = ledgerDao.getLedgerById(l.ledgerId);
        openingBalance = ledgerDao.getLedgerBalance(ledger.getId(), l.fromDateTimestamp);
        closingBalance = ledgerDao.getLedgerBalance(ledger.getId(), l.toDateTimestamp);

        accountNameTv = (TextView) view.findViewById(R.id.accountName);
        accountTypeTv = (TextView) view.findViewById(R.id.accountType);
        accountOpeningBalanceTv = (TextView) view.findViewById(R.id.accountOpeningBalance);
        accountClosingBalanceTv = (TextView) view.findViewById(R.id.accountClosingBalance);

        changeNameButton = (CardView) view.findViewById(R.id.change_ledger_name);
        changeTypeButton = (CardView) view.findViewById(R.id.change_ledger_type);
        changeNameButton.setOnClickListener(this);
        changeTypeButton.setOnClickListener(this);

        /* Type                  Balance
        0. Revenue Account     - CREDIT
        1. Expenditure Account - DEBIT
        2. Assets Account      - DEBIT
        3. Liabilities Account - CREDIT
        4. Equity Account      - CREDIT */

        boolean isDebitBalance = (ledger.getType() == Ledger.Type.EXPENDITURE || ledger.getType() == Ledger.Type.ASSET);

        // OPENING BALANCE
        if(openingBalance > 0) { // debit balance
            // if revenue, liability or equity
            if (!isDebitBalance) accountOpeningBalanceTv.setTextColor(getResources().getColor(R.color.red));
        }
        else if(openingBalance < 0) { // credit balance
            openingBalance *= -1;
            // if expenditure or asset
            if (isDebitBalance) accountOpeningBalanceTv.setTextColor(getResources().getColor(R.color.red));
        }
        accountOpeningBalanceTv.setText(StringUtility.amountFormat(openingBalance, currencyFormat, currencySymbol, currencySymbolPosition));
        accountOpeningBalanceTv.setSelected(true);

        // CLOSING BALANCE
        if(closingBalance > 0) { // debit balance
            // if revenue, liability or equity
            if (!isDebitBalance) accountClosingBalanceTv.setTextColor(getResources().getColor(R.color.red));
        }
        else if(closingBalance < 0) { // credit balance
            closingBalance *= -1;
            // if expenditure or asset
            if (isDebitBalance) accountClosingBalanceTv.setTextColor(getResources().getColor(R.color.red));
        }
        accountClosingBalanceTv.setText(StringUtility.amountFormat(closingBalance, currencyFormat, currencySymbol, currencySymbolPosition));
        accountClosingBalanceTv.setSelected(true);

        accountNameTv.setText(StringUtility.accountNameFormat(ledger.getName()));
        accountNameTv.setSelected(true);

        String accountType = getResources().getStringArray(R.array.ledger_types)[ledger.getType()];
        accountTypeTv.setText(accountType);

        // EDITING DIALOGS
        ledgerTypeChangeDialog = new MaterialAlertDialogBuilder(getContext(),
                R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle(R.string.change_ledger_type_dialog_title)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    Ledger newLedger = ledger;
                    newLedger.setType(selectedLedgerType);
                    ledgerDao.update(newLedger);
                    Toast.makeText(getContext(), R.string.saved, Toast.LENGTH_SHORT).show();
                    getActivity().recreate();
                })
                .setSingleChoiceItems(getResources().getStringArray(R.array.ledger_types),
                        ledger.getType(), (dialogInterface, i) -> selectedLedgerType = i);

        v = LayoutInflater.from(getContext())
                .inflate(R.layout.ledger_name_change_dialog, container, false);

        newAccountNameEditText = (TextView) v.findViewById(R.id.newAccountNameEditText);
        newAccountNameEditText.setText(ledger.getName());

        ledgerNameList = (ArrayList<String>) ledgerDao.getAllNames();
        ledgerNameChangeDialog = new MaterialAlertDialogBuilder(getContext(),
                R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle(R.string.change_ledger_name_dialog_title)
                .setView(v)
                .setPositiveButton(android.R.string.ok, ((dialogInterface, i) -> {
                    if (!ledgerNameList.contains(newAccountNameEditText.getText().toString())
                            && !newAccountNameEditText.getText().toString().trim().isEmpty()) {
                        Ledger newLedger = ledger;
                        newLedger.setName(newAccountNameEditText.getText().toString().trim());
                        ledgerDao.update(newLedger);
                        Toast.makeText(getContext(), R.string.saved, Toast.LENGTH_SHORT).show();

                        // this is a weird fix
                        accountNameTv.setText(StringUtility.accountNameFormat(newAccountNameEditText.getText().toString().trim()));
                        getActivity().recreate();
                    } else {
                        Toast.makeText(getContext(), R.string.error_message_invalid_account_name, Toast.LENGTH_SHORT).show();
                        if(v.getParent() != null) ((ViewGroup)v.getParent()).removeView(v);
                    }
                }))
                .setOnCancelListener(dialogInterface -> {if(v.getParent() != null) ((ViewGroup)v.getParent()).removeView(v);});

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.change_ledger_name) {
            ledgerNameChangeDialog.show();
        } else if(id == R.id.change_ledger_type) {
            ledgerTypeChangeDialog.show();
        }
    }

}