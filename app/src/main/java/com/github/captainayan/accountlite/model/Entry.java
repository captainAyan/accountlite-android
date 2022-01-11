package com.github.captainayan.accountlite.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Entry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int debit_id;
    private int credit_id;
    private int amount;
    private long timestamp;
    private String narration;

    public void setId(int id) {
        this.id = id;
    }

    public void setDebit_id(int debit_id) {
        this.debit_id = debit_id;
    }

    public void setCredit_id(int credit_id) {
        this.credit_id = credit_id;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public int getId() {
        return id;
    }

    public int getDebit_id() {
        return debit_id;
    }

    public int getCredit_id() {
        return credit_id;
    }

    public int getAmount() {
        return amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getNarration() {
        return narration;
    }

    public Entry(int debit_id, int credit_id, int amount, long timestamp, String narration) {
        this.debit_id = debit_id;
        this.credit_id = credit_id;
        this.amount = amount;
        this.timestamp = timestamp;
        this.narration = narration;
    }
}

