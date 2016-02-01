package com.code.kawakuti.phonepharmacy;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
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


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        View rootView = inflater.inflate(R.layout.memorecips, container, false);


        addAlarms();
        memRecipsView = (ListView) rootView.findViewById(R.id.listmemo);
        MemoRecipsAdapter memoAdapter = new MemoRecipsAdapter(memoList, getContext());
        memRecipsView.setAdapter(memoAdapter);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMemoDialog().show();

            }
        });


        // Get the arguments that was supplied when
        // the fragment was instantiated in the
        // CustomPagerAdapter
       /* Bundle args = getArguments();
        ((TextView) rootView.findViewById(R.id.textView2)).setText("Memo Recips Page");*/

        return rootView;
    }

    public void addAlarms() {
        memoList = new ArrayList<>();
        Memo tmp;
        int i = 0;
        while (i < 10) {
            tmp = new Memo();
            TextClock c = new TextClock(this.getContext());
            tmp.setMedicine_to_take(i + "drugs axx");
            tmp.setTextClock(c);
            memoList.add(tmp);
            i++;
        }
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


        /*expiration_date_picker.init(mYear, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(year, monthOfYear + 1, dayOfMonth);
                Log.d(TAG, mCalendar.get(Calendar.DAY_OF_MONTH) + " "
                        + mCalendar.get(Calendar.MONTH) + " " + mCalendar.get(Calendar.YEAR));
            }
        });*/


        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkFields(memo_name)) {

           /*         Med tmp = new Med();
                    tmp.setName(med_name.getText().toString());
                    tmp.setDescription(med_description.getText().toString());
                    tmp.setExpireDate(mCalendar.getTime());
                    tmp.setSrcImage(img_source);

                    if (db.addMed(tmp) > 0) {

                        builder.dismiss();
                        Toast.makeText(getContext(), "Inserted with Sucess", Toast.LENGTH_SHORT).show();
                        displayMeds(listMeds);
                    }*/
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

    private boolean checkFields(EditText memo_name) {
        return false;
    }


}


