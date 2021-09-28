package com.github.captainayan.accountlite.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.github.captainayan.accountlite.model.Entry;
import com.github.captainayan.accountlite.model.Journal;

import java.util.List;

@Dao
public interface EntryDao {
    @Query("SELECT * FROM entry")
    List<Entry> getAll();

    @Insert
    void insert(Entry entry);

    @Query("DELETE FROM entry")
    void deleteAll();

    @Transaction
    @Query("SELECT * FROM entry ORDER BY id DESC")
    public List<Journal> getJournals();

    @Transaction
    @Query("SELECT * FROM entry WHERE timestamp >= :fromDate AND timestamp <= :toDate ORDER BY id DESC")
    public List<Journal> getJournals(double fromDate, double toDate);

    @Transaction
    @Query("SELECT * FROM entry WHERE (:ledgerId = debit_id OR :ledgerId = credit_id) AND (timestamp >= :fromDate AND timestamp <= :toDate) ORDER BY id DESC")
    public List<Journal> getJournalsByLedger(int ledgerId, double fromDate, double toDate);
}
