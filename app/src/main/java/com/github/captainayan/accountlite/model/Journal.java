package com.github.captainayan.accountlite.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class Journal {

    @Embedded
    private Entry entry;

    @Relation(parentColumn = "debit_id", entityColumn = "id")
    private Ledger debitLedger;

    @Relation(parentColumn = "credit_id", entityColumn = "id")
    private Ledger creditLedger;

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public void setDebitLedger(Ledger debitLedger) {
        this.debitLedger = debitLedger;
    }

    public void setCreditLedger(Ledger creditLedger) {
        this.creditLedger = creditLedger;
    }

    public int getId() {
        return entry.getId();
    }

    public int getAmount() {
        return entry.getAmount();
    }

    public long getTimestamp() {
        return entry.getTimestamp();
    }

    public String getNarration() {
        return entry.getNarration();
    }

    public Ledger getDebitLedger() {
        return debitLedger;
    }

    public Ledger getCreditLedger() {
        return creditLedger;
    }
}
