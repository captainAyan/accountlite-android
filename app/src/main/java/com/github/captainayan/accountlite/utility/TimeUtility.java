package com.github.captainayan.accountlite.utility;

import android.content.Intent;
import java.util.Calendar;

public class TimeUtility {
    public static class ToAndFromDate{
        public long toDateTimestamp;
        public long fromDateTimestamp;
    }

    public static ToAndFromDate getToAndFromDateTimestampFromIntent(Intent i) {
        ToAndFromDate toAndFromDate = new ToAndFromDate();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, i.getIntExtra("day", 0));
        c.set(Calendar.MONTH, i.getIntExtra("month", 0));
        c.set(Calendar.YEAR, i.getIntExtra("year", 0));

        // end of the day time
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);

        // extract the toDate
        toAndFromDate.toDateTimestamp = c.getTimeInMillis();

        // beginning of the day time
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        switch (i.getStringExtra("duration")) {
            case "week":
                c.add(Calendar.WEEK_OF_MONTH, -1);
                break;
            case "fortnite":
                c.add(Calendar.WEEK_OF_MONTH, -2);
                break;
            case "month":
                c.add(Calendar.MONTH, -1);
                break;
            case "quarter":
                c.add(Calendar.MONTH, -3);
                break;
            case "half_year":
                c.add(Calendar.MONTH, -6);
                break;
            case "year":
                c.add(Calendar.YEAR, -1);
                break;
            case "all":
                c.set(1970, Calendar.JANUARY, 1);
                break;
            default: // 1 day
                c.add(Calendar.DAY_OF_MONTH, -1);
                break;
        }

        // extract the fromDate
        toAndFromDate.fromDateTimestamp = c.getTimeInMillis();

        return toAndFromDate;
    }
}
