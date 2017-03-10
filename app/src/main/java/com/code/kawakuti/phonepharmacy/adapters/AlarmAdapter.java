package com.code.kawakuti.phonepharmacy.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.code.kawakuti.phonepharmacy.R;
import com.code.kawakuti.phonepharmacy.database.DataBaseAlarmsHandler;
import com.code.kawakuti.phonepharmacy.home.AlarmActivity;
import com.code.kawakuti.phonepharmacy.models.Alarm;
import com.code.kawakuti.phonepharmacy.preferences.AlarmPreferencesAlarmActivity;
import com.code.kawakuti.phonepharmacy.service.AlarmServiceBroadcastReceiver;

import java.util.List;

/**
 * Created by russeliusernestius on 19/02/17.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmHolder> {

    public static final String ALARM_FIELDS[] = {DataBaseAlarmsHandler.COLUMN_ALARM_ACTIVE,
            DataBaseAlarmsHandler.COLUMN_ALARM_TIME, DataBaseAlarmsHandler.COLUMN_ALARM_DAYS};
    private AlarmActivity alarmActivity;
    private List<Alarm> alarms;
    private Context mContext;

    public AlarmAdapter(AlarmActivity alarmActivity, List<Alarm> database_alarm, Context context) {
        this.alarmActivity = alarmActivity;
        this.alarms = database_alarm;
        this.mContext = context;
    }

    @Override
    public AlarmAdapter.AlarmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_item, parent, false);
        return new AlarmHolder(view);
    }

    @Override
    public void onBindViewHolder(AlarmAdapter.AlarmHolder holder, int position) {
        Alarm alarm_tmp = alarms.get(position);
        if (alarm_tmp != null) {
            holder.checkBox.setChecked(alarm_tmp.getAlarmActive());
            holder.alarm_icon.setImageResource(getResId(alarm_tmp.getAlarmActive()));
            holder.medicineTake.setText(alarm_tmp.getAlarmName());
            holder.alarmeTimeView.setText(alarm_tmp.getAlarmTimeString());
            holder.alarmDaysView.setText(alarm_tmp.getRepeatDaysString());
        }
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    public AlarmAdapter getAlarmAdapter() {
        return this;
    }

    public Context getmContext() {

        return mContext;
    }

    public Integer getResId(boolean isActived) {
        return isActived ? R.mipmap.ic_alarm_on : R.mipmap.ic_alarm_off;
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;

    }


    public class AlarmHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnClickListener {
        private final MenuItem.OnMenuItemClickListener onChosenMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Alarm alarm_to = alarms.get(getAdapterPosition());
                switch (item.getItemId()) {

                    case 1:
                        Intent intent = new Intent(getmContext(), AlarmPreferencesAlarmActivity.class);
                        intent.putExtra("alarm", alarm_to);
                        getmContext().startActivity(intent);
                        break;

                    case 2:
                        DataBaseAlarmsHandler.init(getmContext());
                        DataBaseAlarmsHandler.deleteEntry(alarm_to);
                        callAlarmScheduleService(alarm_to);
                        setAlarms(DataBaseAlarmsHandler.getAllAlarms());
                        getAlarmAdapter().notifyDataSetChanged();
                        DataBaseAlarmsHandler.deactivate();
                        break;
                    case 3:
                        break;
                }
                return true;

            }
        };
        private CheckBox checkBox;
        private ImageView alarm_icon;
        private TextView medicineTake, alarmeTimeView, alarmDaysView;

        public AlarmHolder(View itemView) {
            super(itemView);

            alarm_icon = (ImageView) itemView.findViewById(R.id.alarm_img);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox_alarm);
            medicineTake = (TextView) itemView.findViewById(R.id.text_medicine);
            alarmeTimeView = (TextView) itemView.findViewById(R.id.textView_alarm_time);
            alarmDaysView = (TextView) itemView.findViewById(R.id.textView_alarm_days);

            itemView.setOnCreateContextMenuListener(this);
            checkBox.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            // gets item position
            if (v.getId() == R.id.checkBox_alarm) {

                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it

                    Alarm alarm = alarms.get(position);
                    alarm.setAlarmActive(checkBox.isChecked());
                    alarm_icon.setImageResource(getResId(alarm.getAlarmActive()));
                    DataBaseAlarmsHandler.init(getmContext());
                    DataBaseAlarmsHandler.update(alarm);
                    DataBaseAlarmsHandler.deactivate();

                    if (checkBox.isChecked()) {
                        alarm.setAlarmActive(checkBox.isChecked());
                        callAlarmScheduleService(alarm);
                        alarm_icon.setImageResource(getResId(alarm.getAlarmActive()));
                        DataBaseAlarmsHandler.init(getmContext());
                        DataBaseAlarmsHandler.update(alarm);
                        DataBaseAlarmsHandler.deactivate();
                        Toast.makeText(getmContext(), alarm.getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Update = menu.add(Menu.NONE, 1, 1, "Update");
            MenuItem Delete = menu.add(Menu.NONE, 2, 2, "Delete");
            MenuItem Cancel = menu.add(Menu.NONE, 3, 3, "Cancel");

            Update.setOnMenuItemClickListener(onChosenMenu);
            Delete.setOnMenuItemClickListener(onChosenMenu);


        }

        protected void callAlarmScheduleService(Alarm alarm) {
            mContext.sendBroadcast(new Intent(mContext, AlarmServiceBroadcastReceiver.class), null);
        }
    }
}
