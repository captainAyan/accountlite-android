package com.github.captainayan.accountlite.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.github.captainayan.accountlite.R;
import com.github.captainayan.accountlite.utility.StringUtility;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class LedgerSelectionBottomSheetFragment extends BottomSheetDialogFragment {

    private ArrayList<String> ledgerNameList;

    private MaterialButton submit;
    private ChipGroup chipGroup;

    private LedgerSelectionBottomSheetFragment.OnLedgerSelectListener t;

    public void setLedgerNameList(ArrayList<String> ledgerNameList) {
        this.ledgerNameList = ledgerNameList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ledger_selection_bottom_sheet, container, false);
        submit = v.findViewById(R.id.submit);
        chipGroup = (ChipGroup) v.findViewById(R.id.chipGroup);

        for (String ledgerName : ledgerNameList) {
            Chip chip = (Chip) inflater.inflate(R.layout.ledger_selection_chip, chipGroup, false);
            chip.setText(StringUtility.accountNameFormat(ledgerName));
            chipGroup.addView(chip);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chipGroup.getCheckedChipIds().isEmpty()) {
                    Toast.makeText(getContext(), "Select one Account", Toast.LENGTH_SHORT).show();
                } else {
                    String val = (String) ((Chip)chipGroup
                            .findViewById(chipGroup.getCheckedChipId())
                    ).getText();

                    t.onSelect(ledgerNameList.indexOf(val.substring(0, val.length()-4))); // the substring removes the " A/c" part
                }
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = (FrameLayout)dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(0);
            }
        });
    }

    public void setOnLedgerSelectListener(LedgerSelectionBottomSheetFragment.OnLedgerSelectListener t) {
        this.t = t;
    }

    public interface OnLedgerSelectListener {
        void onSelect(int checkedChipId);
    }
}