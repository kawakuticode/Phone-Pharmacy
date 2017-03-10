package com.code.kawakuti.phonepharmacy.database;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by Russelius on 13/03/16.
 */
public class ExportDataBaseToFile extends AsyncTask<String, Void, Boolean> {

    private static Integer FIELD_TYPE_NULL = 0;
    private static Integer FIELD_TYPE_INTEGER = 1;
    private static Integer FIELD_TYPE_FLOAT = 2;
    private static Integer FIELD_TYPE_STRING = 3;
    private static Integer FIELD_TYPE_BLOB = 4;
    private Context myContext;
    private SQLiteOpenHelper db;
    private ProgressDialog dialog;
    private String file_name;
    private String table_name;


    public ExportDataBaseToFile(Context context, SQLiteOpenHelper database, String file_name, String table_name) {
        this.myContext = context;
        this.file_name = file_name;
        this.table_name = table_name;
        this.db = database;
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
        String select = "select * from " + table_name;

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
                // db = new DataBaseMedsHandler(myContext);
                Cursor curCSV = db.getReadableDatabase().rawQuery(select, null);
                //System.out.println("DATABASES -->  " + db.getDatabaseName().toString());

                csvWrite.writeNext(curCSV.getColumnNames());

                while (curCSV.moveToNext()) {
                    String arrStr[] = null;
                    String[] mySecondStringArray = new String[curCSV.getColumnNames().length];

                    for (int i = 0; i < curCSV.getColumnNames().length; i++) {
                        mySecondStringArray[i] = CursorValueToString(curCSV, i);
                    }
                    csvWrite.writeNext(mySecondStringArray);
                }
                csvWrite.close();
                curCSV.close();

                return true;
            } catch (IOException e) {
                //Log.e("EXPORT", e.getMessage(), e);
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

    public String CursorValueToString(Cursor cursor, int position) {

        String value = "";
        switch (cursor.getType(position)) {
            case Cursor.FIELD_TYPE_NULL:
                value = "";
                break;
            case Cursor.FIELD_TYPE_INTEGER:
                value = String.valueOf(cursor.getLong(position));
                //System.out.println("position  - > " + position + "type  - > " + cursor.getType(position));
                break;
            case Cursor.FIELD_TYPE_FLOAT:
                value = String.valueOf(cursor.getFloat(position));
                // System.out.println("position  - > " + position + "type  - > " + cursor.getType(position));
                break;
            case Cursor.FIELD_TYPE_STRING:
                value = cursor.getString(position);
                // System.out.println("position  - > " + position + "type  - > " + cursor.getType(position));
                break;
            case Cursor.FIELD_TYPE_BLOB:
                if (db instanceof DataBaseAlarmsHandler) {

                    try {

                        String tmp = new String(bin2String(cursor.getBlob(position)), "UTF-8");
                        value = cleanString(tmp);

                        //System.out.println(" String tmp --->  " + tmp.toString());
                        //System.out.println(" String Code   --->  " + value);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                } else {

                    value = String.valueOf(cursor.getBlob(position));
                }

                //System.out.println("position  - > " + position + "type  - > " + cursor.getType(position));
                break;
        }
        return value;
    }


    public byte[] bin2String(byte[] blob) {

        String xpt = Arrays.toString(blob);
        // System.out.println(" input --->  " + xpt);
        String[] byteValues = xpt.substring(1, xpt.length() - 1).split(",");

        byte[] bytes = new byte[byteValues.length];
        for (int i = 0; i < byteValues.length; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        return bytes;
    }

    public String cleanString(String x) {
        return x.substring(x.indexOf("xpt") + 2, x.length()).replaceAll("\\W", "").replace("t", "").replace("q", ",");
    }

}