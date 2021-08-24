package com.rider.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.rider.ui.MyApplication;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ubuntu on 18/11/16.
 */

public class DaoManager extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String TAG = "DaoManager";

    private static final String DATABASE_NAME = "rider.db";

    private static final SQLiteException DOWNGRADE_EXCEPTION = new SQLiteException(
            "Database file was deleted");

    private final static DaoManager instance;

    static {
        instance = new DaoManager();
    }

    private final ArrayList<BaseDaoTable> allDaoTables;

    public DaoManager () {
        super(MyApplication.getInstance(), DATABASE_NAME, null, DATABASE_VERSION);
        allDaoTables = new ArrayList<>();
    }

    public static DaoManager getInstance () {
        return instance;
    }


    public void addTable (BaseDaoTable table) {
        DaoLogger.printLog(TAG + " addTable=" + table.getTableName());
        allDaoTables.add(table);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        DaoLogger.printLog(TAG + " onCreate");
        for (BaseDaoTable table : allDaoTables)
            table.create(db);
    }

    @Override
    public void onDowngrade (SQLiteDatabase db, int oldVersion, int newVersion) {
//        super.onDowngrade(db, oldVersion, newVersion);
        DaoLogger.printLog(TAG + " Downgrading database from version "
                + oldVersion + " to " + newVersion);
        File file = new File(db.getPath());
        file.delete();
        DaoLogger.printLog(TAG + " Database file was deleted");
        throw DOWNGRADE_EXCEPTION;
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion > newVersion) {
            DaoLogger.printLog(TAG + " Downgrading database from version "
                    + oldVersion + " to " + newVersion);
            File file = new File(db.getPath());
            file.delete();
            DaoLogger.printLog(TAG + " Database file was deleted");
            throw DOWNGRADE_EXCEPTION;
        } else {

            while (oldVersion < newVersion) {
                oldVersion++;
                DaoLogger.printLog(TAG + " Upgrading database from version " + (oldVersion - 1)
                        + " to " + oldVersion);
                migrate(db, oldVersion);
                for (BaseDaoTable table : allDaoTables)
                    table.migrate(db, oldVersion);
            }
        }
    }

    private void migrate (SQLiteDatabase db, int toVersion) {
        DaoLogger.printLog(TAG + " migrate toVersion=" + toVersion);
        switch (toVersion) {
            default:
                break;
        }
    }

    public void loadDao () {
        try {
            DaoLogger.printLog(TAG + " onLoad getWritableDatabase");
            getWritableDatabase(); // Force onCreate or onUpgrade
        } catch (SQLiteException e) {
            DaoLogger.printLog(TAG + " onLoad SQLiteException=" + e.getMessage());
            if (e == DOWNGRADE_EXCEPTION) {
                loadDao();
            } else {
                throw e;
            }
        }
    }

    public void clearDao () {
        DaoLogger.printLog(TAG + " onClear");
        for (BaseDaoTable table : allDaoTables)
            table.clear();
    }
}
