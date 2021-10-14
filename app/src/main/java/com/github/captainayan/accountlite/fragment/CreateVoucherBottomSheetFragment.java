package com.github.captainayan.accountlite.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.github.captainayan.accountlite.MainActivity;
import com.github.captainayan.accountlite.R;
import com.github.captainayan.accountlite.model.Entry;
import com.github.captainayan.accountlite.model.Ledger;
import com.github.captainayan.accountlite.utility.StringUtility;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateVoucherBottomSheetFragment extends BottomSheetDialogFragment {

    private ArrayList<Ledger> ledgerList;

    private MaterialButton receiptBtn;
    private MaterialButton paymentBtn;
    private ChipGroup accountChipGroup;
    private ChipGroup headChipGroup;
    private EditText amountEditText;

    private OnVoucherCreateListener t;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_voucher_bottom_sheet, container, false);
        receiptBtn = v.findViewById(R.id.receiptBtn);
        paymentBtn = v.findViewById(R.id.paymentBtn);
        accountChipGroup = (ChipGroup) v.findViewById(R.id.accountChipGroup);
        headChipGroup = (ChipGroup) v.findViewById(R.id.headChipGroup);
        amountEditText = (EditText) v.findViewById(R.id.amountEditText);

        ledgerList = ((MainActivity)getActivity()).ledgerList;

        for (Ledger l : ledgerList) {
            if (l.getType() == Ledger.Type.EXPENDITURE || l.getType() == Ledger.Type.REVENUE) {
                Chip chip = (Chip) inflater.inflate(R.layout.ledger_selection_chip, container, false);
                chip.setText(StringUtility.accountNameFormat(l.getName()));
                chip.setId(l.getId());
                headChipGroup.addView(chip);
            }
            else if (l.getType() == Ledger.Type.ASSET || l.getType() == Ledger.Type.LIABILITY) {
                Chip chip = (Chip) inflater.inflate(R.layout.ledger_selection_chip, container, false);
                chip.setText(StringUtility.accountNameFormat(l.getName()));
                chip.setId(l.getId());
                accountChipGroup.addView(chip);
            }
        }

        paymentBtn.setOnClickListener(view -> createVoucher(false));

        receiptBtn.setOnClickListener(view -> createVoucher(true));

        return v;
    }
    
    public void createVoucher(boolean isReceipt) {
        if (headChipGroup.getCheckedChipId()==-1 || accountChipGroup.getCheckedChipId()==-1) {
            Toast.makeText(getContext(), R.string.error_message_select_account, Toast.LENGTH_SHORT).show();
            return;
        }
        if(amountEditText.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), R.string.error_message_invalid_amount, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (Integer.parseInt(amountEditText.getText().toString()) == 0) {
                Toast.makeText(getContext(), R.string.error_message_invalid_amount, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        catch(NumberFormatException e) {
            Toast.makeText(getContext(), R.string.error_message_invalid_amount, Toast.LENGTH_SHORT).show();
            return;
        }
        if (isReceipt)
            t.onSelect(new Entry(accountChipGroup.getCheckedChipId(), headChipGroup.getCheckedChipId(),
                    Integer.parseInt(amountEditText.getText().toString()), Calendar.getInstance().getTimeInMillis(),
                    "Receipt Voucher"));
        else
            t.onSelect(new Entry(headChipGroup.getCheckedChipId(), accountChipGroup.getCheckedChipId(),
                    Integer.parseInt(amountEditText.getText().toString()), Calendar.getInstance().getTimeInMillis(),
                    "Payment Voucher"));

        Toast.makeText(getContext(), R.string.voucher_created_success, Toast.LENGTH_SHORT).show();
        headChipGroup.clearCheck();
        accountChipGroup.clearCheck();
        amountEditText.setText("");
    }

    public void setOnVoucherCreateListener(OnVoucherCreateListener t) {
        this.t = t;
    }

    public interface OnVoucherCreateListener {
        void onSelect(Entry e);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            FrameLayout bottomSheet = (FrameLayout)dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setPeekHeight(0);
        });
    }

}
