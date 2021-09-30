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

    // @Query("SELECT SUM(amount) FROM entry WHERE (:ledgerId = debit_id OR :ledgerId = credit_id) AND timestamp <= :toDate")
    @Query("SELECT debit-credit FROM " +
            "(SELECT COALESCE(SUM(amount), 0) AS debit FROM entry WHERE :ledgerId = debit_id AND timestamp <= :toDate) AS A, " +
            "(SELECT COALESCE(SUM(amount), 0) AS credit FROM entry WHERE :ledgerId = credit_id AND timestamp <= :toDate) AS B")
    int getLedgerBalance(int ledgerId, double toDate);

    @Insert
    void insert(Ledger ledger);

    @Query("DELETE FROM ledger")
    void deleteAll();
}
