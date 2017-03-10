package com.code.kawakuti.phonepharmacy.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.code.kawakuti.phonepharmacy.R;
import com.code.kawakuti.phonepharmacy.adapters.AlarmAdapter;
import com.code.kawakuti.phonepharmacy.database.DataBaseAlarmsHandler;
import com.code.kawakuti.phonepharmacy.database.ExportDataBaseToFile;
import com.code.kawakuti.phonepharmacy.models.Alarm;
import com.code.kawakuti.phonepharmacy.models.Alarm.Day;
import com.code.kawakuti.phonepharmacy.preferences.AlarmPreferencesAlarmActivity;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AlarmActivity extends Fragment {
    ListView alarmListView;
    View rootView;
    AlarmAdapter alarmAdapter;
    private DataBaseAlarmsHandler db_alarms;
    private String file_name = "alarm_data_base_back_up.csv";
    private String import_file_path = "/PhoneParmacy/alarm_data_base_back_up.csv";
    private List<Alarm> alarmList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.alarm_card, container, false);
        DataBaseAlarmsHandler.init(getContext());
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.cardview_alarm);
        recyclerView.setHasFixedSize(true);
        setHasOptionsMenu(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        db_alarms = new DataBaseAlarmsHandler(this.getContext());

        alarmList = DataBaseAlarmsHandler.getAllAlarms();


        alarmAdapter = new AlarmAdapter(this, alarmList, this.getContext());
        recyclerView.setAdapter(alarmAdapter);
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
        final List<Alarm> alarms = DataBaseAlarmsHandler.getAllAlarms();
        alarmAdapter.setAlarms(alarms);
        alarmAdapter.notifyDataSetChanged();
        db_alarms.close();
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
                //    Log.d("DaTABASE TABLE ", DataBaseAlarmsHandler.ALARM_TABLE);
        db_alarms=new DataBaseAlarmsHandler(getContext());
        new ExportDataBaseToFile(getContext(),db_alarms,file_name,DataBaseAlarmsHandler.ALARM_TABLE).execute();
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
        return i == 1;
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

    public Day[] arrayToDay(String days_string) {
        String[] arrayDays = days_string.split(",");
        LinkedList<Day> days = new LinkedList<>();
        for (int i = 0; i < arrayDays.length - 1; i++) {
            days.add(stringToDay(arrayDays[i]));
        }
        return days.toArray(new Day[days.size()]);
    }

    public Day stringToDay(String dayIn) {
        Day tmp = null;
        switch (dayIn) {
            case "MONDAY":
                tmp = Day.MONDAY;
                break;
            case "TUESDAY":
                tmp = Day.TUESDAY;
                break;
            case "WEDNESDAY":
                tmp = Day.WEDNESDAY;
                break;
            case "THURSDAY":
                tmp = Day.THURSDAY;
                break;
            case "FRIDAY":
                tmp = Day.FRIDAY;
                break;
            case "SATURDAY":
                tmp = Day.SATURDAY;
                break;
            case "SUNDAY":
                tmp = Day.SUNDAY;
                break;
        }
        return tmp;
    }

    private class ImportCSVAlarmsFileToDataBaseTask extends AsyncTask<String, Void, Long> {
        private final ProgressDialog dialog = new ProgressDialog(getContext());
        Long insertions = Long.valueOf(0);
        private String file_path;

        public ImportCSVAlarmsFileToDataBaseTask(String path) {
            this.file_path = path;
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.dialog.setMessage("Please wait while importing database...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Long doInBackground(String... params) {
            DataBaseAlarmsHandler.init(getContext());
            int counter = 0;
            try {
                CSVReader reader = new CSVReader(new FileReader(file_path));
                String[] nextLine;
                boolean first_line = true;
                while ((nextLine = reader.readNext()) != null) {
                    if (nextLine.length != 7) {
                        //  Log.d("CSVParser ---> ", "Bad Csv File");
                        continue;
                    }
                    if (first_line) {
                        first_line = false;
                        continue;
                    } else {
                        Alarm tmp = new Alarm();
                        tmp.setAlarmActive(integerToBoolean(ParserStringToInt(nextLine[1].trim())));
                        tmp.setAlarmTime(nextLine[2].trim());
                        tmp.setDays(arrayToDay(nextLine[3].trim()));
                        tmp.setAlarmTonePath(nextLine[4].trim());
                        tmp.setVibrate(integerToBoolean(ParserStringToInt(nextLine[5].trim())));
                        tmp.setAlarmName(nextLine[6].trim());
                        insertions += DataBaseAlarmsHandler.create(tmp);
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            DataBaseAlarmsHandler.deactivate();
            return insertions;
        }


        @Override
        protected void onPostExecute(Long check) {
            super.onPostExecute(check);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (check > 0) {
                updateAlarmList();
                Toast.makeText(getContext(), "alarm database backup  imported Successfully!" + "\n", Toast.LENGTH_LONG).show();
            } else {
                updateAlarmList();
                Toast.makeText(getContext(), "Fail to import dataBase", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

