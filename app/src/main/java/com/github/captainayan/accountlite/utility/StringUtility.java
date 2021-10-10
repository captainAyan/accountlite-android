package com.github.captainayan.accountlite.utility;

import java.text.SimpleDateFormat;

public class StringUtility {

    public static String dateFormat(double timestamp, String dateFormat, String dateSeparator) {
        if(dateFormat.equals("DMY"))
            return new SimpleDateFormat("dd"+dateSeparator+"MM"+dateSeparator+"yyyy").format(timestamp);
        else if(dateFormat.equals("MDY"))
            return new SimpleDateFormat("MM"+dateSeparator+"dd"+dateSeparator+"yyyy").format(timestamp);
        else if(dateFormat.equals("YMD"))
            return new SimpleDateFormat("yyyy"+dateSeparator+"MM"+dateSeparator+"dd").format(timestamp);
        else return "";
    }

    public static String accountNameFormat(String accountName) {
        return capitalizeFirstLetters(accountName) + " A/c";
    }

    public static String capitalizeFirstLetters(String str) {
        String words[]=str.split("\\s");
        StringBuilder capitalizeWord= new StringBuilder();
        for(String w:words){
            String first=w.substring(0,1);
            String afterfirst=w.substring(1);
            capitalizeWord.append(first.toUpperCase()).append(afterfirst).append(" ");
        }
        return capitalizeWord.toString().trim();
    }

    public static String amountFormat(int amount, String format, String symbol, String symbolPosition) {
        boolean isNegative = (amount < 0);
        if (amount<0) amount *= -1;

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

        if(symbolPosition.equals("START")) result = symbol + result;
        else result = result + symbol;

        if(isNegative) return "(" + result + ")";
        else return result;
    }

    public static String narrationFormat(String narration) {
        return "(" + Character.toUpperCase(narration.charAt(0)) + narration.substring(1)  + ")";
    }

    public static String idFormat(int id) {
        return "#"+id;
    }

}
