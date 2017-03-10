package com.code.kawakuti.phonepharmacy.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
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
import android.widget.Toast;

import com.code.kawakuti.phonepharmacy.R;
import com.code.kawakuti.phonepharmacy.adapters.MedicinesAdapter;
import com.code.kawakuti.phonepharmacy.database.DataBaseMedsHandler;
import com.code.kawakuti.phonepharmacy.database.ExportDataBaseToFile;
import com.code.kawakuti.phonepharmacy.home.AddMedicineActivity;
import com.code.kawakuti.phonepharmacy.models.Med;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import static com.code.kawakuti.phonepharmacy.database.DataBaseMedsHandler.MedColumns.TABLE_MEDICINE;

/**
 * Created by Russelius on 31/01/16.
 */
public class MedicinesFragment extends Fragment {

    private static final String TAG = "PHARMACY";
    private static final int SELECT_FILE = 1;
    private static String IMPORT_FILE_PATH = "/PhoneParmacy/medicine_data_base_back_up.csv";
    View rootView;
    private MedicinesAdapter medicineAdapter;
    private List<Med> listMeds = new ArrayList<Med>();
    private DataBaseMedsHandler db;
    private String file_name = "medicine_data_base_back_up.csv";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("file_uri", IMPORT_FILE_PATH);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            IMPORT_FILE_PATH = (String) savedInstanceState.get("file_uri");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.medicine_card, container, false);
        setHasOptionsMenu(true);
        db = new DataBaseMedsHandler(getContext());
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.cardview_medicine);
        recyclerView.setHasFixedSize(true);

        listMeds = db.getAllMedsList();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        medicineAdapter = new MedicinesAdapter(listMeds, getContext());
        recyclerView.setAdapter(medicineAdapter);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddMedicineActivity.class);
                startActivity(intent);

            }
        });

        db.close();
        return rootView;
    }
    @Override
    public void onPause() {
        DataBaseMedsHandler.deactivate();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateListMeds();
    }



    public void updateListMeds() {
        DataBaseMedsHandler.init(getContext());
        final List<Med> medicines = db.getAllMedsList();
        medicineAdapter.setMeds(medicines);
        medicineAdapter.notifyDataSetChanged();
        db.close();

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
                DataBaseMedsHandler.init(getContext());
                new ExportDataBaseToFile(getContext(), db, file_name, TABLE_MEDICINE.toString()).execute();
                break;
            case R.id.menu_item_import:
                String path = getImport_file_path(IMPORT_FILE_PATH);
                if (!path.isEmpty()) {
                    new ImportCSVMedicineFileToDataBaseTask(path).execute();
                } else
                    Toast.makeText(getActivity(), "No preview DataBase to import", Toast.LENGTH_LONG).show();


                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECT_FILE:
                if (resultCode == getActivity().RESULT_OK) {
                    // Get the Uri of the selected file
                    IMPORT_FILE_PATH = data.getData().getPath();
                    new ImportCSVMedicineFileToDataBaseTask(IMPORT_FILE_PATH).execute();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    public Date LongToDate(String longValue) {
        //  Calendar c = Calendar.getInstance();
        // DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        //df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = new Date(Long.parseLong(longValue));
            //date = new Date(TimeUnit.SECONDS.toMillis(time));
        } catch (NumberFormatException format) {
            format.printStackTrace();
        }
        return date;
    }

    private class ImportCSVMedicineFileToDataBaseTask extends AsyncTask<String, Void, Long> {
        private final ProgressDialog dialog = new ProgressDialog(getContext());
        Long result;
        private String file_path;

        public ImportCSVMedicineFileToDataBaseTask(String path) {
            this.file_path = path;
        }

        @Override
        protected Long doInBackground(String... params) {
            ParcelFileDescriptor pFileDescriptor = null;
            DataBaseMedsHandler.init(getContext());

            db.getWritableDatabase().beginTransaction();
            try {
                /*pFileDescriptor = getContext().getContentResolver().openFileDescriptor(file_path, "r");
                FileDescriptor fileDescriptor = pFileDescriptor.getFileDescriptor();*/

                CSVReader reader = new CSVReader(new FileReader(file_path));
                String[] nextLine;
                //here I am just displaying the CSV file contents, and you can store your file content into db from while loop...
                boolean first_line = true;
                while ((nextLine = reader.readNext()) != null) {
                    if (nextLine.length != 5) {
                        //Log.d("CSVParser", "Skipping Bad CSV Row");
                        continue;
                    }
                    if (first_line) {
                        first_line = false;
                        continue;
                    } else {

                        Med tmp = new Med();
                        tmp.setName(nextLine[1].trim());
                        tmp.setDescription(nextLine[2].trim());
                        tmp.setExpireDate(LongToDate(nextLine[3].trim()));
                        tmp.setSrcImage(nextLine[4].trim());
                        db.addMed(tmp);
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            db.getWritableDatabase().setTransactionSuccessful();
            db.getWritableDatabase().endTransaction();
            db.close();
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
                updateListMeds();
                Toast.makeText(getContext(), "File is built Successfully!" + "\n", Toast.LENGTH_LONG).show();
            } else {
                updateListMeds();
                Toast.makeText(getContext(), "File fail to build", Toast.LENGTH_SHORT).show();
            }
        }
    }

}