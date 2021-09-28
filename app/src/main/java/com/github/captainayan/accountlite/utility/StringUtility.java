package com.github.captainayan.accountlite.utility;

import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;

public class StringUtility {

    public static String dateFormat(double timestamp) {
        return new SimpleDateFormat("dd/MM/yyyy").format(timestamp);
    }

    public static String accountNameFormat(String accountName) {
        return capitalizeFirstLetters(accountName) + " A/c";
    }

    public static String capitalizeFirstLetters(String str) {
        String words[]=str.split("\\s");
        String capitalizeWord="";
        for(String w:words){
            String first=w.substring(0,1);
            String afterfirst=w.substring(1);
            capitalizeWord+=first.toUpperCase()+afterfirst+" ";
        }
        return capitalizeWord.trim();
    }

    public static String amountFormat(int amount, String format, String symbol, String symbolPosition) {
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

        if(symbolPosition.equals("START")) return symbol + result;
        else return result + symbol;
    }

    public static String narrationFormat(String narration) {
        return "(" + Character.toUpperCase(narration.charAt(0)) + narration.substring(1)  + ")";
    }

    public static String idFormat(int id) {
        return "#"+id;
    }

}
