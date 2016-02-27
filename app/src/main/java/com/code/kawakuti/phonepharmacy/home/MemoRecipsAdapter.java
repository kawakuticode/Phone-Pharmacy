package com.code.kawakuti.phonepharmacy.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.code.kawakuti.phonepharmacy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Russelius on 01/02/16.
 */
public class MemoRecipsAdapter extends BaseAdapter {


    private List<Memo> memorecips = new ArrayList<Memo>();
    private Context context;

    public MemoRecipsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return memorecips.size();
    }

    @Override
    public Object getItem(int position) {
        return memorecips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.memorerow, parent, false);
            viewHolder.txtClock = (TextView) convertView.findViewById(R.id.textClock);
            viewHolder.medToTake = (TextView) convertView.findViewById(R.id.medicine_to_take);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtClock.setText(memorecips.get(position).getTextClock());
        viewHolder.medToTake.setText(memorecips.get(position).getMedicine_to_take());

        return convertView;
    }

    public void setAlarms(List<Memo> alarms) {

        this.memorecips = alarms;
    }

    /**
     * private class Holder view and re-use
     */
    private static class ViewHolder {
        TextView txtClock;
        TextView medToTake;
    }
}
