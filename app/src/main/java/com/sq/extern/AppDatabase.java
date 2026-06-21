package com.sq.extern;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Quest.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract QuestDao questDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "sidequest_db")
                    .fallbackToDestructiveMigration() // Bei Schema-Änderung DB löschen und neu aufbauen
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}