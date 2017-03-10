package com.code.kawakuti.phonepharmacy.home;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.code.kawakuti.phonepharmacy.R;
import com.code.kawakuti.phonepharmacy.service.BroadCastAlarm;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TakeMedicineAlarm extends AppCompatActivity {

    static TextView medicine;
    static TextView time;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    int timeHour, timeMinute;

    public static TextView getTextTimeView() {
        return time;

    }

    public static TextView getTextMedicine() {
        return medicine;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.takemedicealarm);
        time = (TextClock) findViewById(R.id.time_content);
        medicine = (TextView) findViewById(R.id.medicine_take);
        findViewById(R.id.button_stop_alarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });
    }

    private void cancelAlarm() {
        Intent intent = new Intent(this , BroadCastAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0,
                intent, 0);
        // And cancel the alarm.
        alarmManager.cancel(sender);
        // Tell the user about what we did.
        Toast.makeText(this, "repeating_unscheduled", Toast.LENGTH_LONG).show();
    }


}

