package com.github.captainayan.accountlite.model;

import android.text.Editable;

import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

@Entity
public class Ledger {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int type;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Ledger(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    interface LedgerType {
        int REVENUE = 0;
        int EXPENSES = 1;
        int ASSET = 2;
        int LIABILITY = 3;
        int EQUITY = 4;
    }

}