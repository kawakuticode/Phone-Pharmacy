package com.code.kawakuti.phonepharmacy.database;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by Russelius on 13/03/16.
 */
public class ExportDataBaseToFile extends AsyncTask<String, Void, Boolean> {

    private Context myContext;
    private SQLiteOpenHelper db;
    private ProgressDialog dialog;
    private String file_name;
    private String data_base_name;


    public ExportDataBaseToFile(Context context, SQLiteOpenHelper database, String file_name, DataBaseMedsHandler.MedColumns data_base_name) {
        this.myContext = context;
        this.file_name = file_name;
        this.db = database;
        this.data_base_name = data_base_name.toString();
    }


    @Override
    protected void onPreExecute() {
        this.dialog = new ProgressDialog(myContext);
        this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.dialog.setMessage("Please wait while exporting database...");
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    protected Boolean doInBackground(final String... args) {
        String state = Environment.getExternalStorageState();
        String select = "select * from " + data_base_name;

        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        } else {
            File exportDir = new File(Environment.getExternalStorageDirectory(), "/PhoneParmacy/");

            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            try {
                File file = new File(exportDir, file_name);
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                //db = new DataBaseMedsHandler(myContext);

                Cursor curCSV = db.getReadableDatabase().rawQuery(select, null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while (curCSV.moveToNext()) {
                    String arrStr[] = null;
                    String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
                    for (int i = 0; i < curCSV.getColumnNames().length; i++) {
                        mySecondStringArray[i] = curCSV.getString(i);
                    }
                    csvWrite.writeNext(mySecondStringArray);
                }
                csvWrite.close();
                curCSV.close();
                db.close();
                return true;

            } catch (IOException e) {
                Log.e("EXPORT", e.getMessage(), e);
                return false;
            }
        }
    }

    protected void onPostExecute(final Boolean success) {
        this.dialog.dismiss();
        if (success) {
            Toast.makeText(myContext, "Export successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(myContext, "Export failed", Toast.LENGTH_SHORT).show();
        }
    }
}

