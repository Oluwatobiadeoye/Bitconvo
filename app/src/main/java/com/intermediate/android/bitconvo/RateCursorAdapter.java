package com.intermediate.android.bitconvo;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.intermediate.android.bitconvo.data.CurrencyContract.*;

/**
 * Created by adeoye oluwatobi on 10/16/2017.
 * Cursor adapter which loads data into a ListView
 */
public class RateCursorAdapter extends CursorAdapter {

    public RateCursorAdapter(Context context, Cursor c) {
        super(context,c,0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
            int forexNameIndex = cursor.getColumnIndex(WatchlistEntry.RATE_FOREX_NAME);
            int fullNameIndex = cursor.getColumnIndex(WatchlistEntry.RATE_FULL_NAME);
            int valueIndex = cursor.getColumnIndex(WatchlistEntry.VALUE);
            int percentageIndex = cursor.getColumnIndex(WatchlistEntry.PERCENTAGE);

        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        TextView forexNameView = (TextView) view.findViewById(R.id.forex_name);
        TextView fullNameView = (TextView)  view.findViewById(R.id.full_name);
        TextView valueView = (TextView) view.findViewById(R.id.value);
        TextView percentView = (TextView) view.findViewById(R.id.percent);

        String forexName = cursor.getString(forexNameIndex);
        String fullName = cursor.getString(fullNameIndex);
        String value = cursor.getString(valueIndex);
        String percentage = cursor.getString(percentageIndex);

        int textColor;
        if (!percentage.equals("%")) {
            if (percentage.contains("-")){
                percentView.setText(percentage+"%");
                textColor = ContextCompat.getColor(context,R.color.negative_value);
                percentView.setTextColor(textColor);
            } else {
                percentView.setText("+"+percentage+"%");
                textColor = ContextCompat.getColor(context,R.color.positive_value);
                percentView.setTextColor(textColor);
            }
        } else {
            percentView.setText("NA");
            percentView.setTextColor(ContextCompat.getColor(context,R.color.negative_value));
        }
        if (forexName.contains("ETH")){
            imageView.setImageResource(R.drawable.ethereum);
        } else {
            imageView.setImageResource(R.drawable.bitcoin);
        }

        forexNameView.setText(forexName);
        fullNameView.setText(fullName);
        valueView.setText(value);
    }
}
