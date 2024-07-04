package com.github.captainayan.accountlite.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.captainayan.accountlite.LedgerAccountActivity;
import com.github.captainayan.accountlite.R;
import com.github.captainayan.accountlite.adapter.JournalAdapter;
import com.github.captainayan.accountlite.database.AppDatabase;
import com.github.captainayan.accountlite.database.EntryDao;
import com.github.captainayan.accountlite.database.LedgerDao;
import com.github.captainayan.accountlite.model.Journal;
import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.utility.JournalListUtility;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LedgerEntriesFragment extends Fragment {

    private TextView emptyView;

    private ChipGroup chipGroup;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private JournalAdapter adapter;

    private ArrayList<Journal> journalList = new ArrayList<>();
    private Ledger ledger;
    private EntryDao entryDao;
    private LedgerDao ledgerDao;

    public LedgerEntriesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        entryDao = AppDatabase.getAppDatabase(getContext()).entryDao();
        ledgerDao = AppDatabase.getAppDatabase(getContext()).ledgerDao();

        LedgerAccountActivity l = (LedgerAccountActivity) getActivity();
        journalList = (ArrayList<Journal>) entryDao.getJournalsByLedger(
                l.ledgerId, l.toAndFromDate.fromDateTimestamp, l.toAndFromDate.toDateTimestamp);
        ledger = ledgerDao.getLedgerById(l.ledgerId);
        return inflater.inflate(R.layout.fragment_ledger_entries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emptyView = (TextView) view.findViewById(R.id.emptyView);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // ArrayList<Object> journalListWithMonthSeparator = JournalListUtility.createMonthSeparatedListFromJournalList(journalList);
        // adapter = new JournalAdapter(getContext(), journalListWithMonthSeparator);
        adapter = new JournalAdapter(getContext(), journalList);
        adapter.setLedger(ledger);
        manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        if (!journalList.isEmpty()) emptyView.setVisibility(View.INVISIBLE);

        chipGroup = (ChipGroup) view.findViewById(R.id.chipGroup);
        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                if ((journalList.size() > 2) &&
                        ((journalList.get(0).getTimestamp() > journalList.get(1).getTimestamp() // newest first
                                && checkedIds.get(0) == R.id.oldestFirstChip)
                                || (journalList.get(0).getTimestamp() < journalList.get(1).getTimestamp() // oldest first
                                && checkedIds.get(0) == R.id.newestFirstChip))
                ) {
                    // newest first
                    Collections.reverse(journalList);
                    adapter.onOrderChange(journalList);
                }
            }
        });
    }
}