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

import java.util.ArrayList;
import java.util.List;

public class ToAddCard extends AppCompatActivity {

    private String coinValue,currencyValue,tableCurrencyValue,currencyForexName,coinForexName,lastPart,coinFullName,currencyFullName,percentage;
    private Cursor data;
    private String[] parts;
    private Spinner spinnerCoins,spinner_currencies;
    private TextView exchangeRate;
    private List<SpinnerItem> coinSpinnerItems,currencySpinnerItems;
    private SpinnerAdapter mCoinAdapter,mCurrencyAdapter;
    private SpinnerItem currentCoinSpinnerItem,currentCurrencySpinnerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_add_card);
        loadData();

        spinnerCoins = (Spinner) findViewById(R.id.spinner_coins);
        spinner_currencies = (Spinner) findViewById(R.id.spinner_currencies);
        exchangeRate = (TextView) findViewById(R.id.exchange_rate);

        coinSpinnerItems = getCoinSpinnerList();
        currencySpinnerItems = getCurrencySpinnerList();
        mCoinAdapter = new SpinnerAdapter(this,R.layout.spinners_list,coinSpinnerItems);
        mCurrencyAdapter = new SpinnerAdapter(this,R.layout.spinners_list,currencySpinnerItems);
        spinnerCoins.setAdapter(mCoinAdapter);
        spinner_currencies.setAdapter(mCurrencyAdapter);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear);
        setUpSpinners();


    }

    private void setUpSpinners() {
        final  int btcValueIndex = data.getColumnIndex(CurrencyEntry.CURR_BTC_VAL);
        final int forexNameIndex = data.getColumnIndex(CurrencyEntry.CURR_FOREX_NAME);
        final  int ethValueIndex = data.getColumnIndex(CurrencyEntry.CURR_ETH_VAL);
        final int fullNameIndex =  data.getColumnIndex(CurrencyEntry.CURR_FULL_NAME);
        final int percentageBtcIndex = data.getColumnIndex(CurrencyEntry.CURR_BTC_PER);
        final int percentageEthIndex = data.getColumnIndex(CurrencyEntry.CURR_ETH_PER);


        spinnerCoins.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCoinSpinnerItem = (SpinnerItem) parent.getItemAtPosition(position);
                coinForexName = currentCoinSpinnerItem.getShortName();
                coinValue = 1 + " "+coinForexName;
                int currencyPosition = spinner_currencies.getSelectedItemPosition();
                data.moveToPosition(currencyPosition);
                switch (position) {
                    case 0:
                        tableCurrencyValue = data.getString(btcValueIndex);
                        percentage = data.getString(percentageBtcIndex);
                        break;
                    case 1:
                        tableCurrencyValue = data.getString(ethValueIndex);
                        percentage = data.getString(percentageEthIndex);
                        break;
                }
                coinFullName = currentCoinSpinnerItem.getFullName();

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
        final String[] projection = {
                CurrencyEntry._ID,
                CurrencyEntry.CURR_BTC_VAL,
                CurrencyEntry.CURR_ETH_VAL,
                CurrencyEntry.CURR_BTC_PER,
                CurrencyEntry.CURR_ETH_PER,
                CurrencyEntry.CURR_FULL_NAME,
                CurrencyEntry.CURR_FOREX_NAME,
        };
        data = getContentResolver().query(CurrencyEntry.CONTENT_URI,projection,null,null,null);
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

                    if (!findMatch) {
                        inserted = getContentResolver().insert(WatchlistEntry.CONTENT_URI, values);
                        if (inserted != null) {
                            ToAddCard.this.finish();
                            closeCursor(cursor);
                            closeCursor(data);
                        } else {
                            Toast.makeText(ToAddCard.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                            ToAddCard.this.finish();
                            closeCursor(cursor);
                            closeCursor(data);
                        }
                    } else {
                        Toast.makeText(ToAddCard.this, "Pair already in Watchlist", Toast.LENGTH_SHORT).show();
                    }

            } else {
                inserted = getContentResolver().insert(WatchlistEntry.CONTENT_URI, values);
                if (inserted != null) {
                    ToAddCard.this.finish();
                    closeCursor(cursor);
                    closeCursor(data);
                } else {
                    Toast.makeText(ToAddCard.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                    ToAddCard.this.finish();
                    closeCursor(cursor);
                    closeCursor(data);
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

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    public ArrayList<SpinnerItem> getCurrencySpinnerList() {
        ArrayList<SpinnerItem> spinnerItems = new ArrayList<>();
        spinnerItems.add(new SpinnerItem("USD","United States Dollar",R.drawable.bitcoin));
        spinnerItems.add(new SpinnerItem("EUR" ,"European Euro",R.drawable.ethereum));
        spinnerItems.add(new SpinnerItem("JPY","Japanese Yen",R.drawable.ethereum));
        spinnerItems.add(new SpinnerItem("GBP", "Great British Pound",R.drawable.ethereum));
        spinnerItems.add(new SpinnerItem("CHF", "Swiss Franc",R.drawable.ethereum));
        spinnerItems.add(new SpinnerItem("CAD", "Canadian Dollar",R.drawable.bitcoin));
        spinnerItems.add(new SpinnerItem("AUD", "Australian Dollar",R.drawable.bitcoin));
        spinnerItems.add(new SpinnerItem("ZAR", "South African Dollar",R.drawable.bitcoin));
        spinnerItems.add(new SpinnerItem("INR", "Indian Rupee",R.drawable.ethereum));
        spinnerItems.add(new SpinnerItem("IRR", "Iranian Rial",R.drawable.ethereum));
        spinnerItems.add(new SpinnerItem("HKD", "Hong Kong Dollar",R.drawable.ethereum));
        spinnerItems.add(new SpinnerItem("JMD", "Jamaican dollar",R.drawable.bitcoin));
        spinnerItems.add(new SpinnerItem("KWD", "Kuwaiti Dinar",R.drawable.bitcoin));
        spinnerItems.add(new SpinnerItem("MYR", "Malaysian Ringgit",R.drawable.bitcoin));
        spinnerItems.add(new SpinnerItem("NGN", "Nigerian Naira",R.drawable.bitcoin));
        spinnerItems.add(new SpinnerItem("QAR", "Qatari Rial",R.drawable.bitcoin));
        spinnerItems.add(new SpinnerItem("RUB", "Russian Rubble",R.drawable.bitcoin));
        spinnerItems.add(new SpinnerItem("SAR", "Saudi Riyal",R.drawable.bitcoin));
        spinnerItems.add(new SpinnerItem("KRW", "South Korea Won",R.drawable.ethereum));
        spinnerItems.add(new SpinnerItem("GHS", "Ghanian Cedi",R.drawable.ethereum));
        return spinnerItems;
    }
    public ArrayList<SpinnerItem> getCoinSpinnerList() {
        ArrayList<SpinnerItem> spinnerItems = new ArrayList<>();
        spinnerItems.add(new SpinnerItem("BTC","Bitcoin",R.drawable.bitcoin));
        spinnerItems.add(new SpinnerItem("ETH" ,"Ethereum",R.drawable.ethereum));
        return spinnerItems;
    }
}
