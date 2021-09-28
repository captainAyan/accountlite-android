package com.github.captainayan.accountlite.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.captainayan.accountlite.R;
import com.github.captainayan.accountlite.model.Journal;
import com.github.captainayan.accountlite.utility.StringUtility;

import java.util.ArrayList;

class JournalViewHolder extends RecyclerView.ViewHolder {

    TextView debitAccountName, creditAccountName, amount, narration, time, entryId;

    public JournalViewHolder(@NonNull View itemView) {
        super(itemView);

        debitAccountName = (TextView) itemView.findViewById(R.id.debitAccountName);
        creditAccountName = (TextView) itemView.findViewById(R.id.creditAccountName);
        amount = (TextView) itemView.findViewById(R.id.amount);
        narration = (TextView) itemView.findViewById(R.id.narration);
        time = (TextView) itemView.findViewById(R.id.time);
        entryId = (TextView) itemView.findViewById(R.id.entryId);
    }
}

public class JournalAdapter extends RecyclerView.Adapter<JournalViewHolder> {

    private ArrayList<Journal> journalList;
    private Context ctx;

    private String currencyFormat;
    private String currencySymbol;
    private String currencySymbolPosition;

    public JournalAdapter(Context ctx, ArrayList<Journal> journalList) {
        this.journalList = journalList;
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
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.journal_card, parent, false);
        return new JournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        Journal j = journalList.get(position);
        holder.debitAccountName.setText(StringUtility.accountNameFormat(j.getDebitLedger().getName()));
        holder.creditAccountName.setText(StringUtility.accountNameFormat(j.getCreditLedger().getName()));
        holder.amount.setText(StringUtility.amountFormat(j.getAmount(), currencyFormat, currencySymbol, currencySymbolPosition));
        holder.narration.setText(StringUtility.narrationFormat(j.getNarration()));
        holder.time.setText(StringUtility.dateFormat(j.getTimestamp()));
        holder.entryId.setText(StringUtility.idFormat(j.getId()));

        holder.debitAccountName.setSelected(true);
        holder.creditAccountName.setSelected(true);
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }
}
