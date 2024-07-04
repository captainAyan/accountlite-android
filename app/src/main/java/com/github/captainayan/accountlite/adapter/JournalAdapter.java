package com.github.captainayan.accountlite.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.captainayan.accountlite.R;
import com.github.captainayan.accountlite.model.Journal;
import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.utility.JournalListUtility;
import com.github.captainayan.accountlite.utility.StringUtility;

import java.util.ArrayList;

class JournalViewHolder extends RecyclerView.ViewHolder {

    public TextView debitAccountName, creditAccountName, amount, narration, time, entryId;

    public JournalViewHolder(@NonNull View itemView) {
        super(itemView);

        debitAccountName = (TextView) itemView.findViewById(R.id.debitAccountName);
        creditAccountName = (TextView) itemView.findViewById(R.id.creditAccountName);
        amount = (TextView) itemView.findViewById(R.id.amount);
        narration = (TextView) itemView.findViewById(R.id.narration);
        time = (TextView) itemView.findViewById(R.id.time);
        entryId = (TextView) itemView.findViewById(R.id.entryId);
    }

    public void bind(Journal j, String currencyFormat, String currencySymbol, String currencySymbolPosition,
                     String dateFormat, String dateSeparator ) {
        debitAccountName.setText(StringUtility.accountNameFormat(j.getDebitLedger().getName()));
        creditAccountName.setText(StringUtility.accountNameFormat(j.getCreditLedger().getName()));
        amount.setText(StringUtility.amountFormat(j.getAmount(), currencyFormat, currencySymbol, currencySymbolPosition));
        narration.setText(StringUtility.narrationFormat(j.getNarration()));
        time.setText(StringUtility.dateFormat(j.getTimestamp(), dateFormat, dateSeparator));
        entryId.setText(StringUtility.idFormat(j.getId()));

        debitAccountName.setSelected(true);
        creditAccountName.setSelected(true);
    }
}

class MonthSeparatorViewHolder extends RecyclerView.ViewHolder {
    public TextView monthNameTextView;

    public MonthSeparatorViewHolder(@NonNull View itemView) {
        super(itemView);

        monthNameTextView = (TextView) itemView.findViewById(R.id.monthName);
    }

    public void bind(String monthName) {
        monthNameTextView.setText(monthName);
    }
}

public class JournalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static int TYPE_JOURNAL = 1;
    static int TYPE_MONTH = 2;

    private ArrayList<Object> journalListWithMonthSeparator;
    private Context ctx;

    private final String currencyFormat, currencySymbol, currencySymbolPosition, dateFormat, dateSeparator;

    // for ledger entries fragment
    private Ledger ledger = null;

    public JournalAdapter(Context ctx, ArrayList<Journal> journalList) {
        this.journalListWithMonthSeparator = JournalListUtility.createMonthSeparatedListFromJournalList(journalList);
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

        dateFormat = PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                ctx.getResources().getString(R.string.date_format_pref_key),
                ctx.getResources().getString(R.string.date_format_default_value));

        dateSeparator = PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                ctx.getResources().getString(R.string.date_separator_pref_key),
                ctx.getResources().getString(R.string.date_separator_default_value));
    }

    @Override
    public int getItemViewType(int position) {
        if (journalListWithMonthSeparator.get(position) instanceof Journal) return TYPE_JOURNAL;
        else return TYPE_MONTH;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_JOURNAL) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.journal_card, parent, false);
            return new JournalViewHolder(view);
        }
        else {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.month_separator_item, parent, false);
            return new MonthSeparatorViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_JOURNAL) {
            JournalViewHolder journalViewHolder = (JournalViewHolder) holder;

            if (ledger != null) {
                boolean isDebitBalance = (ledger.getType() == Ledger.Type.EXPENDITURE || ledger.getType() == Ledger.Type.ASSET);
                boolean isDebitEntry = ((Journal)journalListWithMonthSeparator.get(position)).getDebitLedger().getId() == ledger.getId();

                if ((isDebitBalance && !isDebitEntry) || (!isDebitBalance && isDebitEntry))
                    journalViewHolder.amount.setTextColor(ContextCompat.getColor(ctx, R.color.red));
                else journalViewHolder.amount.setTextColor(ContextCompat.getColor(ctx, R.color.green));
            }

            Journal j = (Journal)journalListWithMonthSeparator.get(position);
            journalViewHolder.bind(j, currencyFormat, currencySymbol, currencySymbolPosition, dateFormat, dateSeparator);
        }
        else {
            MonthSeparatorViewHolder monthSeparatorViewHolder = (MonthSeparatorViewHolder) holder;
            monthSeparatorViewHolder.bind((String)journalListWithMonthSeparator.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return journalListWithMonthSeparator.size();
    }

    public void setLedger(Ledger l) {
        this.ledger = l;
    }

    public void onOrderChange(ArrayList<Journal> journalList) {
        this.journalListWithMonthSeparator = JournalListUtility.createMonthSeparatedListFromJournalList(journalList);
        this.notifyDataSetChanged();
    }
}
