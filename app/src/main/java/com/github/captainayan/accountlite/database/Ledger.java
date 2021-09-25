package com.github.captainayan.accountlite.database;

import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

@Entity
public class Ledger {
    @PrimaryKey
    public int id;
    public String name;
    public int type;

    public String getLabel() {
        return name + " #" + id;
    }
}

@Dao
interface LedgerDao {
    @Query("SELECT * FROM ledger")
    List<Ledger> getAll();

    @Insert
    void insert(Ledger ledger);
}