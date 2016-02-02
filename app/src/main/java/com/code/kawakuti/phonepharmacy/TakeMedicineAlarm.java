package com.code.kawakuti.phonepharmacy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TakeMedicineAlarm extends AppCompatActivity {

    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    static TextView medicine;
    static TextView time;
    int timeHour, timeMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.takemedicealarm);
        time = (TextView) findViewById(R.id.time_content);
        medicine = (TextView) findViewById(R.id.medicine_take);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent tmp = getIntent();
        setAlarm(tmp.getIntExtra("hour", 0), tmp.getIntExtra("minute", 0));
        findViewById(R.id.button_stop_alarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });
    }

    public static TextView getTextTimeView() {
        return time;

    }

    public static TextView getTextMedicine() {
        return medicine;

    }

    private void setAlarm(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Intent myIntent = new Intent(this, BroadCastAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TakeMedicineAlarm.this, 0, myIntent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm() {
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

}
