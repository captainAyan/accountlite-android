package com.github.captainayan.accountlite.utility;

import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;

public class StringUtility {

    public static String dateFormat(double timestamp) {
        return new SimpleDateFormat("dd/MM/yyyy").format(timestamp);
    }

    public static String accountNameFormat(String accountName) {
        return Character.toUpperCase(accountName.charAt(0)) + accountName.substring(1) + " A/c";
    }

    public static String amountFormat(int amount, String format, String currency) {
        StringBuilder a = new StringBuilder(String.valueOf(amount));
        String result = "";

        if(format.equals("IND")) {
            // do the Indian 🇮🇳 formatting
            if(a.length() > 3) a.insert(a.length()-3, ',');
            for(int i=a.length()-6; i>0; i-=2) {
                a.insert(i, ',');
            }
            result = a.toString();
        }
        else if(format.equals("INT")) {
            // do the international formatting
            for(int i=a.length()-3; i>0; i-=3) {
                a.insert(i, ',');
            }
            result = a.toString();
        }
        else result = a.toString();

        return currency + " " + result;
    }

    public static String narrationFormat(String narration) {
        return "(" + narration + ")";
    }

    public static String idFormat(int id) {
        return "#"+id;
    }

}
