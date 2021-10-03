package com.github.captainayan.accountlite.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.github.captainayan.accountlite.model.Entry;
import com.github.captainayan.accountlite.model.Ledger;

@Database(entities = {Entry.class, Ledger.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract EntryDao entryDao();
    public abstract LedgerDao ledgerDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "AppDatabase")
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public void delete(Context context) {
        context.deleteDatabase("AppDatabase");
    }
}
