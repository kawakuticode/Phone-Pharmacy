package com.code.kawakuti.phonepharmacy;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Russelius on 28/01/16.
 */
public class MedAdapter extends BaseAdapter {

    private List<Med> medicines;
    private Context context;
    private ImageLoader imageLoader;

    public MedAdapter(Context context, List<Med> medicines, ImageLoader imageL) {
        this.medicines = medicines;
        this.context = context;
        this.imageLoader = imageL;
    }

    @Override
    public int getCount() {
        return medicines.size();
    }

    @Override
    public Object getItem(int position) {
        return medicines.get(position);
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
            convertView = inflater.inflate(R.layout.medicinerow, parent, false);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.list_image);
            viewHolder.med_name = (TextView) convertView.findViewById(R.id.med_name);
            viewHolder.description = (TextView) convertView.findViewById(R.id.desc_med);
            viewHolder.exp_date = (TextView) convertView.findViewById(R.id.exp_date_);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.img.setImageBitmap(imageLoader.setMedicineImage(medicines.get(position).getSrcImage() , 100));
        viewHolder.med_name.setText(medicines.get(position).getName());
        viewHolder.description.setText(medicines.get(position).getDescription());
        if (medicines.get(position).getExpireDate() != null) {
            switch (verifyDate(medicines.get(position).getExpireDate())) {
                case 0:
                    viewHolder.exp_date.setTextColor(Color.RED);
                    viewHolder.exp_date.setText(convertStringToDate(medicines.get(position).getExpireDate()));
                    break;
                case 1:
                    viewHolder.exp_date.setText(convertStringToDate(medicines.get(position).getExpireDate()));
                    break;
            }
        }
        return convertView;
    }

    public int verifyDate(Date indate) {
        return System.currentTimeMillis() < indate.getTime() ? 1 : 0;
    }

    public String convertStringToDate(Date indate) {

        String dateString = null;
        SimpleDateFormat sdfr = new SimpleDateFormat("dd/MMM/yyyy");

        try {
            dateString = sdfr.format(indate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dateString;
    }

    /**
     * private class Holder view and re-use
     */
    private static class ViewHolder {
        ImageView img;
        TextView med_name;
        TextView description;
        TextView exp_date;
    }


}
