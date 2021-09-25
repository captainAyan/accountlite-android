package com.github.captainayan.accountlite.utility;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Parser {

    public static final String TAG = "Parser";

    public static void parse(String input, ArrayList<Journal> journalList, HashMap<String, String> metaDataMap) {
        ArrayList<String> lines = new ArrayList<String>(Arrays.asList(input.split("\n")));

        for(int i=0; i<lines.size(); i++) {
            if (lines.get(i).charAt(0) == '#') { // meta
                ArrayList<String> x = new ArrayList<>(Arrays.asList(lines.get(i).split("=")));
                String name = x.get(0).substring(1);

                Log.d(TAG, "parse: " + lines.get(i));
                String content = lines.get(i).substring(name.length()+2);

                metaDataMap.put(name, content);
            }

            else { // journal
                ArrayList<String> e = new ArrayList<>(Arrays.asList(lines.get(i).split(",")));

                int id = Integer.parseInt(e.get(0));
                int amount = Integer.parseInt(e.get(1));
                int time = Integer.parseInt(e.get(2));
                String debit = e.get(3);
                String credit = e.get(4);

                /***
                 * This is getting the last element (narration) from the string. The code
                 * is little confusing, but it prevents the necessity of creating a parser
                 * to take care of string escaping
                 */
                int l = e.get(0).length()
                        +e.get(1).length()
                        +e.get(2).length()
                        +e.get(3).length()
                        +e.get(4).length() + 5;
                String narration = lines.get(i).substring(l, lines.get(i).length()-l);

                journalList.add(new Journal(id, amount, time, debit, credit, narration));
            }
        }
    }

    public static String stringify(ArrayList<Journal> journalList, HashMap<String, String> metaDataMap) {

        StringBuilder output = new StringBuilder();

        for (Map.Entry<String, String> p: metaDataMap.entrySet()) {
            String b = "#";
            b += p.getKey();
            b += "=";
            b += p.getValue();
            b += "\n";
            output.append(b);
        }

        for (Journal j: journalList) {
            output.append(j.stringify());
            output.append("\n");
        }

        return String.valueOf(output);
    }

    public static class Journal {

        private int _id, _amount, _time;
        private String _debit, _credit, _narration;

        public Journal(int id, int amount, int time, String debit, String credit, String narration) {
            this._id = id;
            this._amount = amount;
            this._time = time;
            this._debit = debit;
            this._credit = credit;
            this._narration = narration;
        }

        public int get_id() {
            return _id;
        }

        public int get_amount() {
            return _amount;
        }

        public int get_time() {
            return _time;
        }

        public String get_debit() {
            return _debit;
        }

        public String get_credit() {
            return _credit;
        }

        public String get_narration() {
            return _narration;
        }

        public String stringify() {
            return _id+","+_amount+","+_time+","+_debit+","+_credit+","+_narration;
        }

    }
}
