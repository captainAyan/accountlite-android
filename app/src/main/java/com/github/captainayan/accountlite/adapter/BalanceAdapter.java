package com.github.captainayan.accountlite.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.captainayan.accountlite.R;
import com.github.captainayan.accountlite.model.TestBalance;

import java.util.ArrayList;

class BalanceViewHolder extends RecyclerView.ViewHolder {

    TextView amount, account;

    BalanceViewHolder(@NonNull View itemView) {
        super(itemView);
        amount = itemView.findViewById(R.id.amountView);
        account = itemView.findViewById(R.id.accountView);
    }
}

public class BalanceAdapter extends RecyclerView.Adapter<BalanceViewHolder>  {

    private ArrayList<TestBalance> balances;
    private Context ctx;

    public BalanceAdapter(Context ctx, ArrayList<TestBalance> balances) {
        this.balances = balances;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.balance_card, parent, false);
        return new BalanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder holder, int position) {
        final TestBalance d = balances.get(position);
        holder.amount.setText(d.amount);
        holder.account.setText(d.account);
    }

    @Override
    public int getItemCount() {
        return balances.size();
    }
}
