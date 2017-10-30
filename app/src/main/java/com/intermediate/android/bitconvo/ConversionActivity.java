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

public class ConversionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private String exchangeString,valueForCurrency,currencyForexName,coinForexName,constantStringValue, formattedCoinValue, formattedCurrencyValue,forexName;
    private List<SpinnerItem> coinSpinnerItems,currencySpinnerItems;
    private int btcValueIndex,forexNameIndex,ethValueIndex;
    private SpinnerAdapter mCoinAdapter,mCurrencyAdapter;
    private double fromValue,constant,convertedValue;
    private Spinner spinnerCoins,spinnerCurrencies;
    private EditText coinEditText,currencyEditText;
    private SpinnerItem currentCoinSpinnerItem;
    private Uri currentRateUri;
    private TextView textView;
    private String[] parts;
    private Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        //Get the uri of each cards clicked in the MainActivity ListView.
        Intent intent = getIntent();
        currentRateUri = intent.getData();

        // Check if the the above uri has a valid value
        if (currentRateUri == null) {
            setTitle("Quick Conversion");
            supportInvalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.app_name));
        }

        // Get references to the views in the layout
        coinEditText = (EditText) findViewById(R.id.etCoin);
        currencyEditText = (EditText) findViewById(R.id.et_currency);
        textView = (TextView) findViewById(R.id.text);
        spinnerCoins = (Spinner) findViewById(R.id.spinner_coins);
        spinnerCurrencies = (Spinner) findViewById(R.id.spinner_currencies);

        coinSpinnerItems = getCoinSpinnerList();
        currencySpinnerItems = getCurrencySpinnerList();
        mCoinAdapter = new SpinnerAdapter(this,R.layout.spinners_list,coinSpinnerItems);
        mCurrencyAdapter = new SpinnerAdapter(this,R.layout.spinners_list,currencySpinnerItems);
        spinnerCoins.setAdapter(mCoinAdapter);
        spinnerCurrencies.setAdapter(mCurrencyAdapter);

        setUpSpinnersAndEditText();
        getSupportLoaderManager().initLoader(2, null, this);

    }

    // This query the database for some data in a background thread
    // which are used to update UI.
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader data;
        String[] projection1 =  {WatchlistEntry._ID,
                WatchlistEntry.RATE_FOREX_NAME,
                WatchlistEntry.VALUE,

                };
        String[] projection2 = {
                CurrencyEntry._ID,
                CurrencyEntry.CURR_BTC_VAL
        };
