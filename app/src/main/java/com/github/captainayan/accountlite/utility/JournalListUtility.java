package com.github.captainayan.accountlite.utility;

import com.github.captainayan.accountlite.model.Journal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class JournalListUtility {
    public static ArrayList<Object> createMonthSeparatedListFromJournalList(ArrayList<Journal> journalList) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);

        String month = "";
        ArrayList<Object> list = new ArrayList<>();

        for (Journal j : journalList) {
            c.setTimeInMillis(j.getTimestamp());
            String currentMonth = monthFormat.format(c.getTime());

            if (!month.equals(currentMonth)) {
                month = currentMonth;
                list.add(month);
            }
            list.add(j);
        }

        return list;
    }
}
