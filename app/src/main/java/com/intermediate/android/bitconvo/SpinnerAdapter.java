package com.intermediate.android.bitconvo;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by adeoye oluwatobi on 10/22/2017.
 * Custom adapter that supplies data to a Spinner object
 */
public class SpinnerAdapter extends ArrayAdapter<SpinnerItem> {

    public SpinnerAdapter(Context context, int ResourceId, List<SpinnerItem> list) {
        super(context,ResourceId,list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (convertView == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.spinners_list,parent,false);
        }
        SpinnerItem currentItem = getItem(position);

        TextView shortNameView = (TextView) row.findViewById(R.id.short_name_spinner);
        TextView fullNameView = (TextView) row.findViewById(R.id.full_name_spinner);
        ImageView imageView = (ImageView) row.findViewById(R.id.image_spinner);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) shortNameView.getLayoutParams();
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        shortNameView.setText(currentItem.getShortName());
        shortNameView.setLayoutParams(lp);

        fullNameView.setVisibility(View.GONE);
        imageView.setImageResource(currentItem.getImageResource());
        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (convertView == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.spinners_list,parent,false);
        }
        SpinnerItem currentItem = getItem(position);

        TextView shortNameView = (TextView) row.findViewById(R.id.short_name_spinner);
        TextView fullNameView = (TextView) row.findViewById(R.id.full_name_spinner);
        ImageView imageView = (ImageView) row.findViewById(R.id.image_spinner);

        shortNameView.setText(currentItem.getShortName());
        fullNameView.setText(currentItem.getFullName());
        imageView.setImageResource(currentItem.getImageResource());
        return row;
    }


}
