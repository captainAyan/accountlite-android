package com.github.captainayan.accountlite.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.FrameLayout;

import com.github.captainayan.accountlite.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;

import java.util.Date;
import java.util.List;

public class DateRangeSelectionBottomSheetFragment extends BottomSheetDialogFragment {

    private DatePicker datePicker;
    private MaterialButton submit;
    private ChipGroup chipGroup;

    private DateRangeSelectionBottomSheetFragment.OnTimeSelectListener t;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_date_range_selection_bottom_sheet, container, false);
        datePicker = v.findViewById(R.id.datePicker);
        submit = v.findViewById(R.id.submit);
        chipGroup = (ChipGroup) v.findViewById(R.id.chipGroup);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("day", datePicker.getDayOfMonth());
                i.putExtra("month", datePicker.getMonth());
                i.putExtra("year", datePicker.getYear());

                int id = chipGroup.getCheckedChipId();
                if (id == -1) i.putExtra("duration", getResources().getString(R.string.default_date_range_default_values)); // default
                else if (id == R.id.weekChip) i.putExtra("duration", "week");
                else if (id == R.id.fortniteChip) i.putExtra("duration", "fortnite");
                else if (id == R.id.monthChip) i.putExtra("duration", "month");

                if (t != null) t.onSelect(i);
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

    public void setOnTimeSelectListener(OnTimeSelectListener t) {
        this.t = t;
    }

    public interface OnTimeSelectListener {
        void onSelect(Intent intent);
    }
}