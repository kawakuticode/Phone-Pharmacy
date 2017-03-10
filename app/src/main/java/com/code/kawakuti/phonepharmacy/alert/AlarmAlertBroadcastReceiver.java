package com.code.kawakuti.phonepharmacy.alert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.code.kawakuti.phonepharmacy.database.DataBaseAlarmsHandler;
import com.code.kawakuti.phonepharmacy.models.Alarm;
import com.code.kawakuti.phonepharmacy.service.AlarmServiceBroadcastReceiver;
import com.google.android.gms.drive.DriveFile;


public class AlarmAlertBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		context.sendBroadcast(new Intent(context, AlarmServiceBroadcastReceiver.class), null);
		StaticWakeLock.lockOn(context);
		Alarm alarm = (Alarm) intent.getExtras().getSerializable("alarm");
		Intent mathAlarmAlertActivityIntent = new Intent(context, AlarmAlertActivity.class);
		mathAlarmAlertActivityIntent.putExtra("alarm", alarm);
		mathAlarmAlertActivityIntent.addFlags(DriveFile.MODE_READ_ONLY);
		context.startActivity(mathAlarmAlertActivityIntent);

	}
}
//}