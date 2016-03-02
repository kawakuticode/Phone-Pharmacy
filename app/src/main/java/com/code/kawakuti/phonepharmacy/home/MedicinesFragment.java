package com.code.kawakuti.phonepharmacy.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.code.kawakuti.phonepharmacy.R;
import com.code.kawakuti.phonepharmacy.database.DataBaseMedsHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Russelius on 31/01/16.
 */
public class MedicinesFragment extends Fragment {

    private static final int REQUEST_CAMERA = 0;
    private static final int SELECT_FILE = 1;
    private static final String TAG = "PHARMACY";
    private TextView med_name, med_description, edit_name, edit_description;
    private DatePicker expiration_date_picker, edit_expiration_date_picker;
    private String img_source;
    private Button bt_chooseFile, bt_save, bt_cancel, edit_bt_cancel, edit_bt_update;
    private Calendar mCalendar;
    private int day, month, mYear;
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
                                Log.d(TAG, "UPDATE _----xxx-> " + tmpMed.toString());
                                editMed(tmpMed).show();

                                break;
                            case "Delete":
                                deleteMed(tmpMed);
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


    public void initCalendar() {
        mCalendar = new GregorianCalendar();
        day = mCalendar.get(Calendar.DAY_OF_MONTH);
        month = mCalendar.get(Calendar.MONTH);
        mYear = mCalendar.get(Calendar.YEAR);

    }


    public void initCalendarEdit(Med med) {
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(med.getExpireDate());
        day = mCalendar.get(Calendar.DAY_OF_MONTH);
        month = mCalendar.get(Calendar.MONTH);
        mYear = mCalendar.get(Calendar.YEAR);

    }


    public boolean checkFields(TextView textView) {
        return textView.getText().length() != 0;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }



    public void displayMeds(final List <Med> medTemp) {
        medTemp.clear();
        medTemp.addAll(db.getAllMedsList());
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (medicineAdapter == null) {
                    medicineAdapter = new MedicineAdapter(getContext(), medTemp, loaderImg);
                    ListView listView = (ListView) rootView.findViewById(R.id.list);
                    listView.setAdapter(medicineAdapter);
                } else {
                    medicineAdapter.notifyDataSetChanged();
                }
                // reload content

              /*  if (alarms.size() > 0) {
                    rootView.findViewById(android.R.id.empty).setVisibility(View.INVISIBLE);
                } else {
                    rootView.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
                }*/
            }
        });
    }
    /*    if (medicineAdapter == null) {
            medicineAdapter = new MedicineAdapter(getContext(), medTemp, loaderImg);
            ListView listView = (ListView) rootView.findViewById(R.id.list);
            listView.setAdapter(medicineAdapter);
        } else {
            medicineAdapter.notifyDataSetChanged();
        }*/







    public String setButtonText(String path) {
        String result = "";

        if (path != null) {
            Log.d(TAG, "=====> path  is not null  ");
            String[] path_parts = path.split("/");
            result = path_parts[path_parts.length - 1];
        } else if (path == null) {
            Log.d(TAG, "=====> path  is NULL ");
            result = "select photo";
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data , Button bt) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContext().getContentResolver().query(selectedImageUri, projection, null, null,
                null);
        //managedQuery(selectedImageUri, projection, null, null,
        //null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String selectedImagePath = cursor.getString(column_index);
        img_source = selectedImagePath;


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


    public Dialog editMed(final Med med) {

        final Dialog builderEdit = new Dialog(getContext());

        // Get the layout inflater
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builderEdit.setTitle(R.string.dialog_edit);
        builderEdit.setContentView(inflater.inflate(R.layout.dialog_editmed, null));


        edit_name = (TextView) builderEdit.findViewById(R.id.edit_name);
        edit_description = (TextView) builderEdit.findViewById(R.id.edit_desc);
        edit_expiration_date_picker = (DatePicker) builderEdit.findViewById(R.id.edit_date);
        bt_chooseFile = (Button) builderEdit.findViewById(R.id.edit_imgSrc);
        edit_bt_cancel = (Button) builderEdit.findViewById(R.id.edit_cancel);
        edit_bt_update = (Button) builderEdit.findViewById(R.id.edit_save);


        edit_name.setText(med.getName());
        bt_chooseFile.setText(setButtonText(med.getSrcImage()));

        bt_chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        initCalendarEdit(med);
        edit_expiration_date_picker.init(mYear, month,
                day, new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mCalendar.set(year, monthOfYear + 1, dayOfMonth);
                        Log.d(TAG, mCalendar.get(Calendar.DAY_OF_MONTH) + " "
                                + mCalendar.get(Calendar.MONTH) + " " + mCalendar.get(Calendar.YEAR));
                    }
                });



        edit_bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                med.setName(edit_name.getText().toString());
                med.setDescription(edit_description.getText().toString());
                med.setExpireDate(mCalendar.getTime());
                med.setSrcImage(img_source);

                if (db.updateEntry(med) > 0) {

                    builderEdit.dismiss();
                    Toast.makeText(getContext(), "Updated with Sucess", Toast.LENGTH_SHORT).show();
                    displayMeds(listMeds);
                }
            }
        });
        edit_bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Cancel Dialog ", Toast.LENGTH_SHORT).show();
                builderEdit.cancel();
            }
        });
        return builderEdit;
    }


    public void deleteMed(Med med) {
         db.deleteEntry(med.getId());
        displayMeds(listMeds);

    }
}