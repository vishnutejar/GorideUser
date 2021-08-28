package com.quickzetuser.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.quickzetuser.database.BaseDaoTable;
import com.quickzetuser.database.DaoLogger;
import com.quickzetuser.database.DaoManager;
import com.quickzetuser.model.BookCabModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ubuntu on 23/1/17.
 */

public class  BookingTable extends BaseDaoTable {

    public static final class Fields implements BaseColumns {
        public static final String BOOKING_ID = "user_id";
        public static final String BOOKING_STATUS = "status";
        public static final String BOOKING_DATA = "item_data";
    }


    public static final String TAG = "BookingTable";

    private static final String NAME = "booking_table";


    private static final String[] PROJECTION = new String[]{
            Fields._ID, Fields.BOOKING_ID, Fields.BOOKING_DATA, Fields.BOOKING_STATUS};
    private final static BookingTable instance;

    static {
        instance = new BookingTable(DaoManager.getInstance());
        DaoManager.getInstance().addTable(instance);
    }

    private final DaoManager databaseManager;

    private BookingTable(DaoManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public static BookingTable getInstance() {
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
                + Fields.BOOKING_ID + " INTEGER NOT NULL,"
                + Fields.BOOKING_STATUS + " INTEGER NOT NULL,"
                + Fields.BOOKING_DATA + " TEXT NOT NULL"
                + ");";
    }

    public int getBookingId(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(Fields.BOOKING_ID));
    }

    public int getBookingStatus(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(Fields.BOOKING_STATUS));
    }

    public String getBookingData(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(Fields.BOOKING_DATA));
    }

    public void addOrUpdateBooking(BookCabModel bookCabModel) {
        if (checkBookingExits(bookCabModel.getBooking_id())) {
            updateBooking(bookCabModel);
            return;
        }
        ContentValues values = new ContentValues();
        values.put(Fields.BOOKING_ID, bookCabModel.getBooking_id());
        values.put(Fields.BOOKING_STATUS, bookCabModel.getStatus());

        values.put(Fields.BOOKING_DATA, new Gson().toJson(bookCabModel));

        getDatabase(false).insert(NAME, null, values);
        DaoLogger.printLog(TAG, "addOrUpdateBooking getBooking_id=" + bookCabModel.getBooking_id());
    }

    private void updateBooking(BookCabModel bookCabModel) {

        ContentValues values = new ContentValues();
        values.put(Fields.BOOKING_DATA, new Gson().toJson(bookCabModel));
        values.put(Fields.BOOKING_STATUS, bookCabModel.getStatus());

        getDatabase(false).update(NAME, values, Fields.BOOKING_ID + " = ?",
                new String[]{String.valueOf(bookCabModel.getBooking_id())});
        DaoLogger.printLog(TAG, " updateBooking getBooking_id=" + bookCabModel.getBooking_id());
    }

    public void deleteBooking(long booking_id) {
        getDatabase(false).delete(NAME, Fields.BOOKING_ID + " = ?",
                new String[]{String.valueOf(booking_id)});

        DaoLogger.printLog(TAG, " deleteBooking booking_id=" + booking_id);
    }

    public void deleteUnUsedBookings(String booking_ids) {
        getDatabase(false).delete(NAME,
                Fields.BOOKING_STATUS + " !=? AND "
                        + Fields.BOOKING_ID + " NOT IN (" + booking_ids + ")",
                new String[]{String.valueOf(5)});

        DaoLogger.printLog(TAG, " deleteUnUsedBookings booking_ids=" + booking_ids);
    }

    public int getRunningBookingCount() {
        String query = "SELECT * FROM " + NAME +
                " WHERE " + Fields.BOOKING_STATUS + " <= 5";
        Cursor cursor = listFromQuery(query);
        if (cursor == null) return 0;
        int count = cursor.getCount();

        cursor.close();
        return count;
    }

    public List<BookCabModel> getRunningBooking() {
        List<BookCabModel> list = new ArrayList<>();
        String query = "SELECT * FROM " + NAME +
                " WHERE " + Fields.BOOKING_STATUS + " <= 5";
        Cursor cursor = listFromQuery(query);
        if (cursor == null) return list;

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                list.add(parseCursor(cursor));
            }
        }
        cursor.close();
        return list;
    }


    public BookCabModel getRunningBookingFromId(long booking_id) {
        BookCabModel bookCabModel = null;
        String query = "SELECT * FROM " + NAME +
                " WHERE " + Fields.BOOKING_ID + "=" + booking_id;
        Cursor cursor = listFromQuery(query);
        if (cursor == null) return bookCabModel;
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                bookCabModel = parseCursor(cursor);
            }
        }
        cursor.close();
        return bookCabModel;
    }


    public boolean checkBookingExits(long booking_id) {
        String query = "SELECT " + Fields._ID + " FROM " + NAME +
                " WHERE " + Fields.BOOKING_ID + "=" + booking_id;
        Cursor cursor = listFromQuery(query);
        boolean exists = false;

        if (cursor == null) return exists;
        if (cursor.getCount() > 0) {
            exists = true;
        }
        cursor.close();
        return exists;
    }

    public BookCabModel parseCursor(Cursor cursor) {
        BookCabModel bookCabModel = null;
        try {
            bookCabModel = new Gson().fromJson(getBookingData(cursor), BookCabModel.class);
        } catch (JsonSyntaxException e) {
        }
        return bookCabModel;
    }


}
