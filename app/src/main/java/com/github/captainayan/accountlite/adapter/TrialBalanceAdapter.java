package com.github.captainayan.accountlite.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.captainayan.accountlite.R;
import com.github.captainayan.accountlite.model.Journal;
import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.utility.StringUtility;

import java.util.ArrayList;

class TrialBalanceViewHolder extends RecyclerView.ViewHolder {

    public TextView accountName, accountType, accountBalance;

    public TrialBalanceViewHolder(@NonNull View itemView) {
        super(itemView);

        accountName = (TextView) itemView.findViewById(R.id.accountName);
        accountType = (TextView) itemView.findViewById(R.id.accountType);
        accountBalance = (TextView) itemView.findViewById(R.id.accountBalance);
    }
}

public class TrialBalanceAdapter extends RecyclerView.Adapter<TrialBalanceViewHolder> {

    private ArrayList<Ledger.LedgerWithBalance> ledgerWithBalanceList;
    private Context ctx;

    private final String currencyFormat, currencySymbol, currencySymbolPosition;

    public TrialBalanceAdapter(Context ctx, ArrayList<Ledger.LedgerWithBalance> ledgerWithBalanceList) {
        this.ledgerWithBalanceList = ledgerWithBalanceList;
        this.ctx = ctx;

        currencyFormat = PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                ctx.getResources().getString(R.string.currency_format_pref_key),
                ctx.getResources().getString(R.string.currency_format_default_value));

        currencySymbol = PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                ctx.getResources().getString(R.string.currency_symbol_pref_key),
                ctx.getResources().getString(R.string.currency_symbol_default_value));

        currencySymbolPosition = PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                ctx.getResources().getString(R.string.currency_symbol_position_pref_key),
                ctx.getResources().getString(R.string.currency_symbol_position_default_value));
    }

    @NonNull
    @Override
    public TrialBalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.ledger_card, parent, false);
        return new TrialBalanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrialBalanceViewHolder holder, int position) {
        Ledger.LedgerWithBalance l = ledgerWithBalanceList.get(position);

        boolean isDebitBalance = (l.getType() == Ledger.Type.EXPENDITURE || l.getType() == Ledger.Type.ASSET);

        int balance = l.getBalance();

        // BALANCE
        if(balance > 0) { // debit balance
            // if revenue, liability or equity
            if (!isDebitBalance) holder.accountBalance.setTextColor(ctx.getResources().getColor(R.color.red));
        }
        else if(balance < 0) { // credit balance
            balance *= -1;
            // if expenditure or asset
            if (isDebitBalance) holder.accountBalance.setTextColor(ctx.getResources().getColor(R.color.red));
        }
        holder.accountBalance.setText(StringUtility.amountFormat(balance, currencyFormat, currencySymbol, currencySymbolPosition));

        holder.accountName.setText(StringUtility.accountNameFormat(l.getName()));

        String accountType = ctx.getResources().getStringArray(R.array.ledger_types)[l.getType()];
        holder.accountType.setText(accountType);
    }

    @Override
    public int getItemCount() {
        return ledgerWithBalanceList.size();
    }
}
