package com.intermediate.android.bitconvo;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.intermediate.android.bitconvo.data.CurrencyContract.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
        private static final  String CRYPTO_REQUEST_URL = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms=BTC,ETH&" +
                "tsyms=USD,EUR,JPY,GBP,CHF,CAD,AUD,ZAR,INR,IRR,HKD,JMD,KWD,MYR,NGN,QAR,RUB,SAR,KRW,GHS";
        private String btcCurrencyValue,ethCurrencyValue,btcPer,ethPer,forexName,coinforexName,currencyForexName,fullName,currentCurrencyName;
        private String[] partNames,projection1,projection2;
        private SwipeRefreshLayout swipeRefreshLayout;
        private List<String> currencyNames;
        private RateCursorAdapter mAdapter;
        private FloatingActionButton fab;
        private boolean isFirstRun;
        private  ImageView imageView;
        private Uri currentRateUri;
        private long currentRateId;
        private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if there is an active network connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() ) {
            mAdapter = new RateCursorAdapter(this, null);
            ListView listView = (ListView) findViewById(R.id.list);
            emptyView = findViewById(R.id.empty_view);
            imageView = (ImageView) findViewById(R.id.plus_sign);
            fab = (FloatingActionButton) findViewById(R.id.fab);
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
            getSupportLoaderManager().initLoader(1, null, this);
            listView.setEmptyView(emptyView);
            listView.setAdapter(mAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, ConversionActivity.class);
                    currentRateUri = ContentUris.withAppendedId(WatchlistEntry.CONTENT_URI, id);
                    intent.setData(currentRateUri);
                    startActivity(intent);
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                    startActivity(intent);
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
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
                        fab.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                }
            });
        } else {
            startActivity(new Intent(MainActivity.this, NetworkProblemActivity.class));
        }
    }

    private void makeNetworkRequest() {
        supportInvalidateOptionsMenu();
        JsonObjectRequest requestCurrencyValues = new JsonObjectRequest(Request.Method.GET, CRYPTO_REQUEST_URL, null,
                 new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject display = response.getJSONObject("DISPLAY");
                            ContentValues values = new ContentValues();
                            for (int i =0; i < currencyNames.size(); i++) {
                                currentCurrencyName = currencyNames.get(i);
                                partNames = currentCurrencyName.split(" ",2);
                                currencyForexName = partNames[0];
                                fullName = partNames[1];

                                JSONObject btcObject = display.getJSONObject("BTC");
                                JSONObject ethObject = display.getJSONObject("ETH");
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
                            imageView.setVisibility(View.VISIBLE);
                            fab.setVisibility(View.VISIBLE);
                            getSharedPreferences("PREFERENCE",MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("isFirstRun",false).apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (emptyView !=null) {
                    emptyView.setVisibility(View.GONE);
                }
                    Intent intent = new Intent(MainActivity.this, NetworkProblemActivity.class);
                    startActivity(intent);
                    finish();
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

    // Check if the application is running for the first time
    // if yes, insert data into the database else update the data in the database.
    public  void checkFirstRun() {
        isFirstRun = getSharedPreferences("PREFERENCE",MODE_PRIVATE).getBoolean("isFirstRun",true);
        if (isFirstRun) {
            makeNetworkRequest();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_rate:
                swipeRefreshLayout.setRefreshing(true);
                updateData();
                return true;
            case R.id.delete_all:
                showDialog();
                return true;
            case R.id.convert:
                Intent intent = new Intent(MainActivity.this, ConversionActivity.class);
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


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alert_messageII))
                .setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAll();
                    }
                })
                .setNegativeButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateData() {
        if (fab.getVisibility() == View.INVISIBLE && !isFirstRun) {
            fab.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
        }
        JsonObjectRequest requestUpdatedValues = new JsonObjectRequest(Request.Method.GET, CRYPTO_REQUEST_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject display = response.getJSONObject("DISPLAY");
                            ContentValues values = new ContentValues();
                            for (int i =0; i < currencyNames.size(); i++) {
                                currentCurrencyName = currencyNames.get(i);
                                partNames = currentCurrencyName.split(" ",2);
                                currencyForexName = partNames[0];

                                JSONObject btcObject = display.getJSONObject("BTC");
                                JSONObject ethObject = display.getJSONObject("ETH");
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
                String message;
                if (error instanceof NetworkError || error instanceof TimeoutError) {
                    message ="No internet connection";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof ServerError) {
                    message = "The server could not be found.Please try again after some time!!";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                } else {
                    message = "Parsing error! Please try again after some time!!";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
        NetworkRequest.getInstance(getApplicationContext()).addToRequestQueue(requestUpdatedValues);
    }
}