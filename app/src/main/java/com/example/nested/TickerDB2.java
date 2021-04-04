package com.example.nested;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

public class TickerDB2 extends SQLiteOpenHelper {

    private static int DB_VERSION = 1;
    private static String DATABASE_NAME = "TickersDB2.db";
    private static String TABLE_NAME = "favouriteTable";
    public static String ID = "id";
    public static String COLUMN_PIC = "pic";
    public static String COLUMN_TICKERNAME = "tickername";
    public static String COLUMN_COMPANYNAME = "companyname";
    public static String COLUMN_PRICE = "price";
    public static String COLUMN_FAVOURITE = "favourite";
    public static String COLUMN_DELTAPRICE = "deltaprice";
    public static String COLUMN_OLDPRICE = "oldprice";
    public static String COLUMN_CURRENCY = "utility";

    private static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + ID + " TEXT," + COLUMN_PIC + " TEXT,"
            + COLUMN_TICKERNAME  + " TEXT UNIQUE PRIMARY KEY, " + COLUMN_COMPANYNAME + " TEXT," + COLUMN_PRICE + " TEXT,"
            + COLUMN_DELTAPRICE + " TEXT, " + COLUMN_FAVOURITE + " TEXT DEFAULT 0," + COLUMN_OLDPRICE + " TEXT, " +
            COLUMN_CURRENCY + " TEXT)";


    public TickerDB2(Context context) {
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

    public void insertIntoTheDatabase(String id, String pic, String tickerName, String companyName, String price, String deltaPrice, String favourite,String oldprice, String currency) {
        SQLiteDatabase db;
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TickerDB2.ID, id);
        cv.put(TickerDB2.COLUMN_PIC, pic);
        cv.put(TickerDB2.COLUMN_TICKERNAME, tickerName);
        cv.put(TickerDB2.COLUMN_COMPANYNAME, companyName);
        cv.put(TickerDB2.COLUMN_PRICE, price);
        cv.put(TickerDB2.COLUMN_DELTAPRICE, deltaPrice);
        cv.put(TickerDB2.COLUMN_FAVOURITE, favourite);
        cv.put(TickerDB2.COLUMN_OLDPRICE, oldprice);
        cv.put(TickerDB2.COLUMN_CURRENCY, currency);
        db.replace(TickerDB2.TABLE_NAME, null, cv);
        //  Log.d("TickerDB2 Status", id+" " + tickerName + ", favstatus - "+favourite+" - . " + cv);
    }

    public Cursor read_row_data(String tickername) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + TickerDB2.TABLE_NAME + " where " + " tickername " + "  = " + " '" +tickername+ "' ";
        return db.rawQuery(sql, null, null);
    }

    public void become_fav(String tickername) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE " + TickerDB2.TABLE_NAME + " SET  " + TickerDB2.COLUMN_FAVOURITE + " ='1' WHERE " + " tickername " + "  = " + " '" +tickername+ "' ";
        db.execSQL(sql);
        //  Log.d("become", tickername.toString());

    }

    public void remove_fav(String tickername) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE " + TickerDB2.TABLE_NAME + " SET  " + TickerDB2.COLUMN_FAVOURITE + " ='0' WHERE " + " tickername " + "  = " + " '" +tickername+ "' ";
        db.execSQL(sql);
        // Log.d("remove", tickername.toString());
    }

    public Cursor select_all_favorite_list() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TickerDB2.TABLE_NAME + " WHERE " + TickerDB2.COLUMN_FAVOURITE + " ='1'";
        return db.rawQuery(sql, null, null);
    }

    public Cursor select_all() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TickerDB2.TABLE_NAME;
        return db.rawQuery(sql, null, null);
    }

    public  void update_price (String tickername,String price) throws SQLException {

        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE " + TickerDB2.TABLE_NAME + " SET  " + TickerDB2.COLUMN_PRICE + "  = " + " '" +price+ "' " + " WHERE " + " tickername " + "  = " + " '" +tickername+ "' ";
        db.execSQL(sql);
        // Log.d("!!!!запуска!!!!!", "update_price_DB "+tickername+price);
    }
}
