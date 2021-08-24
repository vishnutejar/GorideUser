package com.rider.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rider.database.BaseDaoTable;
import com.rider.database.DaoLogger;
import com.rider.database.DaoManager;
import com.rider.model.FavouriteModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ubuntu on 23/1/17.
 */

public class FavoriteTable extends BaseDaoTable {

    public static final String TAG = "FavoriteTable";
    private static final String NAME = "favorite_table";
    private static final String[] PROJECTION = new String[]{
            Fields._ID, Fields.FAVORITE_ID, Fields.FAVORITE_TYPE, Fields.FAVORITE_DATA};
    private final static FavoriteTable instance;

    static {
        instance = new FavoriteTable(DaoManager.getInstance());
        DaoManager.getInstance().addTable(instance);
    }

    private final DaoManager databaseManager;

    private FavoriteTable(DaoManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public static FavoriteTable getInstance() {
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
                + Fields.FAVORITE_TYPE + " TEXT NOT NULL,"
                + Fields.FAVORITE_DATA + " TEXT NOT NULL"
                + ");";
    }

    public int getFavoriteType(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(Fields.FAVORITE_TYPE));
    }

    public String getFavoriteData(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(Fields.FAVORITE_DATA));
    }

    public void addFavorite(FavouriteModel locationModel) {
        if (!checkFavoriteExits(locationModel.getType())) {
            ContentValues values = new ContentValues();
            values.put(Fields.FAVORITE_TYPE, locationModel.getType());
            values.put(Fields.FAVORITE_DATA, new Gson().toJson(locationModel));
            getDatabase(false).insert(NAME, null, values);
        }

    }


    public void deleteFavorite(String type) {
        getDatabase(false).delete(NAME, Fields.FAVORITE_TYPE + " = ?",
                new String[]{type});
    }

    /**
     * SELECT * FROM playlists ORDER BY Name=Classical DESC, Name=Grunge DESC
     *
     * @return
     */
    public List<FavouriteModel> getAllFavorite() {
        List<FavouriteModel> list = new ArrayList<>();
        String query = "SELECT * FROM " + NAME + " ORDER BY " + Fields.FAVORITE_TYPE + "='Home' DESC, "
                + Fields.FAVORITE_TYPE + "='Work' DESC";
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


    public boolean checkFavoriteExits(String type) {
        String query = "SELECT * FROM " + NAME +
                " WHERE " + Fields.FAVORITE_TYPE + "='" + type + "'";
        Cursor cursor = listFromQuery(query);
        boolean exists = false;

        if (cursor == null) return exists;
        if (cursor.getCount() > 0) {
            exists = true;
        }
        cursor.close();
        return exists;
    }

    public FavouriteModel parseCursor(Cursor cursor) {
        FavouriteModel locationModel = null;
        try {
            locationModel = new Gson().fromJson(getFavoriteData(cursor), FavouriteModel.class);
        } catch (JsonSyntaxException e) {
        }
        return locationModel;
    }

    public static final class Fields implements BaseColumns {
        public static final String FAVORITE_ID = "favorite_id";
        public static final String FAVORITE_TYPE = "favorite_type";
        public static final String FAVORITE_DATA = "item_data";
    }


}
