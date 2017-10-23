package com.intermediate.android.bitconvo;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.intermediate.android.bitconvo.data.CurrencyContract.*;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.NumberFormat;

public class Conversion extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri currentRateUri;
    private Spinner spinnerCoins,spinner_currencies;
    private Cursor cursor;
    private TextView textView;
    private EditText coinEditText,currencyEditText;
    private double mFromValue,constant,convertedValue;
    private String[] parts;
    private String exchangeString,valueForCurrency,currencyForexName,coinForexName,constantStringValue,formattedCoin,formattedCurrency,forexName;
    private List<SpinnerItem> coinSpinnerItems,currencySpinnerItems;
    private SpinnerItem currentCoinSpinnerItem;
    private SpinnerAdapter mCoinAdapter,mCurrencyAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);
        Intent intent = getIntent();
        currentRateUri = intent.getData();

        if (currentRateUri == null) {
            setTitle("Quick Conversion");
            supportInvalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.app_name));
        }

        coinEditText = (EditText) findViewById(R.id.etCoin);
        currencyEditText = (EditText) findViewById(R.id.et_currency);
        textView = (TextView) findViewById(R.id.text);
        spinnerCoins = (Spinner) findViewById(R.id.spinner_coins);
        spinner_currencies = (Spinner) findViewById(R.id.spinner_currencies);

        coinSpinnerItems = getCoinSpinnerList();
        currencySpinnerItems = getCurrencySpinnerList();
        mCoinAdapter = new SpinnerAdapter(this,R.layout.spinners_list,coinSpinnerItems);
        mCurrencyAdapter = new SpinnerAdapter(this,R.layout.spinners_list,currencySpinnerItems);
        spinnerCoins.setAdapter(mCoinAdapter);
        spinner_currencies.setAdapter(mCurrencyAdapter);

        setUpSpinners();

        if (currentRateUri != null) {
            getSupportLoaderManager().initLoader(2, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection =  {WatchlistEntry._ID,
                WatchlistEntry.RATE_FOREX_NAME,
                WatchlistEntry.VALUE,
                };

        return new CursorLoader(this,
                currentRateUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
       if (data.moveToNext()) {
           String coinName;
           String currencyName;
           int forexNameIndex = data.getColumnIndex(WatchlistEntry.RATE_FOREX_NAME);
           int valueIndex = data.getColumnIndex(WatchlistEntry.VALUE);

           forexName = data.getString(forexNameIndex);
           String[] parts = forexName.split(" / ", 2);
           coinName = parts[0];
           currencyName = parts[1];

           switch (coinName) {
               case "BTC":
                   spinnerCoins.setSelection(0);
                   break;
               case "ETH":
                   spinnerCoins.setSelection(1);
           }
           switch (currencyName) {
               case "USD":
                   spinner_currencies.setSelection(0);
                   break;
               case "EUR":
                   spinner_currencies.setSelection(1);
                   break;
               case "JPY":
                   spinner_currencies.setSelection(2);
                   break;
               case "GBP":
                   spinner_currencies.setSelection(3);
                   break;
               case "CHF":
                   spinner_currencies.setSelection(4);
                   break;
               case "CAD":
                   spinner_currencies.setSelection(5);
                   break;
               case "AUD":
                   spinner_currencies.setSelection(6);
                   break;
               case "ZAR":
                   spinner_currencies.setSelection(7);
                   break;
               case "INR":
                   spinner_currencies.setSelection(8);
                   break;
               case "IRR":
                   spinner_currencies.setSelection(9);
                   break;
               case "HKD":
                   spinner_currencies.setSelection(10);
                   break;
               case "JMD":
                   spinner_currencies.setSelection(11);
                   break;
               case "KWD":
                   spinner_currencies.setSelection(12);
                   break;
               case "MYR":
                   spinner_currencies.setSelection(13);
                   break;
               case "NGN":
                   spinner_currencies.setSelection(14);
                   break;
               case "QAR":
                   spinner_currencies.setSelection(15);
                   break;
               case "RUB":
                   spinner_currencies.setSelection(16);
                   break;
               case "SAR":
                   spinner_currencies.setSelection(17);
                   break;
               case "KRW":
                   spinner_currencies.setSelection(18);
                   break;
               case "GHS":
                   spinner_currencies.setSelection(19);
                   break;
           }
           exchangeString = data.getString(valueIndex);
           parts = exchangeString.split(" ", 2);
           valueForCurrency = parts[1];
           coinEditText.setText("1");
           currencyEditText.setText(valueForCurrency);
           textView.setText(1 + " " + coinName + " = " + valueForCurrency + " " + currencyName);
       }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    private void setUpSpinners() {
        loadData();
        final  int btcValueIndex = cursor.getColumnIndex(CurrencyEntry.CURR_BTC_VAL);
        final int forexNameIndex = cursor.getColumnIndex(CurrencyEntry.CURR_FOREX_NAME);
        final  int ethValueIndex = cursor.getColumnIndex(CurrencyEntry.CURR_ETH_VAL);

        spinnerCoins.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCoinSpinnerItem = (SpinnerItem) parent.getItemAtPosition(position);
                coinForexName = currentCoinSpinnerItem.getShortName();
                mFromValue =  numberParse(coinEditText.getText().toString()).doubleValue();

                int currencyPosition = spinner_currencies.getSelectedItemPosition();
                cursor.moveToPosition(currencyPosition);
                switch (position){
                    case 0:
                        exchangeString = cursor.getString(btcValueIndex);
                        break;
                    case 1:
                        exchangeString = cursor.getString(ethValueIndex);
                        break;
                }
                currencyForexName = cursor.getString(forexNameIndex);
                parts = exchangeString.split(" ",2);
                constantStringValue = parts[1];
                constant = numberParse(constantStringValue).doubleValue();

                convertedValue = convertToCurrency(mFromValue,constant);
                formattedCoin = numberFormat(mFromValue);
                formattedCurrency = numberFormat(convertedValue);
                currencyEditText.setText(formattedCurrency);
                textView.setText(formattedCoin + " " + coinForexName + " = " + formattedCurrency + " "  + currencyForexName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_currencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFromValue = numberParse(coinEditText.getText().toString()).doubleValue();
                cursor.moveToPosition(position);
                switch (spinnerCoins.getSelectedItemPosition()){
                    case 0:
                        exchangeString = cursor.getString(btcValueIndex);
                        break;
                    case 1:
                        exchangeString = cursor.getString(ethValueIndex);
                }
                currencyForexName = cursor.getString(forexNameIndex);
                parts = exchangeString.split(" ",2);
                constantStringValue = parts[1];
                constant =numberParse(constantStringValue).doubleValue();
                convertedValue = convertToCurrency(mFromValue,constant);
                formattedCoin = numberFormat(mFromValue);
                coinEditText.setText(formattedCoin);
                formattedCurrency = numberFormat(convertedValue);
                currencyEditText.setText(formattedCurrency);
                textView.setText(formattedCoin + " " + coinForexName + " = " + formattedCurrency + " "  + currencyForexName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        coinEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                coinEditText.setCursorVisible(true);
                return false;
            }
        });

        coinEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    coinEditText.setCursorVisible(false);
                    if (TextUtils.isEmpty(coinEditText.getText().toString())) {
                        coinEditText.setText(formattedCoin);
                        currencyEditText.setText(formattedCurrency);
                        return false;
                    }

                        mFromValue = numberParse(coinEditText.getText().toString()).doubleValue();
                        int currencyPosition = spinner_currencies.getSelectedItemPosition();
                        cursor.moveToPosition(currencyPosition);
                        switch (spinnerCoins.getSelectedItemPosition()){
                            case 0:
                                exchangeString = cursor.getString(btcValueIndex);
                                break;
                            case 1:
                                exchangeString = cursor.getString(ethValueIndex);
                                break;
                        }
                        currencyForexName = cursor.getString(forexNameIndex);
                        parts = exchangeString.split(" ",2);
                        constantStringValue = parts[1];
                        constant = numberParse(constantStringValue).doubleValue();
                        convertedValue = convertToCurrency(mFromValue,constant);
                        formattedCoin = numberFormat(mFromValue);
                        formattedCurrency = numberFormat(convertedValue);
                        currencyEditText.setText(formattedCurrency);
                        textView.setText(formattedCoin + " " + coinForexName + " = " + formattedCurrency + " "  + currencyForexName);
                }
                return false;
            }
        });

        currencyEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                currencyEditText.setCursorVisible(true);
                return false;
            }
        });

        currencyEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    currencyEditText.setCursorVisible(false);
                    if (TextUtils.isEmpty(currencyEditText.getText().toString())) {
                        coinEditText.setText(formattedCoin);
                        currencyEditText.setText(formattedCurrency);
                        return false;
                    }
                        mFromValue = numberParse(currencyEditText.getText().toString()).doubleValue();
                        cursor.moveToPosition(spinner_currencies.getSelectedItemPosition());
                        switch (spinnerCoins.getSelectedItemPosition()){
                            case 0:
                                exchangeString = cursor.getString(btcValueIndex);
                                break;
                            case 1:
                                exchangeString = cursor.getString(ethValueIndex);
                        }
                        currencyForexName = cursor.getString(forexNameIndex);
                        parts = exchangeString.split(" ",2);
                        constantStringValue = parts[1];
                        constant =numberParse(constantStringValue).doubleValue();
                        convertedValue = convertToCoin(mFromValue,constant);
                        formattedCoin = numberFormat(convertedValue);
                        formattedCurrency = numberFormat(mFromValue);
                        coinEditText.setText(formattedCoin);
                        currencyEditText.setText(formattedCurrency);
                        textView.setText(formattedCoin + " " + coinForexName + " = " + formattedCurrency + " "  + currencyForexName);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.delete:
                removeFromWatchList();
        }
        return super.onOptionsItemSelected(item);
    }

    private double convertToCurrency(double fromValue, double constant) {
        return fromValue*constant;
    }

    private double convertToCoin(double fromValue, double constant) {
        return fromValue/constant;
    }

    private String numberFormat (double number) {
        NumberFormat numberformat = NumberFormat.getInstance(Locale.getDefault());
        numberformat.setMaximumFractionDigits(4);
        numberformat.setRoundingMode(RoundingMode.HALF_UP);
        return numberformat.format(number);
    }

    private Number numberParse(String text)  {
        Number number = null;
        try {
             number = NumberFormat.getInstance(Locale.getDefault()).parse(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return number;
    }

    private void loadData() {
        String[] projection = {
                CurrencyEntry.CURR_BTC_VAL,
                CurrencyEntry.CURR_ETH_VAL,
                CurrencyEntry.CURR_FOREX_NAME,
        };
        cursor = getContentResolver().query(CurrencyEntry.CONTENT_URI,projection,null,null,null);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentRateUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete,menu);
        return true;
    }

    private void removeFromWatchList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alert_message))
                .setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getContentResolver().delete(currentRateUri,null,null);
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
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
