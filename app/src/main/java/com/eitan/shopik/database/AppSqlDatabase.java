package com.eitan.shopik.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.eitan.shopik.database.daos.CompanyDao;
import com.eitan.shopik.database.models.Company;
import com.eitan.shopik.database.daos.ItemDao;
import com.eitan.shopik.database.daos.UserDao;
import com.eitan.shopik.database.models.User;
import com.eitan.shopik.database.models.ShoppingItem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, ShoppingItem.class, Company.class}, version = 7)
@TypeConverters({Converters.class})
public abstract class AppSqlDatabase extends RoomDatabase {

    private static volatile AppSqlDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppSqlDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppSqlDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppSqlDatabase.class, "word_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract UserDao userDao();
    public abstract ItemDao ItemDao();
    public abstract CompanyDao CompanyDao();

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {}
}