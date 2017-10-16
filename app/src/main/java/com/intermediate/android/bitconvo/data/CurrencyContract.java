package com.intermediate.android.bitconvo.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by adeoye oluwatobi on 10/13/2017.
 */
public class CurrencyContract {

    public static final String CONTENT_AUTHORITY = "com.intermediate.android.bitconvo";
    public static final String PATH_CURRENCY = "currencies";
    public static final String PATH_WATCHLIST = "watchlist";

    private CurrencyContract() {
    }

    public static class CurrencyEntry implements BaseColumns {
        public static final String CURR_ID =BaseColumns._ID;
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_CURRENCY);
        public static final String TABLE_NAME="currencies";
        public static final String CURR_FOREX_NAME = "name";
        public static final String CURR_FULL_NAME="full_name";
        public static final String CURR_BTC_VAL="bitcoin_price";
        public static final String CURR_ETH_VAL="ethereum_price";
        public static final String CURR_BTC_PER="bitcoin_percentage";
        public static final String CURR_ETH_PER="ethereum_percentage";
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+ CONTENT_AUTHORITY+"/" +PATH_CURRENCY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"+CONTENT_AUTHORITY+"/"+PATH_CURRENCY;
    }
    public  static class WatchlistEntry implements BaseColumns{
        public static final String WATCH_ID = BaseColumns._ID;
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_WATCHLIST);
        public static final String TABLE_NAME="watchlist";
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+ CONTENT_AUTHORITY+"/" +PATH_WATCHLIST;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"+CONTENT_AUTHORITY+"/"+PATH_WATCHLIST;
        public static final String RATE_FOREX_NAME = "rate";
        public static final String RATE_FULL_NAME = "name";
        public static final String VALUE = "value";
        public static final String PERCENTAGE = "percentage";
    }
}
