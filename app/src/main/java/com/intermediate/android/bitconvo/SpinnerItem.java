package com.intermediate.android.bitconvo;

/**
 * Created by adeoye oluwatobi on 10/21/2017.
 */
public class SpinnerItem {
    private String shortName, fullName;
    private int imageResource;

    public SpinnerItem(String shortName,String fullName,int imageResource) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.imageResource = imageResource;
    }

    public String getShortName() {
        return this.shortName;
    }
    public String getFullName() {
        return this.fullName;
    }
    public int getImageResource(){
        return this.imageResource;
    }
}
