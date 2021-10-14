package com.github.captainayan.accountlite.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.github.captainayan.accountlite.FinalStatementActivity;
import com.github.captainayan.accountlite.R;
import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.utility.StringUtility;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class BalanceSheetFragment extends Fragment {

    // preferences
    private String currencyFormat, currencySymbol, currencySymbolPosition;

    // views
    private TextView totalLiabilityTextView;
    private TextView totalAssetTextView;
    private TextView totalEquityTextView;
    private LinearLayout liabilityListView;
    private LinearLayout assetListView;
    private LinearLayout equityListView;

    // data
    private int totalLiability = 0;
    private int totalEquity = 0;
    private int totalAsset = 0;
    private int surplusOrDeficit = 0;
    private ArrayList<Ledger.LedgerWithBalance> liabilityLedgerWithBalanceList;
    private ArrayList<Ledger.LedgerWithBalance> equityLedgerWithBalanceList;
    private ArrayList<Ledger.LedgerWithBalance> assetLedgerWithBalanceList;

    public BalanceSheetFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_balance_sheet, container, false);

        liabilityLedgerWithBalanceList = new ArrayList<Ledger.LedgerWithBalance>();
        equityLedgerWithBalanceList = new ArrayList<Ledger.LedgerWithBalance>();
        assetLedgerWithBalanceList = new ArrayList<Ledger.LedgerWithBalance>();

        for (Ledger.LedgerWithBalance l : ((FinalStatementActivity)getActivity()).ledgerWithBalanceList) {
            if (l.getType() == Ledger.Type.EXPENDITURE) surplusOrDeficit -= l.getBalance();
            else if (l.getType() == Ledger.Type.REVENUE) surplusOrDeficit += l.getBalance()*-1;
            else if (l.getType() == Ledger.Type.ASSET) {
                totalAsset += l.getBalance();
                assetLedgerWithBalanceList.add(l);
            }
            else if (l.getType() == Ledger.Type.LIABILITY) {
                totalLiability += l.getBalance() * -1;
                liabilityLedgerWithBalanceList.add(l);
            }
            else if (l.getType() == Ledger.Type.EQUITY) {
                totalEquity += l.getBalance() * -1;
                equityLedgerWithBalanceList.add(l);
            }
        }

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

        // views
        totalLiabilityTextView = (TextView) v.findViewById(R.id.liability_total_amount);
        totalAssetTextView = (TextView) v.findViewById(R.id.asset_total_amount);
        totalEquityTextView = (TextView) v.findViewById(R.id.equity_total_amount);
        liabilityListView = (LinearLayout) v.findViewById(R.id.liability_list);
        assetListView = (LinearLayout) v.findViewById(R.id.asset_list);
        equityListView = (LinearLayout) v.findViewById(R.id.equity_list);

        totalEquity += surplusOrDeficit;
        totalLiability += totalEquity;

        totalLiabilityTextView.setText(
                StringUtility.amountFormat(totalLiability, currencyFormat, currencySymbol, currencySymbolPosition));
        totalAssetTextView.setText(
                StringUtility.amountFormat(totalAsset, currencyFormat, currencySymbol, currencySymbolPosition));
        totalEquityTextView.setText(
                StringUtility.amountFormat(totalEquity, currencyFormat, currencySymbol, currencySymbolPosition));

        // EQUITY
        for (Ledger.LedgerWithBalance l : equityLedgerWithBalanceList) {
            View i = getLayoutInflater().inflate(R.layout.statement_item, equityListView, false);
            ((TextView)i.findViewById(R.id.item_name)).setText(StringUtility.accountNameFormat(l.getName()));
            ((TextView)i.findViewById(R.id.item_amount)).setText(
                    StringUtility.amountFormat(l.getBalance()*-1,currencyFormat, currencySymbol, currencySymbolPosition));
            equityListView.addView(i);
        }

        // Adjusting Surplus or Deficit to EQUITY
        View _i = getLayoutInflater().inflate(R.layout.statement_item, equityListView, false);
        if(surplusOrDeficit >= 0) ((TextView)_i.findViewById(R.id.item_name)).setText(R.string.balance_sheet_surplus_label);
        else ((TextView)_i.findViewById(R.id.item_name)).setText(R.string.balance_sheet_deficit_label);
        ((TextView)_i.findViewById(R.id.item_amount)).setText(
                StringUtility.amountFormat(surplusOrDeficit,currencyFormat, currencySymbol, currencySymbolPosition));
        equityListView.addView(_i);

        // LIABILITY
        for (Ledger.LedgerWithBalance l : liabilityLedgerWithBalanceList) {
            View i = getLayoutInflater().inflate(R.layout.statement_item, liabilityListView, false);
            ((TextView)i.findViewById(R.id.item_name)).setText(StringUtility.accountNameFormat(l.getName()));
            ((TextView)i.findViewById(R.id.item_amount)).setText(
                    StringUtility.amountFormat(l.getBalance()*-1,currencyFormat, currencySymbol, currencySymbolPosition));
            liabilityListView.addView(i);
        }

        // ASSET
        for (Ledger.LedgerWithBalance l : assetLedgerWithBalanceList) {
            View i = getLayoutInflater().inflate(R.layout.statement_item, assetListView, false);
            ((TextView)i.findViewById(R.id.item_name)).setText(StringUtility.accountNameFormat(l.getName()));
            ((TextView)i.findViewById(R.id.item_amount)).setText(
                    StringUtility.amountFormat(l.getBalance(),currencyFormat, currencySymbol, currencySymbolPosition));
            assetListView.addView(i);
        }

        return v;
    }
}
