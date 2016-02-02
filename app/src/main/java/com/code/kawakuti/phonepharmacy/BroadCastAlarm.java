package com.code.kawakuti.phonepharmacy;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

/**
 * Created by Russelius on 02/02/16.
 */
public class BroadCastAlarm extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("medicine");
        /*TakeMedicineAlarm.getTextTimeView().setText("Enough Rest. Do Work Now!");
       TakeMedicineAlarm.getTextMedicine().setText("Enough Rest. Do Work Now!");*/

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        ringtone.play();

        Intent i = new Intent(context, TakeMedicineAlarm.class) ;
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

        Toast.makeText(context, message,
                Toast.LENGTH_SHORT).show();
    }
}
