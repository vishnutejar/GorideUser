package com.quickzetuser.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;


/**
 * Created by ubuntu on 18/11/16.
 */

public abstract class  BaseDaoTable {

    public static final String TAG = "BaseDaoTable";

    protected abstract String getTableName ();

    protected abstract String[] getProjection ();

    protected String getListOrder () {
        return null;
    }


    public abstract void create (SQLiteDatabase db);

    public abstract void migrate (SQLiteDatabase db, int toVersion);

    public abstract SQLiteDatabase getDatabase (boolean isReadable);


    public Cursor listFromQuery (@NonNull String query) {
        DaoLogger.printLog(TAG + " " + getTableName() + " query = " + query);

        SQLiteDatabase db = getDatabase(true);
        return db.rawQuery(query, null);
    }

    public void executeSql (@NonNull String sqlQuery) {
        SQLiteDatabase db = getDatabase(false);
        executeSql(db, sqlQuery);
    }

    public void executeSql (@NonNull SQLiteDatabase db, @NonNull String sqlQuery) {
        DaoLogger.printLog(TAG + " " + getTableName() + " sqlQuery = " + sqlQuery);
        db.execSQL(sqlQuery);
    }


    public Cursor list () {
        SQLiteDatabase db = getDatabase(true);
        return db.query(getTableName(), getProjection(), null, null, null,
                null, getListOrder());
    }


    public void clear () {
        SQLiteDatabase db = getDatabase(false);
        DaoLogger.printLog(TAG + " " + getTableName() + " clear");
        db.delete(getTableName(), null, null);
    }

    public void dropTable () {
        executeSql("DROP TABLE IF EXISTS " + getTableName() + ";");
    }

    public void renameTable (String oldName) {
        executeSql("ALTER TABLE " + oldName + " RENAME TO " + getTableName() + ";");
    }
}