if(currentRateUri != null) {
     data = new CursorLoader(this,
            currentRateUri,
            projection1,
            null,
            null,
            null);
} else {
    data = new CursorLoader(this,
            CurrencyEntry.CONTENT_URI,
            projection2,
            null,
            null,
            null);
}
        return data;
    }

    // This is called after loading the data in a background thread
    // The code to immediately update the UI also lies here.
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String coinName;
        String currencyName;
        int valueIndex;
        if (data.moveToNext() && currentRateUri != null) {
            int forexNameIndex = data.getColumnIndex(WatchlistEntry.RATE_FOREX_NAME);
            valueIndex = data.getColumnIndex(WatchlistEntry.VALUE);

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
                    spinnerCurrencies.setSelection(0);
                    break;
                case "EUR":
                    spinnerCurrencies.setSelection(1);
                    break;
                case "JPY":
                    spinnerCurrencies.setSelection(2);
                    break;
                case "GBP":
                    spinnerCurrencies.setSelection(3);
                    break;
                case "CHF":
                    spinnerCurrencies.setSelection(4);
                    break;
                case "CAD":
                    spinnerCurrencies.setSelection(5);
                    break;
                case "AUD":
                    spinnerCurrencies.setSelection(6);
                    break;
                case "ZAR":
                    spinnerCurrencies.setSelection(7);
                    break;
                case "INR":
                    spinnerCurrencies.setSelection(8);
                    break;
                case "IRR":
                    spinnerCurrencies.setSelection(9);
                    break;
                case "HKD":
                    spinnerCurrencies.setSelection(10);
                    break;
                case "JMD":
                    spinnerCurrencies.setSelection(11);
                    break;
                case "KWD":
                    spinnerCurrencies.setSelection(12);
                    break;
                case "MYR":
                    spinnerCurrencies.setSelection(13);
                    break;
                case "NGN":
                    spinnerCurrencies.setSelection(14);
                    break;
                case "QAR":
                    spinnerCurrencies.setSelection(15);
                    break;
                case "RUB":
                    spinnerCurrencies.setSelection(16);
                    break;
                case "SAR":
                    spinnerCurrencies.setSelection(17);
                    break;
                case "KRW":
                    spinnerCurrencies.setSelection(18);
                    break;
                case "GHS":
                    spinnerCurrencies.setSelection(19);
                    break;
            }
            exchangeString = data.getString(valueIndex);
            parts = exchangeString.split(" ", 2);
            valueForCurrency = parts[1];
            coinEditText.setText("1");
            currencyEditText.setText(valueForCurrency);
            textView.setText(String.format("%s %s = %s %s", "1",coinName,valueForCurrency,currencyName));
        }
        else if (data.moveToNext() && currentRateUri == null){
            valueIndex = data.getColumnIndex(CurrencyEntry.CURR_BTC_VAL);
            spinnerCoins.setSelection(0);
            spinnerCurrencies.setSelection(0);
            exchangeString = data.getString(valueIndex);
            parts = exchangeString.split(" ", 2);
            valueForCurrency = parts[1];
            coinEditText.setText("1");
            currencyEditText.setText(valueForCurrency);
            coinName = "BTC";
            currencyName = "USD";
            textView.setText(String.format("%s %s = %s %s", "1",coinName,valueForCurrency,currencyName));
        }
    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Empty method
    }
    private void setUpSpinnersAndEditText() {
        loadData();

        // get the index value of the column headers in the database
        btcValueIndex = cursor.getColumnIndex(CurrencyEntry.CURR_BTC_VAL);
        forexNameIndex = cursor.getColumnIndex(CurrencyEntry.CURR_FOREX_NAME);
        ethValueIndex = cursor.getColumnIndex(CurrencyEntry.CURR_ETH_VAL);

        spinnerCoins.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCoinSpinnerItem = (SpinnerItem) parent.getItemAtPosition(position);
                coinForexName = currentCoinSpinnerItem.getShortName();
                if (TextUtils.isEmpty(coinEditText.getText().toString())) {
                    fromValue = 1;
                }
                else  {  fromValue =  numberParse(coinEditText.getText().toString()).doubleValue();}
                int currencyPosition = spinnerCurrencies.getSelectedItemPosition();
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

                convertedValue = convertToCurrency(fromValue,constant);
                formattedCoinValue = numberFormat(fromValue);
                formattedCurrencyValue = numberFormat(convertedValue);
                coinEditText.setText(formattedCoinValue);
                currencyEditText.setText(formattedCurrencyValue);
                textView.setText(String.format("%s %s = %s %s", formattedCoinValue,coinForexName, formattedCurrencyValue,currencyForexName));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCurrencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (TextUtils.isEmpty(coinEditText.getText().toString())) {
                    fromValue = 1;
                }
                else  {  fromValue =  numberParse(coinEditText.getText().toString()).doubleValue();}
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
                convertedValue = convertToCurrency(fromValue,constant);
                formattedCoinValue = numberFormat(fromValue);
                coinEditText.setText(formattedCoinValue);
                formattedCurrencyValue = numberFormat(convertedValue);
                currencyEditText.setText(formattedCurrencyValue);
                textView.setText(String.format("%s %s = %s %s", formattedCoinValue,coinForexName, formattedCurrencyValue,currencyForexName));
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
                        coinEditText.setText(formattedCoinValue);
                        currencyEditText.setText(formattedCurrencyValue);
                        return false;
                    }

                        fromValue = numberParse(coinEditText.getText().toString()).doubleValue();
                        int currencyPosition = spinnerCurrencies.getSelectedItemPosition();
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
                        convertedValue = convertToCurrency(fromValue,constant);
                        formattedCoinValue = numberFormat(fromValue);
                        formattedCurrencyValue = numberFormat(convertedValue);
                        coinEditText.setText(formattedCoinValue);
                        currencyEditText.setText(formattedCurrencyValue);
                        textView.setText(String.format("%s %s = %s %s", formattedCoinValue,coinForexName, formattedCurrencyValue,currencyForexName));
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
                        coinEditText.setText(formattedCoinValue);
                        currencyEditText.setText(formattedCurrencyValue);
                        return false;
                    }
                        fromValue = numberParse(currencyEditText.getText().toString()).doubleValue();
                        cursor.moveToPosition(spinnerCurrencies.getSelectedItemPosition());
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
                        convertedValue = convertToCoin(fromValue,constant);
                        formattedCoinValue = numberFormat(convertedValue);
                        formattedCurrencyValue = numberFormat(fromValue);
                        coinEditText.setText(formattedCoinValue);
                        currencyEditText.setText(formattedCurrencyValue);
                        textView.setText(String.format("%s %s = %s %s", formattedCoinValue,coinForexName, formattedCurrencyValue,currencyForexName));
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeCursor();
                NavUtils.navigateUpFromSameTask(this);
                finish();
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

    // coverts double into a String using the NumberFormat method
    // and the user's default Locale
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
                        closeCursor();
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
        spinnerItems.add(new SpinnerItem("USD","United States Dollar",R.drawable.usa));
        spinnerItems.add(new SpinnerItem("EUR" ,"European Euro",R.drawable.euro));
        spinnerItems.add(new SpinnerItem("JPY","Japanese Yen",R.drawable.japan));
        spinnerItems.add(new SpinnerItem("GBP", "Great British Pound",R.drawable.england));
        spinnerItems.add(new SpinnerItem("CHF", "Swiss Franc",R.drawable.switzerland));
        spinnerItems.add(new SpinnerItem("CAD", "Canadian Dollar",R.drawable.canada));
        spinnerItems.add(new SpinnerItem("AUD", "Australian Dollar",R.drawable.australia));
        spinnerItems.add(new SpinnerItem("ZAR", "South African Dollar",R.drawable.southafrica));
        spinnerItems.add(new SpinnerItem("INR", "Indian Rupee",R.drawable.india));
        spinnerItems.add(new SpinnerItem("IRR", "Iranian Rial",R.drawable.iran));
        spinnerItems.add(new SpinnerItem("HKD", "Hong Kong Dollar",R.drawable.honkong));
        spinnerItems.add(new SpinnerItem("JMD", "Jamaican dollar",R.drawable.jamaica));
        spinnerItems.add(new SpinnerItem("KWD", "Kuwaiti Dinar",R.drawable.kuwait));
        spinnerItems.add(new SpinnerItem("MYR", "Malaysian Ringgit",R.drawable.malaysia));
        spinnerItems.add(new SpinnerItem("NGN", "Nigerian Naira",R.drawable.nigeria));
        spinnerItems.add(new SpinnerItem("QAR", "Qatari Rial",R.drawable.qatar));
        spinnerItems.add(new SpinnerItem("RUB", "Russian Rubble",R.drawable.russia));
        spinnerItems.add(new SpinnerItem("SAR", "Saudi Riyal",R.drawable.saudi));
        spinnerItems.add(new SpinnerItem("KRW", "South Korea Won",R.drawable.saudi));
        spinnerItems.add(new SpinnerItem("GHS", "Ghanian Cedi",R.drawable.ghana));
        return spinnerItems;
    }
    public ArrayList<SpinnerItem> getCoinSpinnerList() {
        ArrayList<SpinnerItem> spinnerItems = new ArrayList<>();
        spinnerItems.add(new SpinnerItem("BTC","Bitcoin",R.drawable.bitcoin));
        spinnerItems.add(new SpinnerItem("ETH" ,"Ethereum",R.drawable.ethereum));
        return spinnerItems;
    }

    //close cursor after reading from it to free memory
    private void closeCursor(){
        if (cursor!= null ) {
            cursor.close();
        }
    }
}
