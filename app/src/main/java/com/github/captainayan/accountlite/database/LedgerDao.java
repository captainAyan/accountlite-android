package com.github.captainayan.accountlite.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;

import com.github.captainayan.accountlite.model.Ledger;

import java.util.List;

@Dao
public interface LedgerDao {
    @Query("SELECT * FROM ledger")
    List<Ledger> getAll();

    @Query("SELECT name FROM ledger")
    List<String> getAllNames();

    @Query("SELECT * FROM ledger WHERE id == :id")
    Ledger getLedgerById(int id);

    @Query("SELECT debit-credit FROM " +
            "(SELECT COALESCE(SUM(amount), 0) AS debit FROM entry WHERE :ledgerId = debit_id AND timestamp <= :toDate) AS A, " +
            "(SELECT COALESCE(SUM(amount), 0) AS credit FROM entry WHERE :ledgerId = credit_id AND timestamp <= :toDate) AS B")
    int getLedgerBalance(int ledgerId, double toDate);

    @Query("SELECT id, name, type, (SELECT debit-credit FROM " +
            "(SELECT COALESCE(SUM(amount), 0) AS debit FROM entry WHERE ledger.id = debit_id AND timestamp <= :toDate) AS A," +
            "(SELECT COALESCE(SUM(amount), 0) AS credit FROM entry WHERE ledger.id = credit_id AND timestamp <= :toDate) AS B" +
            ") AS balance FROM ledger")
    List<Ledger.LedgerWithBalance> getLedgersWithBalance(double toDate);

    @Insert
    void insert(Ledger ledger);

    @Update
    void update(Ledger ledger);

}
