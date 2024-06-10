package com.github.captainayan.accountlite.utility.statement;

import com.github.captainayan.accountlite.model.Journal;

import java.util.ArrayList;

public class JournalEntriesCSVStatement {
    private ArrayList<Journal> journalList;

    public JournalEntriesCSVStatement() {}

    public JournalEntriesCSVStatement setJournalList(ArrayList<Journal> journalList) {
        this.journalList = journalList;
        return this;
    }

    public String build() {
        StringBuilder csv = new StringBuilder()
                .append("ID,Debit,Credit,Amount,Timestamp,Narration\n");

        for (Journal j: journalList) {
            csv.append(j.getId()).append(",")
                    .append(j.getDebitLedger().getName()).append(",")
                    .append(j.getCreditLedger().getName()).append(",")
                    .append(j.getAmount()).append(",")
                    .append(j.getTimestamp()).append(",")
                    .append(j.getNarration()).append("\n");
        }
        return csv.toString();
    }
}
