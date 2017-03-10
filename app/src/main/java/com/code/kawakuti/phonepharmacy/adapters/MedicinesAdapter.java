package com.code.kawakuti.phonepharmacy.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code.kawakuti.phonepharmacy.R;
import com.code.kawakuti.phonepharmacy.database.DataBaseMedsHandler;
import com.code.kawakuti.phonepharmacy.home.UpdateMedicineActivity;
import com.code.kawakuti.phonepharmacy.models.Med;
import com.code.kawakuti.phonepharmacy.utilis.ImageLoader;
import com.code.kawakuti.phonepharmacy.utilis.PharmacyHelper;

import java.util.List;

/**
 * Created by russeliusernestius on 18/02/17.
 */

public class MedicinesAdapter extends RecyclerView.Adapter<MedicinesAdapter.MedViewHolder> {

    private List<Med> medList;
    private Context mContext;
    private int position;

    public int getPosition() {

        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public MedicinesAdapter getMedicinesAdapter() {

        return this;
    }

    public List<Med> getMedList() {
        return medList;
    }


    @Override
    public long getItemId(int position) {

        return super.getItemId(position);
    }

    public Context getmContext() {

        return mContext;
    }

    public MedicinesAdapter(List<Med> medList, Context mContext) {
        this.medList = medList;
        this.mContext = mContext;
    }

    public MedicinesAdapter() {
    }

    public void setMeds(List<Med> medicines) {
        this.medList = medicines;
    }

    @Override
    public MedicinesAdapter.MedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medicine_item, parent, false);
        return new MedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MedicinesAdapter.MedViewHolder holder, int position) {

        Med medicine = medList.get(position);

        holder.medicine_img.setImageBitmap(ImageLoader.setMedicineImage(getmContext(), medicine.getSrcImage(), 80));
        holder.med_name.setText(medicine.getName());
        holder.desc_med.setText(medicine.getDescription());
        if (medicine.getExpireDate() != null) {
            switch (PharmacyHelper.verifyDate(medicine.getExpireDate())) {
                case 0:
                    holder.exp_date.setTextColor(Color.RED);
                    holder.exp_date.setText(PharmacyHelper.convertStringToDate(medicine.getExpireDate()));
                    break;
                case 1:
                    holder.exp_date.setTextColor(Color.parseColor("#66000000"));
                    holder.exp_date.setText(PharmacyHelper.convertStringToDate(medicine.getExpireDate()));
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return medList.size();
    }

    public class MedViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private ImageView medicine_img;
        private TextView med_name;
        private TextView desc_med;
        private TextView exp_date;


        public MedViewHolder(View itemView) {
            super(itemView);
            medicine_img = (ImageView) itemView.findViewById(R.id.medicine_img);
            med_name = (TextView) itemView.findViewById(R.id.medicine_item_name);
            desc_med = (TextView) itemView.findViewById(R.id.med_desc_item);
            exp_date = (TextView) itemView.findViewById(R.id.exp_date_item);
            itemView.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {


            //new MedicinesAdapter()= (AdapterView.AdapterContextMenuInfo) menuInfo;
            MenuItem Update = menu.add(Menu.NONE, 1, 1, "Update");
            MenuItem Delete = menu.add(Menu.NONE, 2, 2, "Delete");
            MenuItem Cancel = menu.add(Menu.NONE, 3, 3, "Cancel");

            Update.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);


        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                DataBaseMedsHandler dbHandler = new DataBaseMedsHandler(getmContext());

                /// List<Med> data_med = dbHandler.getAllMedsList();

                Med tmp_med = medList.get(getAdapterPosition());


                switch (item.getItemId()) {

                    case 1:
                        Intent update_intent = new Intent(getmContext(), UpdateMedicineActivity.class);
                        update_intent.putExtra("medicine", tmp_med);
                        getmContext().startActivity(update_intent);
                        break;

                    case 2:

                        dbHandler.deleteEntry(tmp_med);
                        setMeds(dbHandler.getAllMedsList());
                        getMedicinesAdapter().notifyDataSetChanged();
                        dbHandler.close();

                        break;
                    case 3:
                        break;
                }
                return true;

            }
        };

    }
}
