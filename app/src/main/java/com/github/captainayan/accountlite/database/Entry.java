package com.github.captainayan.accountlite.database;

import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

@Entity
public class Entry {
    @PrimaryKey
    public int id;
    public int debit_id;
    public int credit_id;
    public int amount;
    public double timestamp;
    public String narration;
}

@Dao
interface EntryDao {
    @Query("SELECT * FROM entry")
    List<Entry> getAll();

    @Query("SELECT * FROM entry WHERE timestamp > :fromTime AND timestamp < :toTIme ")
    List<Entry> getByTime(double fromTime, double toTIme);

    @Insert
    void insertAll(Entry... entries);
    @Insert
    void insert(Entry entry);
}