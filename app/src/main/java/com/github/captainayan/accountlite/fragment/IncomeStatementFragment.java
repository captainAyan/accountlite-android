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

import java.util.ArrayList;

public class IncomeStatementFragment  extends Fragment {
    // preferences
    private String currencyFormat, currencySymbol, currencySymbolPosition;

    // views
    private TextView totalExpenditureTextView;
    private TextView totalRevenueTextView;
    private TextView surplusOrDeficitTextView;
    private TextView surplusOrDeficitLabelTextView;
    private LinearLayout expenditureListView;
    private LinearLayout revenueListView;

    // data
    private int totalExpenditure = 0;
    private int totalRevenue = 0;
    private int surplusOrDeficit = 0;
    private ArrayList<Ledger.LedgerWithBalance> expenditureLedgerWithBalanceList;
    private ArrayList<Ledger.LedgerWithBalance> revenueLedgerWithBalanceList;

    public IncomeStatementFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_income_statement, container, false);

        expenditureLedgerWithBalanceList = new ArrayList<Ledger.LedgerWithBalance>();
        revenueLedgerWithBalanceList = new ArrayList<Ledger.LedgerWithBalance>();

        for (Ledger.LedgerWithBalance l : ((FinalStatementActivity)getActivity()).ledgerWithBalanceList) {
            if (l.getType() == Ledger.Type.EXPENDITURE) {
                totalExpenditure += l.getBalance();
                expenditureLedgerWithBalanceList.add(l);
            }
            else if (l.getType() == Ledger.Type.REVENUE) {
                totalRevenue += l.getBalance()*-1;
                revenueLedgerWithBalanceList.add(l);
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
        totalExpenditureTextView = (TextView) v.findViewById(R.id.expenditure_total_amount);
        totalRevenueTextView = (TextView) v.findViewById(R.id.revenue_total_amount);
        surplusOrDeficitTextView = (TextView) v.findViewById(R.id.surplus_amount);
        surplusOrDeficitLabelTextView = (TextView) v.findViewById(R.id.surplus_label);
        expenditureListView = (LinearLayout) v.findViewById(R.id.expenditure_list);
        revenueListView = (LinearLayout) v.findViewById(R.id.revenue_list);

        surplusOrDeficit = totalRevenue-totalExpenditure;

        totalExpenditureTextView.setText(
                StringUtility.amountFormat(totalExpenditure,currencyFormat, currencySymbol, currencySymbolPosition));
        totalRevenueTextView.setText(
                StringUtility.amountFormat(totalRevenue,currencyFormat, currencySymbol, currencySymbolPosition));
        surplusOrDeficitTextView.setText(
                StringUtility.amountFormat(surplusOrDeficit,currencyFormat, currencySymbol, currencySymbolPosition));
        if(surplusOrDeficit >= 0) surplusOrDeficitLabelTextView.setText(R.string.surplus);
        else surplusOrDeficitLabelTextView.setText(R.string.deficit);

        for (Ledger.LedgerWithBalance l : expenditureLedgerWithBalanceList) {
            View i = getLayoutInflater().inflate(R.layout.statement_item, expenditureListView, false);
            ((TextView)i.findViewById(R.id.item_name)).setText(StringUtility.accountNameFormat(l.getName()));
            ((TextView)i.findViewById(R.id.item_amount)).setText(
                    StringUtility.amountFormat(l.getBalance(),currencyFormat, currencySymbol, currencySymbolPosition));
            expenditureListView.addView(i);
        }

        for (Ledger.LedgerWithBalance l : revenueLedgerWithBalanceList) {
            View i = getLayoutInflater().inflate(R.layout.statement_item, revenueListView, false);
            ((TextView)i.findViewById(R.id.item_name)).setText(StringUtility.accountNameFormat(l.getName()));
            ((TextView)i.findViewById(R.id.item_amount)).setText(
                    StringUtility.amountFormat(l.getBalance()*-1,currencyFormat, currencySymbol, currencySymbolPosition));
            revenueListView.addView(i);
        }

        return v;
    }
}
