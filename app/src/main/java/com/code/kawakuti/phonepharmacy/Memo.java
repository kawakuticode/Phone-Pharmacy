package com.code.kawakuti.phonepharmacy;

import android.widget.TextClock;

/**
 * Created by Russelius on 01/02/16.
 */
public class Memo {
    private TextClock textClock;
    private String medicine_to_take;

    public Memo() {
    }


    public TextClock getTextClock() {
        return textClock;
    }

    public void setTextClock(TextClock textClock) {
        this.textClock = textClock;
    }

    public String getMedicine_to_take() {
        return medicine_to_take;
    }

    public void setMedicine_to_take(String medicine_to_take) {
        this.medicine_to_take = medicine_to_take;
    }

    @Override
    public String toString() {
        return "Memo{" +
                "textClock=" + textClock +
                ", medicine_to_take='" + medicine_to_take + '\'' +
                '}';
    }
}
