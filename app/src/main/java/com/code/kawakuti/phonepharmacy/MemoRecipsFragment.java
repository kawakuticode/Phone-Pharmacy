package com.code.kawakuti.phonepharmacy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Russelius on 31/01/16.
 */

public class MemoRecipsFragment extends Fragment {
    TextClock clock;
    TextView medTotake;

    ListView memRecipsView;
    List<Memo> memoList;
    private EditText memo_name;
    private Button bt_save, bt_cancel;
    private TimePicker textClockPicker;
    MemoRecipsAdapter memoAdapter;
    private TextClock txtclock;
    private View rootView;
    private String options[] = new String[]{"Update", "Delete", "Cancel"};
    private DataBaseMemoHandler db;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        rootView = inflater.inflate(R.layout.memorecips, container, false);


        // addAlarms();
        db = new DataBaseMemoHandler(this.getContext());
        memoList = db.getAllMemoList();
        memRecipsView = (ListView) rootView.findViewById(R.id.listmemo);
        memoAdapter = new MemoRecipsAdapter(getContext(), memoList);
        memRecipsView.setAdapter(memoAdapter);
        registerForContextMenu(memRecipsView);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        memRecipsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Memo tmpMemo = (Memo) parent.getItemAtPosition(position);
                builder.setTitle(tmpMemo.getMedicine_to_take());
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (options[which]) {
                            case "Update":
                               // editMemo(tmpMed).show();
                                break;
                            case "Delete":
                                deleteMemo(tmpMemo);
                                break;
                            case "Cancel":
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }

        });
        memRecipsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              Intent t = new Intent(getContext() , TakeMedicineAlarm.class);
                Calendar nCalendar = Calendar.getInstance();
                t.putExtra("hour",nCalendar.get(Calendar.HOUR_OF_DAY) );
                t.putExtra("minute",nCalendar.get(Calendar.MINUTE)+1 );
                startActivity(t);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMemoDialog().show();

            }
        });

        return rootView;
    }


    public Dialog addMemoDialog() {

        final Dialog builder = new Dialog(getContext());
        // Get the layout inflater
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(R.string.dialog_memo);
        builder.setContentView(inflater.inflate(R.layout.addmemo_dialog, null));

        memo_name = (EditText) builder.findViewById(R.id.name_med_to_take);
        textClockPicker = (TimePicker) builder.findViewById(R.id.timePicker);

        bt_save = (Button) builder.findViewById(R.id.save);
        bt_cancel = (Button) builder.findViewById(R.id.cancel);
        txtclock = new TextClock(getContext());

        final Calendar mCalendar = Calendar.getInstance();
       /* textClockPicker.setHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        textClockPicker.setMinute(mCalendar.get(Calendar.MINUTE));
        textClockPicker.*/

        textClockPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat mSDF = new SimpleDateFormat("hh:mm a");
                txtclock.setText(mSDF.format(mCalendar.getTime()));
            }
        });

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkFields(memo_name)) {
                    Memo tmp = new Memo();
                    tmp.setMedicine_to_take(memo_name.getText().toString());
                    tmp.setTextClock(txtclock.getText().toString());
                    Log.d("TEXTCLOCK_INPUT", tmp.toString());

                    if (db.addMemo(tmp) > 0) {
                        Toast.makeText(getContext(), "Inserted with Sucess", Toast.LENGTH_SHORT).show();
                        displayMemo(memoList);
                        builder.dismiss();
                    }
                    Toast.makeText(getContext(), "Inserted with Sucess", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "CHECK FIELDS", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Cancel Dialog ", Toast.LENGTH_SHORT).show();
                builder.cancel();
            }
        });
        return builder;
    }


    public void deleteMemo(Memo memo) {
        db.deleteEntry(memo.getId());
        displayMemo(memoList);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(memoList.get(info.position).getMedicine_to_take());
            MenuInflater inflater =(MenuInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.menu.menu_update_options, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.update:
                return true;

            case R.id.delete:
                return true;

            case R.id.cancel_update:
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }



    private void displayMemo(List<Memo> mxList) {

        mxList.clear();
        mxList.addAll(db.getAllMemoList());
        if (memoAdapter == null) {
            memoAdapter = new MemoRecipsAdapter(getContext(), mxList);
            ListView listView = (ListView) rootView.findViewById(R.id.list);
            listView.setAdapter(memoAdapter);
        } else {
            memoAdapter.notifyDataSetChanged();
        }
    }



    private boolean checkFields(EditText memo_name) {
        return true;
    }


}


