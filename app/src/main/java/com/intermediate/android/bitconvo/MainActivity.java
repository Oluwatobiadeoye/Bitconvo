package com.intermediate.android.bitconvo;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.intermediate.android.bitconvo.data.CurrencyContract;
import com.intermediate.android.bitconvo.data.CurrencyContract.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
        private static final  String CRYPTO_REQUEST_URL = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms=BTC,ETH&" +
                "tsyms=USD,EUR,JPY,GBP,CHF,CAD,AUD,ZAR,INR,IRR,HKD,JMD,KWD,MYR,NGN,QAR,RUB,SAR,KRW,GHS";
        private static String LOG_TAG = MainActivity.class.getSimpleName();
        private List<String> currencyNames;
        private RateCursorAdapter mAdapter;
        private FloatingActionButton fab;
        private SwipeRefreshLayout swipeRefreshLayout;
        private String btcCurrencyValue,ethCurrencyValue,btcPer,ethPer,forexName,coinforexName,currencyForexName,fullName,currentCurrencyName;
        private String[] partNames,projection1,projection2;
        private Uri currentRateUri;
        private long currentRateId;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_rate:
                 updateData();
                return true;
            case R.id.delete_all:
                deleteAll();
                return true;
            case R.id.convert:
                Intent intent = new Intent(MainActivity.this, Conversion.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter  = new RateCursorAdapter(this,null);
        ListView listView = (ListView) findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.progress1,
                R.color.progress2,
                R.color.progress3,
                R.color.progress4
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });

        putCurrencyNames();
        checkFirstRun();
        getSupportLoaderManager().initLoader(1,null,this);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,Conversion.class);
                currentRateUri = ContentUris.withAppendedId(WatchlistEntry.CONTENT_URI,id);
                intent.setData(currentRateUri);
                startActivity(intent);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    fab.animate().cancel();
                    fab.setVisibility(View.INVISIBLE);
                } else {
                    fab.animate().cancel();
                    fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (MainActivity.this,ToAddCard.class);
                startActivity(intent);
            }
        });
    }


    private void makeNetworkRequest() {
        JsonObjectRequest requestCurrencyValues = new JsonObjectRequest(Request.Method.GET, CRYPTO_REQUEST_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject display = response.getJSONObject("DISPLAY");
                            ContentValues values = new ContentValues();
                            for (int i =0; i < currencyNames.size(); i++) {
                                String btc = "BTC";
                                String eth = "ETH";
                                currentCurrencyName = currencyNames.get(i);
                                partNames = currentCurrencyName.split(" ",2);
                                currencyForexName = partNames[0];
                                fullName = partNames[1];
                                JSONObject btcObject = display.getJSONObject(btc);
                                JSONObject ethObject = display.getJSONObject(eth);
                                JSONObject btcCurrencyObject = btcObject.getJSONObject(currencyForexName);
                                btcCurrencyValue = btcCurrencyObject.getString("PRICE");
                                btcPer = btcCurrencyObject.getString("CHANGEPCT24HOUR");
                                JSONObject ethCurrencyObject = ethObject.getJSONObject(currencyForexName);
                                ethCurrencyValue = ethCurrencyObject.getString("PRICE");
                                ethPer = ethCurrencyObject.getString("CHANGEPCT24HOUR");
                                values.put(CurrencyEntry.CURR_FOREX_NAME,currencyForexName);
                                values.put(CurrencyEntry.CURR_FULL_NAME,fullName);
                                values.put(CurrencyEntry.CURR_BTC_VAL,btcCurrencyValue);
                                values.put(CurrencyEntry.CURR_BTC_PER,btcPer);
                                values.put(CurrencyEntry.CURR_ETH_VAL,ethCurrencyValue);
                                values.put(CurrencyEntry.CURR_ETH_PER,ethPer);
                                getContentResolver().insert(CurrencyEntry.CONTENT_URI,values);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        NetworkRequest.getInstance(getApplicationContext()).addToRequestQueue(requestCurrencyValues);
    }

    private void putCurrencyNames () {
        currencyNames = new ArrayList<>();
        currencyNames.add("USD United States Dollar");
        currencyNames.add("EUR European Euro");
        currencyNames.add("JPY Japanese Yen");
        currencyNames.add("GBP Great British Pound");
        currencyNames.add("CHF Swiss Franc");
        currencyNames.add("CAD Canadian Dollar");
        currencyNames.add("AUD Australian Dollar");
        currencyNames.add("ZAR South African Dollar");
        currencyNames.add("INR Indian Rupee");
        currencyNames.add("IRR Iranian Rial");
        currencyNames.add("HKD Hong Kong Dollar");
        currencyNames.add("JMD Jamaican dollar");
        currencyNames.add("KWD Kuwaiti Dinar");
        currencyNames.add("MYR Malaysian Ringgit");
        currencyNames.add("NGN Nigerian Naira");
        currencyNames.add("QAR Qatari Rial");
        currencyNames.add("RUB Russian Rubble");
        currencyNames.add("SAR Saudi Riyal");
        currencyNames.add("KRW South Korea Won");
        currencyNames.add("GHS Ghanian Cedi");
    }
    public  void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE",MODE_PRIVATE).getBoolean("isFirstRun",true);
        if (isFirstRun) {
            makeNetworkRequest();
            getSharedPreferences("PREFERENCE",MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun",false).apply();
        } else {
            updateData();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
         projection1 = new String[] {
                WatchlistEntry._ID,
                WatchlistEntry.RATE_FOREX_NAME,
                WatchlistEntry.RATE_FULL_NAME,
                WatchlistEntry.VALUE,
                WatchlistEntry.PERCENTAGE
        };
        return new CursorLoader(this,
                WatchlistEntry.CONTENT_URI,
                projection1,
                null,
                null,
                null
                );
    }

    private void updateData() {
        JsonObjectRequest requestUpdatedValues = new JsonObjectRequest(Request.Method.GET, CRYPTO_REQUEST_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject display = response.getJSONObject("DISPLAY");
                            ContentValues values = new ContentValues();
                            for (int i =0; i < currencyNames.size(); i++) {
                                String btc = "BTC";
                                String eth = "ETH";
                                currentCurrencyName = currencyNames.get(i);
                                partNames = currentCurrencyName.split(" ",2);
                                currencyForexName = partNames[0];
                                JSONObject btcObject = display.getJSONObject(btc);
                                JSONObject ethObject = display.getJSONObject(eth);
                                JSONObject btcCurrencyObject = btcObject.getJSONObject(currencyForexName);
                                btcCurrencyValue = btcCurrencyObject.getString("PRICE");
                                btcPer = btcCurrencyObject.getString("CHANGEPCT24HOUR");
                                JSONObject ethCurrencyObject = ethObject.getJSONObject(currencyForexName);
                                ethCurrencyValue = ethCurrencyObject.getString("PRICE");
                                ethPer = ethCurrencyObject.getString("CHANGEPCT24HOUR");
                                values.put(CurrencyEntry.CURR_BTC_VAL,btcCurrencyValue);
                                values.put(CurrencyEntry.CURR_BTC_PER,btcPer);
                                values.put(CurrencyEntry.CURR_ETH_VAL,ethCurrencyValue);
                                values.put(CurrencyEntry.CURR_ETH_PER,ethPer);
                                currentRateId = i + 1;
                                currentRateUri = ContentUris.withAppendedId(CurrencyEntry.CONTENT_URI,currentRateId);
                                getContentResolver().update(currentRateUri,values,null,null);
                            }
                            String updatedValue,updatedPercentage;
                            long watchlistCurrentId;
                            ContentValues valuesWatch = new ContentValues();
                            projection2 = new String[]{
                                    WatchlistEntry._ID,
                                    WatchlistEntry.RATE_FOREX_NAME,
                            };
                            Cursor cursor = getContentResolver().query(WatchlistEntry.CONTENT_URI,projection2,null,null,null);
                            int forexNameIndex = cursor.getColumnIndex(WatchlistEntry.RATE_FOREX_NAME);
                            int _IdIndex = cursor.getColumnIndex(WatchlistEntry.WATCH_ID);
                            while (cursor.moveToNext()) {
                                forexName = cursor.getString(forexNameIndex);
                                watchlistCurrentId = cursor.getLong(_IdIndex);
                                currentRateUri = ContentUris.withAppendedId(WatchlistEntry.CONTENT_URI,watchlistCurrentId);
                                partNames = forexName.split(" / ", 2);
                                coinforexName = partNames[0];
                                currencyForexName = partNames[1];
                                JSONObject coinObject = display.getJSONObject(coinforexName);
                                JSONObject currencyObject = coinObject.getJSONObject(currencyForexName);
                                updatedValue = currencyObject.getString("PRICE");
                                updatedPercentage = currencyObject.getString("CHANGEPCT24HOUR");
                                valuesWatch.put(WatchlistEntry.VALUE,updatedValue);
                                valuesWatch.put(WatchlistEntry.PERCENTAGE,updatedPercentage);
                                getContentResolver().update(currentRateUri,valuesWatch,null,null);
                            }
                            cursor.close();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        NetworkRequest.getInstance(getApplicationContext()).addToRequestQueue(requestUpdatedValues);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private  void deleteAll() {
        getContentResolver().delete(WatchlistEntry.CONTENT_URI,null,null);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirm remove?")
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAll();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}