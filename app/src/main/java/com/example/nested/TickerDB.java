package com.example.nested;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TickerDB extends SQLiteOpenHelper {

    private static int DB_VERSION = 1;
    private static String DATABASE_NAME = "TickersDB.db";
    private static String TABLE_NAME = "favouriteTable";
    public static String ID = "id";
    public static String COLUMN_PIC = "pic";
    public static String COLUMN_TICKERNAME = "tickername";
    public static String COLUMN_COMPANYNAME = "companyname";
    public static String COLUMN_PRICE = "price";
    public static String COLUMN_FAVOURITE = "favourite";
    public static String COLUMN_DELTAPRICE = "deltaprice";
    public static String COLUMN_UTILITY = "utility";

    private static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + ID + " TEXT," + COLUMN_PIC + " TEXT,"
            + COLUMN_TICKERNAME + " TEXT," + COLUMN_COMPANYNAME + " TEXT," + COLUMN_PRICE + " TEXT,"
            + COLUMN_DELTAPRICE + " TEXT, " + COLUMN_FAVOURITE + " TEXT DEFAULT 0," + COLUMN_UTILITY + " TEXT)";


    public TickerDB(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public void insertEmpty() {

    }


    public void insertIntoTheDatabase(String id, String pic, String tickerName, String companyName, String price, String deltaPrice, String favourite, String currency) {
        SQLiteDatabase db;
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TickerDB.ID, id);
        cv.put(TickerDB.COLUMN_PIC, pic);
        cv.put(TickerDB.COLUMN_TICKERNAME, tickerName);
        cv.put(TickerDB.COLUMN_COMPANYNAME, companyName);
        cv.put(TickerDB.COLUMN_PRICE, price);
        cv.put(TickerDB.COLUMN_DELTAPRICE, deltaPrice);
        cv.put(TickerDB.COLUMN_FAVOURITE, favourite);
        db.insert(TickerDB.TABLE_NAME, null, cv);
        // Log.d("TickerDB Status", id+" " + tickerName + ", favstatus - "+favourite+" - . " + cv);
    }


    public Cursor read_all_data(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + TickerDB.TABLE_NAME + " where " + TickerDB.ID + "=" + id + "";
        return db.rawQuery(sql, null, null);
    }


    public void remove_fav(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE " + TickerDB.TABLE_NAME + " SET  " + TickerDB.COLUMN_FAVOURITE + " ='0' WHERE " + TickerDB.ID + "=" + id + "";
        db.execSQL(sql);
        //  Log.d("remove", id.toString());

    }


    public Cursor select_all_favorite_list() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TickerDB.TABLE_NAME + " WHERE " + TickerDB.COLUMN_FAVOURITE + " ='1'";
        return db.rawQuery(sql, null, null);
    }
}
