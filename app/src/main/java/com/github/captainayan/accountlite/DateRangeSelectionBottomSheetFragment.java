package com.github.captainayan.accountlite;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DateRangeSelectionBottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateRangeSelectionBottomSheetFragment extends BottomSheetDialogFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private DatePicker datePicker;
    private MaterialButton submit;
    private ChipGroup chipGroup;
    private Intent intent;

    public DateRangeSelectionBottomSheetFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomSheetFragment.
     */
    public static DateRangeSelectionBottomSheetFragment newInstance(String param1, String param2) {
        DateRangeSelectionBottomSheetFragment fragment = new DateRangeSelectionBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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
                Intent i = intent;
                i.putExtra("day", datePicker.getDayOfMonth());
                i.putExtra("month", datePicker.getMonth());
                i.putExtra("year", datePicker.getYear());

                List<Integer> ids = chipGroup.getCheckedChipIds();
                if (ids.isEmpty()) i.putExtra("duration", "day");
                else if (ids.get(0) == R.id.weekChip) i.putExtra("duration", "week");
                else if (ids.get(0) == R.id.fortniteChip) i.putExtra("duration", "fortnite");
                else if (ids.get(0) == R.id.monthChip) i.putExtra("duration", "month");

                startActivity(i);
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

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}