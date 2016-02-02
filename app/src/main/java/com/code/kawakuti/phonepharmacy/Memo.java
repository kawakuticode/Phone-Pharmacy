package com.code.kawakuti.phonepharmacy;

/**
 * Created by Russelius on 01/02/16.
 */
public class Memo {

    private int id;
    private String medicine_to_take;
    private String textClock;


    public Memo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTextClock() {
        return textClock;
    }

    public void setTextClock(String textClock) {
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
        return "Memo{" + "ID" + id +
                ", textClock=" + textClock +
                ", medicine_to_take='" + medicine_to_take + '\'' +
                '}';
    }
}
