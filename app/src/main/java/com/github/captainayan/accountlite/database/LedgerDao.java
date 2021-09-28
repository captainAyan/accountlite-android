package com.github.captainayan.accountlite.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.github.captainayan.accountlite.model.Ledger;

import java.util.List;

@Dao
public interface LedgerDao {
    @Query("SELECT * FROM ledger")
    List<Ledger> getAll();

    @Query("SELECT * FROM ledger WHERE id == :id")
    Ledger getLedgerById(int id);

    @Insert
    void insert(Ledger ledger);

    @Query("DELETE FROM ledger")
    void deleteAll();
}
