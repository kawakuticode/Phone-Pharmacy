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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.code.kawakuti.phonepharmacy.database.DataBaseMedsHandler;
import com.code.kawakuti.phonepharmacy.database.ExportDataBaseToFile;
import com.code.kawakuti.phonepharmacy.preferences.AlarmPreferencesAlarmActivity;
import com.code.kawakuti.phonepharmacy.service.AlarmServiceBroadcastReciever;

import java.util.List;

import static com.code.kawakuti.phonepharmacy.database.DataBaseMedsHandler.MedColumns.TABLE_NAME;

public class AlarmActivity extends Fragment implements View.OnClickListener{

	ImageButton newButton;
	ListView alarmListView;
	AlarmListAdapter alarmListAdapter;
	View rootView;
	private DataBaseMedsHandler db;
	private String file_name = "alarm_data_base_back_up.csv";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		rootView = inflater.inflate(R.layout.alarm_activity, container, false);
		alarmListView = (ListView)rootView.findViewById(android.R.id.list);
		alarmListView.setLongClickable(true);
		setHasOptionsMenu(true);
		alarmListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
				//view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
				final Alarm alarm = (Alarm) alarmListAdapter.getItem(position);
				Builder dialog = new AlertDialog.Builder(getContext());
				dialog.setTitle("Delete");
				dialog.setMessage("Delete this alarm?");
				dialog.setPositiveButton("Ok", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DataBaseAlarmsHandler.init(getContext());
						DataBaseAlarmsHandler.deleteEntry(alarm);
						AlarmActivity.this.callAlarmScheduleService();
						DataBaseAlarmsHandler.deactivate();

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

		callAlarmScheduleService();

		alarmListAdapter = new AlarmListAdapter(this);
		this.alarmListView.setAdapter(alarmListAdapter);
		alarmListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				Alarm alarm = (Alarm) alarmListAdapter.getItem(position);
				Intent intent = new Intent(getContext(), AlarmPreferencesAlarmActivity.class);
				intent.putExtra("alarm", alarm);
				startActivity(intent);
			}

		});
		FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Toast.makeText(getContext(), "add alarm to take medicine", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getContext(), AlarmPreferencesAlarmActivity.class);
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
		DataBaseAlarmsHandler.deactivate();
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
			AlarmActivity.this.callAlarmScheduleService();
			if (checkBox.isChecked()) {
				Toast.makeText(getContext(), alarm.getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
			}
		}
	}

	protected void callAlarmScheduleService() {
		Intent alarmServiceIntent = new Intent(getContext(), AlarmServiceBroadcastReciever.class);
		getContext().sendBroadcast(alarmServiceIntent, null);
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.export_import_menu, menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_export:

				DataBaseAlarmsHandler.init(getContext());
				new ExportDataBaseToFile(getContext(), db  , file_name , TABLE_NAME).execute();

				break;
			case R.id.menu_item_import :

				break;
		}
		return super.onOptionsItemSelected(item);
	}

}