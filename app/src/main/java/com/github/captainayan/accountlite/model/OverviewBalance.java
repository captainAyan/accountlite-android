package com.github.captainayan.accountlite.model;

public class OverviewBalance {

    private int type, balance;

    public OverviewBalance(int type, int balance) {
        this.type = type;
        this.balance = balance;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void addBalance(int additionBalance) {
        this.balance += additionBalance;
    }

    public int getType() {
        return type;
    }

    public int getBalance() {
        return balance;
    }
}
