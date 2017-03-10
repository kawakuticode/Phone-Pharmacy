package com.code.kawakuti.phonepharmacy.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.code.kawakuti.phonepharmacy.alert.AlarmAlertBroadcastReceiver;
import com.code.kawakuti.phonepharmacy.database.DataBaseAlarmsHandler;
import com.code.kawakuti.phonepharmacy.models.Alarm;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AlarmService extends Service {

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		Log.d(this.getClass().getSimpleName(), "onCreate()");
		super.onCreate();
	}

	private Alarm getNext() {
		Set<Alarm> alarmQueue = new TreeSet<Alarm>(new Comparator<Alarm>() {
			@Override
			public int compare(Alarm lhs, Alarm rhs) {
				int result = 0;
				long diff = lhs.getAlarmTime().getTimeInMillis() - rhs.getAlarmTime().getTimeInMillis();
				if (diff > 0) {
					return 1;
				} else if (diff < 0) {
					return -1;
				}
				return result;
			}
		});

		DataBaseAlarmsHandler.init(getApplicationContext());
		List<Alarm> alarms = DataBaseAlarmsHandler.getAllAlarms();

		for (Alarm alarm : alarms) {
			if (alarm.getAlarmActive())
				alarmQueue.add(alarm);
		}
		if (alarmQueue.iterator().hasNext()) {
			return alarmQueue.iterator().next();
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		DataBaseAlarmsHandler.deactivate();
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Alarm alarm = getNext();
		if (alarm != null) {

			alarm.schedule(getApplicationContext());
			Log.d("next alarm  --> " , alarm.getTimeUntilNextAlarmMessage().toString());


		} else {

			Intent myIntent = new Intent(getApplicationContext(), AlarmAlertBroadcastReceiver.class);
			myIntent.putExtra("alarm", new Alarm());

			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(pendingIntent);
		}

		return START_REDELIVER_INTENT;
	}
}