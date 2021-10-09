package com.github.captainayan.accountlite.model;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

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

    @Ignore
    public Ledger(int id, String name, int type) {
        this.id = id;
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

    public interface Type {
        int REVENUE = 0;
        int EXPENDITURE = 1;
        int ASSET = 2;
        int LIABILITY = 3;
        int EQUITY = 4;
    }

    public static class LedgerWithBalance {
        private int id;
        private String name;
        private int type;
        private int balance;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getType() {
            return type;
        }

        public int getBalance() {
            return balance;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }
    }

}