package com.github.captainayan.accountlite.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.captainayan.accountlite.LedgerAccountActivity;
import com.github.captainayan.accountlite.R;
import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.utility.StringUtility;

public class LedgerDetailsFragment extends Fragment {

    private TextView accountOpeningBalanceTv, accountClosingBalanceTv, accountNameTv, accountTypeTv;
    private String currencyFormat, currencySymbol, currencySymbolPosition;

    private int openingBalance, closingBalance;
    private Ledger ledger;

    public LedgerDetailsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currencyFormat = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(
                getContext().getResources().getString(R.string.currency_format_pref_key),
                getContext().getResources().getString(R.string.currency_format_default_value));

        currencySymbol = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(
                getContext().getResources().getString(R.string.currency_symbol_pref_key),
                getContext().getResources().getString(R.string.currency_symbol_default_value));

        currencySymbolPosition = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(
                getContext().getResources().getString(R.string.currency_symbol_position_pref_key),
                getContext().getResources().getString(R.string.currency_symbol_position_default_value));

        LedgerAccountActivity l = (LedgerAccountActivity) getActivity();
        openingBalance = l.openingBalance;
        closingBalance = l.closingBalance;
        ledger = l.ledger;

        return inflater.inflate(R.layout.fragment_ledger_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        accountNameTv = (TextView) view.findViewById(R.id.accountName);
        accountTypeTv = (TextView) view.findViewById(R.id.accountType);
        accountOpeningBalanceTv = (TextView) view.findViewById(R.id.accountOpeningBalance);
        accountClosingBalanceTv = (TextView) view.findViewById(R.id.accountClosingBalance);

        // accountOpeningBalanceTv.setText(StringUtility.amountFormat(openingBalance, currencyFormat, currencySymbol, currencySymbolPosition));
        // accountClosingBalanceTv.setText(StringUtility.amountFormat(closingBalance, currencyFormat, currencySymbol, currencySymbolPosition));

        if(openingBalance < 0) {
            openingBalance *= -1;
            if (!(ledger.getType() == 0 || ledger.getType() == 2))
                accountOpeningBalanceTv.setTextColor(getResources().getColor(R.color.red));
        }
        accountOpeningBalanceTv.setText(StringUtility.amountFormat(openingBalance, currencyFormat, currencySymbol, currencySymbolPosition));

        if(closingBalance < 0) {
            closingBalance *= -1;
            if (!(ledger.getType() == 0 || ledger.getType() == 2))
                accountClosingBalanceTv.setTextColor(getResources().getColor(R.color.red));
        }
        accountClosingBalanceTv.setText(StringUtility.amountFormat(closingBalance, currencyFormat, currencySymbol, currencySymbolPosition));

        accountNameTv.setText(StringUtility.accountNameFormat(ledger.getName()));
        accountNameTv.setSelected(true);

        String accountType = getResources().getStringArray(R.array.ledger_types)[ledger.getType()];
        accountTypeTv.setText(accountType);
    }
}