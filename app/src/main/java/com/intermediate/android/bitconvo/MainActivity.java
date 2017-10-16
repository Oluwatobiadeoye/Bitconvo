package com.intermediate.android.bitconvo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
        private List<String> currencyNames;
        private RateCursorAdapter mAdapter;
        private static String LOG_TAG = MainActivity.class.getSimpleName();
        private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        putCurrencyNames();
        makeNetworkRequest();
        getSupportLoaderManager().initLoader(1,null,this);
        mAdapter  = new RateCursorAdapter(this,null);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(mAdapter);

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
                            String btcCurrencyValue;
                            String ethCurrencyValue;
                            String btcPer;
                            String ethPer;
                            String forexName;
                            String fullName;

                            for (int i =0; i < currencyNames.size(); i++) {
                                String btc = "BTC";
                                String eth = "ETH";
                                String currentCurrencyName = currencyNames.get(i);
                                String[] partNames = currentCurrencyName.split(" ",2);
                                forexName = partNames[0];
                                fullName = partNames[1];
                                JSONObject btcObject = display.getJSONObject(btc);
                                JSONObject ethObject = display.getJSONObject(eth);
                                JSONObject btcCurrencyObject = btcObject.getJSONObject(forexName);
                                btcCurrencyValue = btcCurrencyObject.getString("PRICE");
                                btcPer = btcCurrencyObject.getString("CHANGEPCT24HOUR");
                                JSONObject ethCurrencyObject = ethObject.getJSONObject(forexName);
                                ethCurrencyValue = ethCurrencyObject.getString("PRICE");
                                ethPer = ethCurrencyObject.getString("CHANGEPCT24HOUR");
                                values.put(CurrencyEntry.CURR_FOREX_NAME,forexName);
                                values.put(CurrencyEntry.CURR_FULL_NAME,fullName);
                                values.put(CurrencyEntry.CURR_BTC_VAL,btcCurrencyValue);
                                values.put(CurrencyEntry.CURR_BTC_PER,btcPer);
                                values.put(CurrencyEntry.CURR_ETH_VAL,ethCurrencyValue);
                                values.put(CurrencyEntry.CURR_ETH_PER,ethPer);
                              Uri u =  getContentResolver().insert(CurrencyEntry.CONTENT_URI,values);
                                Log.v(LOG_TAG,"invalid " + u);
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
            //
            getSharedPreferences("PREFERENCE",MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun",false)
                    .apply();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                WatchlistEntry._ID,
                WatchlistEntry.RATE_FOREX_NAME,
                WatchlistEntry.RATE_FULL_NAME,
                WatchlistEntry.VALUE,
                WatchlistEntry.PERCENTAGE
        };
        return new CursorLoader(this,
                WatchlistEntry.CONTENT_URI,
                projection,
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
}