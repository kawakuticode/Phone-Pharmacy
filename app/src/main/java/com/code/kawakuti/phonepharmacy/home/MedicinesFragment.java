package com.code.kawakuti.phonepharmacy.home;

import android.app.AlertDialog;
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
import android.widget.ListView;

import com.code.kawakuti.phonepharmacy.R;
import com.code.kawakuti.phonepharmacy.database.DataBaseMedsHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Russelius on 31/01/16.
 */
public class MedicinesFragment extends Fragment {

    private static final String TAG = "PHARMACY";
    private ListView medicineListView;
    private MedicineAdapter medicineAdapter;
    private List<Med> listMeds = new ArrayList<Med>();
    private DataBaseMedsHandler db;
    private ImageLoader loaderImg;
    private String options[] = new String[]{"Update", "Delete", "Cancel"};
    View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        rootView = inflater.inflate(R.layout.medicinefragment, container, false);
        db = new DataBaseMedsHandler(this.getContext());
        medicineListView = (ListView) rootView.findViewById(R.id.list);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) rootView.findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //initCalendar();
        loaderImg = new ImageLoader(this.getContext());
        listMeds = db.getAllMedsList();

        medicineAdapter = new MedicineAdapter(this.getContext(), listMeds, loaderImg);
        medicineListView.setAdapter(medicineAdapter);
        registerForContextMenu(medicineListView);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        medicineListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Med tmpMed = (Med) parent.getItemAtPosition(position);
                builder.setTitle(tmpMed.getName());

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (options[which]) {
                            case "Update":
                                Intent update_intent = new Intent(getContext(), UpdateMedicineActivity.class);
                                update_intent.putExtra("medicine", tmpMed);
                                startActivity(update_intent);
                                break;

                            case "Delete":
                                deleteMed(tmpMed);
                                updateListMeds();
                                Log.d(TAG, "DELETE  ----//> " + tmpMed.toString());

                                break;
                            case "Cancel":
                                Log.d(TAG, "CANCEL ----+> " + tmpMed.toString());
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }

        });
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddMedicineActivity.class);
                startActivity(intent);

            }
        });
        return rootView;
    }

    public void updateListMeds() {
        db = new DataBaseMedsHandler(this.getContext());
        final List <Med> medicines = db.getAllMedsList();
        medicineAdapter.setMedicines(medicines);
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                // reload content
                MedicinesFragment.this.medicineAdapter.notifyDataSetInvalidated();

            }
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(listMeds.get(info.position).getName());
            MenuInflater inflater = (MenuInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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


    public void deleteMed(Med med) {
        db.deleteEntry(med.getId());
        updateListMeds();

    }
}