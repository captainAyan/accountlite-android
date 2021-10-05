package com.github.captainayan.accountlite.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.github.captainayan.accountlite.fragment.BalanceSheetFragment;
import com.github.captainayan.accountlite.fragment.IncomeStatementFragment;

public class FinalStatementViewPagerAdapter extends FragmentStateAdapter {

    private final IncomeStatementFragment incomeStatementFragment;
    private final BalanceSheetFragment balanceSheetFragment;

    public FinalStatementViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,
                                          IncomeStatementFragment incomeStatementFragment, BalanceSheetFragment balanceSheetFragment) {
        super(fragmentManager, lifecycle);

        this.incomeStatementFragment = incomeStatementFragment;
        this.balanceSheetFragment = balanceSheetFragment;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) return balanceSheetFragment;
        return incomeStatementFragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
