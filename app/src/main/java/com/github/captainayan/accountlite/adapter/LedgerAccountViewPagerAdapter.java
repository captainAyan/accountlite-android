package com.github.captainayan.accountlite.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.github.captainayan.accountlite.fragment.LedgerDetailsFragment;
import com.github.captainayan.accountlite.fragment.LedgerEntriesFragment;

public class LedgerAccountViewPagerAdapter extends FragmentStateAdapter {

    private LedgerEntriesFragment ledgerEntriesFragment;
    private LedgerDetailsFragment ledgerDetailsFragment;

    public LedgerAccountViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,
                                         LedgerEntriesFragment ledgerEntriesFragment, LedgerDetailsFragment ledgerDetailsFragment) {
        super(fragmentManager, lifecycle);

        this.ledgerEntriesFragment = ledgerEntriesFragment;
        this.ledgerDetailsFragment = ledgerDetailsFragment;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) return ledgerEntriesFragment;
        return ledgerDetailsFragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
