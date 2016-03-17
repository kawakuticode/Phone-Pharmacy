package com.code.kawakuti.phonepharmacy.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.code.kawakuti.phonepharmacy.R;
import com.code.kawakuti.phonepharmacy.database.DataBaseMedsHandler;
import com.code.kawakuti.phonepharmacy.database.ExportDataBaseToFile;

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
    private ListView medicineListView;
    private MedicineAdapter medicineAdapter;
    private List<Med> listMeds = new ArrayList<Med>();
    private DataBaseMedsHandler db;
    private ImageLoader loaderImg;
    private String import_file_path = "/PhoneParmacy/medicine_data_base_back_up.csv";
    private String options[] = new String[]{"Update", "Delete", "Cancel"};
    View rootView;
    private String file_name = "medicine_data_base_back_up.csv";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("file_uri", import_file_path);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            import_file_path = (String) savedInstanceState.get("file_uri");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.medicinefragment, container, false);
        setHasOptionsMenu(true);
        db = new DataBaseMedsHandler(getContext());
        medicineListView = (ListView) rootView.findViewById(R.id.list);
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
        // setListAdapter(null);
        DataBaseMedsHandler.deactivate();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateListMeds();
    }

    public void deleteMed(Med med) {
        DataBaseMedsHandler.init(getContext());
        db.deleteEntry(med);
        db.close();
        updateListMeds();
    }

    public void updateListMeds() {
        DataBaseMedsHandler.init(getContext());
        final List<Med> medicines = db.getAllMedsList();
        medicineAdapter.setMedicines(medicines);
        db.close();
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                MedicinesFragment.this.medicineAdapter.notifyDataSetInvalidated();
                displayNoMedines(medicines);
            }
        });
    }

    public void displayNoMedines(List<Med> medicines) {
        if (medicines.size() > 0) {
            rootView.findViewById(R.id.empty_medicine).setVisibility(View.INVISIBLE);
        } else {
            rootView.findViewById(R.id.empty_medicine).setVisibility(View.VISIBLE);
        }

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
                String path = getImport_file_path(import_file_path);
                if (!path.toString().isEmpty()) {
                    new ImportCSVMedicineFileToDataBaseTask(path).execute();
                } else
                    Toast.makeText(getActivity(), "No preview DataBase to import", Toast.LENGTH_LONG).show();

                //    import_file_path = data.getData().getPath();
                //  new ImportCSVMedicineFileToDataBaseTask(data.getData()).execute();

                //   Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                // intent.setType("*/*");
                //intent.addCategory(Intent.CATEGORY_OPENABLE);
                // startActivityForResult(Intent.createChooser(intent, "Select File"),
                ///       SELECT_FILE);

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
                    import_file_path = data.getData().getPath();
                    new ImportCSVMedicineFileToDataBaseTask(import_file_path).execute();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class ImportCSVMedicineFileToDataBaseTask extends AsyncTask<String, Void, Long> {
        private final ProgressDialog dialog = new ProgressDialog(getContext());
        private String file_path;
        Long result;

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
                        Log.d("CSVParser", "Skipping Bad CSV Row");
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

}