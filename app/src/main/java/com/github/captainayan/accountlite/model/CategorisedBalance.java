package com.github.captainayan.accountlite.model;

public class CategorisedBalance {

    private int type, balance;

    public CategorisedBalance(int type, int balance) {
        this.type = type;
        this.balance = balance;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getType() {
        return type;
    }

    public int getBalance() {
        return balance;
    }
}
