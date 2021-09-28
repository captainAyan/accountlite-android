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

import com.github.captainayan.accountlite.R;
import com.github.captainayan.accountlite.adapter.JournalAdapter;
import com.github.captainayan.accountlite.model.Journal;

import java.util.ArrayList;

public class LedgerEntriesFragment extends Fragment {

    private TextView emptyView;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private JournalAdapter adapter;
    private ArrayList<Journal> journalList;

    public LedgerEntriesFragment(ArrayList<Journal> journalList) {
        this.journalList = journalList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ledger_entries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emptyView = (TextView) view.findViewById(R.id.emptyView);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        adapter = new JournalAdapter(getContext(), journalList);
        manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        if (!journalList.isEmpty()) emptyView.setVisibility(View.INVISIBLE);
    }
}