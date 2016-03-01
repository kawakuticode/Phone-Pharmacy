package com.code.kawakuti.phonepharmacy.home;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.code.kawakuti.phonepharmacy.R;
import com.code.kawakuti.phonepharmacy.database.DataBaseAlarmsHandler;
import com.code.kawakuti.phonepharmacy.preferences.AlarmPreferencesActivity;
import com.code.kawakuti.phonepharmacy.service.AlarmServiceBroadcastReciever;

import java.util.List;

public class AlarmActivity extends Fragment implements View.OnClickListener{

	ImageButton newButton;
	ListView mathAlarmListView;
	AlarmListAdapter alarmListAdapter;
	View rootView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		rootView = inflater.inflate(R.layout.alarm_activity, container, false);
		mathAlarmListView = (ListView)rootView.findViewById(android.R.id.list);
		mathAlarmListView.setLongClickable(true);
		mathAlarmListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

				view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
				final Alarm alarm = (Alarm) alarmListAdapter.getItem(position);
				Builder dialog = new AlertDialog.Builder(getContext());
				dialog.setTitle("Delete");
				dialog.setMessage("Delete this alarm?");
				dialog.setPositiveButton("Ok", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						DataBaseAlarmsHandler.init(getContext());
						DataBaseAlarmsHandler.deleteEntry(alarm);
						AlarmActivity.this.callMathAlarmScheduleService();
						updateAlarmList();
					}
				});
				dialog.setNegativeButton("Cancel", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				dialog.show();

				return true;
			}
		});

		callMathAlarmScheduleService();

		alarmListAdapter = new AlarmListAdapter(this);
		this.mathAlarmListView.setAdapter(alarmListAdapter);
		mathAlarmListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				Alarm alarm = (Alarm) alarmListAdapter.getItem(position);
				Intent intent = new Intent(getContext(), AlarmPreferencesActivity.class);
				intent.putExtra("alarm", alarm);
				startActivity(intent);
			}

		});
		FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Toast.makeText(getContext(), "add alarm to take medicine", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getContext(), AlarmPreferencesActivity.class);
				startActivity(intent);

			}
		});
		return rootView;
	}


	@Override
	public void onPause() {
		// setListAdapter(null);
		DataBaseAlarmsHandler.deactivate();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		updateAlarmList();
	}
	
	public void updateAlarmList(){
		DataBaseAlarmsHandler.init(getContext());
		final List < Alarm> alarms = DataBaseAlarmsHandler.getAll();
		alarmListAdapter.setMathAlarms(alarms);
		
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				// reload content			
				AlarmActivity.this.alarmListAdapter.notifyDataSetChanged();
				if (alarms.size() > 0) {
					rootView.findViewById(android.R.id.empty).setVisibility(View.INVISIBLE);
				} else {
					rootView.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.checkBox_alarm_active) {
			CheckBox checkBox = (CheckBox) v;
			Alarm alarm = (Alarm) alarmListAdapter.getItem((Integer) checkBox.getTag());
			alarm.setAlarmActive(checkBox.isChecked());
			DataBaseAlarmsHandler.update(alarm);
			AlarmActivity.this.callMathAlarmScheduleService();
			if (checkBox.isChecked()) {
				Toast.makeText(getContext(), alarm.getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
			}
		}

	}

	protected void callMathAlarmScheduleService() {
		Intent mathAlarmServiceIntent = new Intent(getContext(), AlarmServiceBroadcastReciever.class);
		getContext().sendBroadcast(mathAlarmServiceIntent, null);
	}

}