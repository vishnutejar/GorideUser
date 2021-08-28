package com.quickzetuser.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.addressfetching.LocationModelFull;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.quickzetuser.database.BaseDaoTable;
import com.quickzetuser.database.DaoLogger;
import com.quickzetuser.database.DaoManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ubuntu on 23/1/17.
 */

public class  RecentTable extends BaseDaoTable {

    public static final String TAG = "RecentTable";
    private static final String NAME = "recent_table";
    private static final String[] PROJECTION = new String[]{
            Fields._ID, Fields.ADDRESS, Fields.RECENT_DATA};
    private final static RecentTable instance;

    static {
        instance = new RecentTable(DaoManager.getInstance());
        DaoManager.getInstance().addTable(instance);
    }

    private final DaoManager databaseManager;

    private RecentTable(DaoManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public static RecentTable getInstance() {
        return instance;
    }

    @Override
    protected String getTableName() {
        return NAME;
    }

    @Override
    protected String[] getProjection() {
        return PROJECTION;
    }

    @Override
    public void create(SQLiteDatabase db) {
        DaoLogger.printLog(TAG + " create table");
        executeSql(db, generateCreateSql());
    }

    @Override
    public void migrate(SQLiteDatabase db, int toVersion) {
        DaoLogger.printLog(TAG + " migrate toVersion=" + toVersion);
        String sql = "";
        switch (toVersion) {

        }
    }

    @Override
    public SQLiteDatabase getDatabase(boolean isReadable) {
        if (isReadable)
            return databaseManager.getReadableDatabase();

        return databaseManager.getWritableDatabase();
    }

    private String generateCreateSql() {
        return "CREATE TABLE " + NAME + " ("
                + Fields._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + Fields.ADDRESS + " TEXT NOT NULL,"
                + Fields.RECENT_DATA + " TEXT NOT NULL"
                + ");";
    }

    public String getRecentAddress(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(Fields.ADDRESS));
    }

    public String getRecentData(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(Fields.RECENT_DATA));
    }

    public void addRecent(LocationModelFull.LocationModel locationModel) {
        if (!checkRecentExits(locationModel.getFulladdress())) {
            ContentValues values = new ContentValues();
            values.put(Fields.ADDRESS, locationModel.getFulladdress());
            values.put(Fields.RECENT_DATA, new Gson().toJson(locationModel));
            getDatabase(false).insert(NAME, null, values);
            removeOldestRecent();
        } else {
            String where =  Fields.ADDRESS + "='"+locationModel.getFulladdress()+"'";
            ContentValues values = new ContentValues();
            values.put(Fields.RECENT_DATA, new Gson().toJson(locationModel));
            getDatabase(false).update(NAME, values, where, null);
        }

    }

    public void removeOldestRecent() {
        String whereRowId = "(Select (CASE " +
                "WHEN (Select count() as counts from " + NAME + ")>10 " +
                "THEN (Select " + Fields._ID + " from " + NAME + " limit 1) " +
                "ELSE 0 END))";
        String whereClause = Fields._ID + "=" + whereRowId;
        int affectedRow = getDatabase(false).delete(NAME, whereClause, null);
    }

    public List<LocationModelFull.LocationModel> getAllRecent() {
        List<LocationModelFull.LocationModel> list = new ArrayList<>();
        String query = "SELECT * FROM " + NAME;
        Cursor cursor = listFromQuery(query);
        if (cursor == null) return null;
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                list.add(parseCursor(cursor));
            }
        }
        cursor.close();
        return list;
    }


    public boolean checkRecentExits(String address) {
        String query = "SELECT " + Fields._ID + " FROM " + NAME +
                " WHERE " + Fields.ADDRESS + "='" + address + "'";
        Cursor cursor = listFromQuery(query);
        boolean exists = false;

        if (cursor == null) return exists;
        if (cursor.getCount() > 0) {
            exists = true;
        }
        cursor.close();
        return exists;
    }

    public LocationModelFull.LocationModel parseCursor(Cursor cursor) {
        LocationModelFull.LocationModel locationModel = null;
        try {
            locationModel = new Gson().fromJson(getRecentData(cursor), LocationModelFull.LocationModel.class);
        } catch (JsonSyntaxException e) {
            DaoLogger.printLog(TAG, " JsonSyntaxException " + e);
        }
        return locationModel;
    }

    public static final class Fields implements BaseColumns {
        public static final String ADDRESS = "address";
        public static final String RECENT_DATA = "item_data";
    }


}
