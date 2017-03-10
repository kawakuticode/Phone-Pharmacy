
package com.code.kawakuti.phonepharmacy.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



public class AlarmServiceBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
        //Log.d("AlarmServiceBReceiver", "onReceive()");
        context.startService(new Intent(context, AlarmService.class));
    }
}


