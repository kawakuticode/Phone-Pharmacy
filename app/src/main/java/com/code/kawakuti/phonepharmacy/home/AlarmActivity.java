package com.code.kawakuti.phonepharmacy.home;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.code.kawakuti.phonepharmacy.database.ExportDataBaseToFile;
import com.code.kawakuti.phonepharmacy.home.Alarm.Day;
import com.code.kawakuti.phonepharmacy.preferences.AlarmPreferencesAlarmActivity;
import com.code.kawakuti.phonepharmacy.service.AlarmServiceBroadcastReciever;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class AlarmActivity extends Fragment implements View.OnClickListener {

    ImageButton newButton;
    ListView alarmListView;
    AlarmListAdapter alarmListAdapter;
    View rootView;
    private DataBaseAlarmsHandler db;
    private String file_name = "alarm_data_base_back_up.csv";
    private String import_file_path = "/PhoneParmacy/alarm_data_base_back_up.csv";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.alarm_activity, container, false);
        alarmListView = (ListView) rootView.findViewById(android.R.id.list);
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

    public void updateAlarmList() {
        DataBaseAlarmsHandler.init(getContext());
        final List<Alarm> alarms = DataBaseAlarmsHandler.getAll();
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
            DataBaseAlarmsHandler.init(getContext());
            DataBaseAlarmsHandler.update(alarm);
            AlarmActivity.this.callAlarmScheduleService();
            DataBaseAlarmsHandler.deactivate();
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
                Log.d("DaTABASE TABLE ", DataBaseAlarmsHandler.ALARM_TABLE);
                db = new DataBaseAlarmsHandler(getContext());
                new ExportDataBaseToFile(getContext(), db, file_name, DataBaseAlarmsHandler.ALARM_TABLE).execute();

                break;
            case R.id.menu_item_import:
                String path = getImport_file_path(import_file_path);
                if (!path.toString().isEmpty()) {
                    new ImportCSVAlarmsFileToDataBaseTask(path).execute();
                } else
                    Toast.makeText(getActivity(), "No preview DataBase to import", Toast.LENGTH_LONG).show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ImportCSVAlarmsFileToDataBaseTask extends AsyncTask<String, Void, Long> {
        private final ProgressDialog dialog = new ProgressDialog(getContext());
        private String file_path;
        Long result;

        public ImportCSVAlarmsFileToDataBaseTask(String path) {
            this.file_path = path;
        }

        @Override
        protected Long doInBackground(String... params) {
            ParcelFileDescriptor pFileDescriptor = null;

            DataBaseAlarmsHandler.init(getContext());
            //  db.getWritableDatabase().beginTransaction();
            try {
                /*pFileDescriptor = getContext().getContentResolver().openFileDescriptor(file_path, "r");
                FileDescriptor fileDescriptor = pFileDescriptor.getFileDescriptor();*/

                CSVReader reader = new CSVReader(new FileReader(file_path));
                String[] nextLine;
                //here I am just displaying the CSV file contents, and you can store your file content into db from while loop...
                boolean first_line = true;
                while ((nextLine = reader.readNext()) != null) {
                 /*   if (nextLine.length != 8) {
                        Log.d("CSVParser", "Skipping Bad CSV Row");
                        continue;
                    }*/
                    if (first_line) {
                        first_line = false;
                        continue;
                    } else {

                        Alarm tmp = new Alarm();
                        tmp.setAlarmActive(integerToBoolean(ParserStringToInt(nextLine[1].trim())));
                        tmp.setAlarmTime(nextLine[2].trim());
                        tmp.setDays(Day.values());
                        tmp.setAlarmTonePath(nextLine[4].trim());
                        tmp.setVibrate(integerToBoolean(ParserStringToInt(nextLine[5].trim())));
                        tmp.setAlarmName(nextLine[6].trim());
                        //System.out.println(" DAY STRING " + nextLine[3].trim());
                        printDays(stringToDay(nextLine[3].trim()));
                        // stringToDay(nextLine[3].trim());
                        DataBaseAlarmsHandler.create(tmp);

                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            DataBaseAlarmsHandler.deactivate();

            return Long.valueOf(-1);

        }


        @Override
        protected void onPreExecute() {
            this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.dialog.setMessage("Please wait while importing database...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(Long check) {
            super.onPostExecute(check);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (Long.valueOf(check) > 0) {
                updateAlarmList();
                Toast.makeText(getContext(), "File is built Successfully!" + "\n", Toast.LENGTH_LONG).show();
            } else {
                updateAlarmList();
                Toast.makeText(getContext(), "File fail to build", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public Integer ParserStringToInt(String i) {
        int x = 0;
        try {
            x = Integer.valueOf(i);
        } catch (NumberFormatException f) {

            f.printStackTrace();
        }
        return x;
    }

    public boolean integerToBoolean(int i) {
        return i == 1 ? true : false;
    }


    public String getImport_file_path(String file_path) {
        String state = Environment.getExternalStorageState();
        String path;

        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            path = "";
        } else {
            File import_file = new File(Environment.getExternalStorageDirectory(), file_path);
            if (!import_file.exists()) {
                path = "";
            } else {
                path = import_file.getAbsolutePath();
            }
        }
        return path;
    }

    public Day[] stringToDay(String days) {
        String[] arrayDays = days.split(",");
        Day[] daysAlarm = new Day[arrayDays.length];


        for (int i = 0; i < arrayDays.length - 1; i++) {
            //   System.out.print(Day.values()[i]);
        //    daysAlarm[i] ="Day.Monday" ;
        }
        return daysAlarm;
    }

    public void printDays(Day[] d) {
        if (d.length != 0) {
            for (Day dx : d) {
                dx.toString();
            }
        }
    }
}

