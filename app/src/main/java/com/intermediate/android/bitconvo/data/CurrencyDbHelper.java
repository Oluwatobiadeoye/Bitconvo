package com.intermediate.android.bitconvo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.intermediate.android.bitconvo.data.CurrencyContract.CurrencyEntry;
import com.intermediate.android.bitconvo.data.CurrencyContract.WatchlistEntry;

/**
 * Created by adeoye oluwatobi on 10/13/2017.
 * This class creates an SQLite database "bitconvo" with two tables: currencies and watchlist
 */
public class CurrencyDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bitconvo.db";
    private static final int DATABASE_VERSION= 1;

    // SQL statement to create table "currencies"
    private final String SQL_CREATE_CURRENCY_TABLE = "CREATE TABLE " + CurrencyEntry.TABLE_NAME + "(" +
    CurrencyEntry.CURR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    CurrencyEntry.CURR_FOREX_NAME + " TEXT, " +
    CurrencyEntry.CURR_FULL_NAME + " TEXT, " +
    CurrencyEntry.CURR_BTC_VAL + " TEXT, " +
    CurrencyEntry.CURR_ETH_VAL + " TEXT, "+
    CurrencyEntry.CURR_BTC_PER + " TEXT, "+
    CurrencyEntry.CURR_ETH_PER + " TEXT);";

    // SQL statement to create table "watchlist"
    private final String SQL_CREATE_WATCHLIST = "CREATE TABLE " + WatchlistEntry.TABLE_NAME + "(" +
            WatchlistEntry.WATCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            WatchlistEntry.RATE_FOREX_NAME + " TEXT, " +
            WatchlistEntry.RATE_FULL_NAME + " TEXT, " +
            WatchlistEntry.VALUE+ " TEXT, " +
            WatchlistEntry.PERCENTAGE + " TEXT);";

    public CurrencyDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_CURRENCY_TABLE);
            db.execSQL(SQL_CREATE_WATCHLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CurrencyEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WatchlistEntry.TABLE_NAME);

        onCreate(db);
    }
}
