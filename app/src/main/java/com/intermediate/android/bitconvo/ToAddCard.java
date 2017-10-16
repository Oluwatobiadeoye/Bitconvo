package com.intermediate.android.bitconvo;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.intermediate.android.bitconvo.data.CurrencyContract.*;
import com.intermediate.android.bitconvo.data.CurrencyDbHelper;

public class ToAddCard extends AppCompatActivity {

    private Spinner spinnerCoins;
    private Spinner spinner_currencies;
    private CurrencyDbHelper mCurrencyDbhelper;
    private SQLiteDatabase db;
    private TextView exchangeRate;
    private String coinValue;
    private String currencyValue;
    private Cursor data;
    private String tableCurrencyValue;
    private String currencyForexName;
    private String coinForexName;
    private String[] parts;
    private String lastPart;
    private String coinFullName;
    private String currencyFullName;
    private String percentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_add_card);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear);

        if (mCurrencyDbhelper == null) {
            mCurrencyDbhelper = new CurrencyDbHelper(this);
        }
        exchangeRate = (TextView) findViewById(R.id.exchange_rate);
        spinner_currencies = (Spinner) findViewById(R.id.spinner_currencies);
        loadData();
        setUpSpinners();


    }

    private void setUpSpinners() {
        spinnerCoins = (Spinner) findViewById(R.id.spinner_coins);
        spinner_currencies = (Spinner) findViewById(R.id.spinner_currencies);

        ArrayAdapter coinsArrayAdapter = ArrayAdapter.createFromResource(this,R.array.coinsArray,
                android.R.layout.simple_spinner_item);
        coinsArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter currenciesArrayAdapter = ArrayAdapter.createFromResource(this,R.array.currenciesArray,
                android.R.layout.simple_spinner_item);
        currenciesArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        spinnerCoins.setAdapter(coinsArrayAdapter);
        spinner_currencies.setAdapter(currenciesArrayAdapter);

        final  int btcValueIndex = data.getColumnIndex(CurrencyEntry.CURR_BTC_VAL);
        final int forexNameIndex = data.getColumnIndex(CurrencyEntry.CURR_FOREX_NAME);
        final  int ethValueIndex = data.getColumnIndex(CurrencyEntry.CURR_ETH_VAL);
        final int fullNameIndex =  data.getColumnIndex(CurrencyEntry.CURR_FULL_NAME);
        final int percentageBtcIndex = data.getColumnIndex(CurrencyEntry.CURR_BTC_PER);
        final int percentageEthIndex = data.getColumnIndex(CurrencyEntry.CURR_ETH_PER);


        spinnerCoins.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                coinForexName = (String) parent.getItemAtPosition(position);
                coinValue = 1 + " "+coinForexName;
                int currencyPosition = spinner_currencies.getSelectedItemPosition();
                data.moveToPosition(currencyPosition);
                switch (position) {
                    case 0:
                        tableCurrencyValue = data.getString(btcValueIndex);
                        percentage = data.getString(percentageBtcIndex);
                        coinFullName ="Bitcoin";
                        break;
                    case 1:
                        tableCurrencyValue = data.getString(ethValueIndex);
                        percentage = data.getString(percentageEthIndex);
                        coinFullName = "Ethereum";
                        break;
                }
                currencyForexName = data.getString(forexNameIndex);

                parts =  tableCurrencyValue.split(" ",2);
                lastPart = parts[1];
                currencyValue = lastPart + " " + currencyForexName;
                exchangeRate.setText(coinValue+ " = " + currencyValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner_currencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                data.moveToPosition(position);
                switch (spinnerCoins.getSelectedItemPosition()) {
                    case 0:
                         tableCurrencyValue = data.getString(btcValueIndex);
                         percentage = data.getString(percentageBtcIndex);
                        break;
                    case 1:
                     tableCurrencyValue = data.getString(ethValueIndex);
                     percentage = data.getString(percentageEthIndex);
                        break;
            }
                currencyForexName = data.getString(forexNameIndex);
                currencyFullName = data.getString(fullNameIndex);
                parts =  tableCurrencyValue.split(" ",2);
                lastPart = parts[1];
                currencyValue = lastPart + " " + currencyForexName;
                exchangeRate.setText(coinValue+ " = " + currencyValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadData() {
        if (db == null) {
            db = mCurrencyDbhelper.getReadableDatabase();
        }
        final String[] projection = {
                CurrencyEntry._ID,
                CurrencyEntry.CURR_BTC_VAL,
                CurrencyEntry.CURR_ETH_VAL,
                CurrencyEntry.CURR_BTC_PER,
                CurrencyEntry.CURR_ETH_PER,
                CurrencyEntry.CURR_FULL_NAME,
                CurrencyEntry.CURR_FOREX_NAME,
        };
        data = db.query(CurrencyEntry.TABLE_NAME,projection,null,null,null,null,null);
    }

    private void addToWatchlist() {

        ContentValues values = new ContentValues();
        String rateForexName = coinForexName +" / " + currencyForexName;
        String rateFullName  = coinFullName + " / " + currencyFullName;
        values.put(WatchlistEntry.RATE_FOREX_NAME,rateForexName);
        values.put(WatchlistEntry.RATE_FULL_NAME,rateFullName);
        values.put(WatchlistEntry.VALUE,tableCurrencyValue);
        values.put(WatchlistEntry.PERCENTAGE,percentage);

            String[] projection = {WatchlistEntry.RATE_FOREX_NAME};
            boolean findMatch = false;
            Cursor cursor = getContentResolver().query(WatchlistEntry.CONTENT_URI, projection, null, null, null, null);
            int rateForexIndex = cursor.getColumnIndex(WatchlistEntry.RATE_FOREX_NAME);
            String nameValue;
            Uri inserted;

            if (cursor.getCount() != 0) {

                while (cursor.moveToNext()) {
                    nameValue = cursor.getString(rateForexIndex);
                    if (nameValue.equals(rateForexName)) {
                        findMatch = true;
                        break;
                    }
                }

                    if (findMatch == false) {
                        inserted = getContentResolver().insert(WatchlistEntry.CONTENT_URI, values);
                        if (inserted != null) {
                            ToAddCard.this.finish();
                            closeCursor();
                        } else {
                            Toast.makeText(ToAddCard.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                            ToAddCard.this.finish();
                            closeCursor();
                        }
                    } else {
                        Toast.makeText(ToAddCard.this, "Pair already in Watchlist", Toast.LENGTH_SHORT).show();
                    }

            } else {
                inserted = getContentResolver().insert(WatchlistEntry.CONTENT_URI, values);
                if (inserted != null) {
                    ToAddCard.this.finish();
                    closeCursor();
                } else {
                    Toast.makeText(ToAddCard.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                    ToAddCard.this.finish();
                    closeCursor();
                }
            }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addToWatchlist();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add,menu);
        return true;
    }

    private void closeCursor() {
        if (data != null) {
            data.close();
        }
    }
}
