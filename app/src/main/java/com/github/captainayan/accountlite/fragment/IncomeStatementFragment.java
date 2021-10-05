package com.github.captainayan.accountlite.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.captainayan.accountlite.R;

public class IncomeStatementFragment  extends Fragment {
    public IncomeStatementFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_statement, container, false);
        return view;
    }
}
