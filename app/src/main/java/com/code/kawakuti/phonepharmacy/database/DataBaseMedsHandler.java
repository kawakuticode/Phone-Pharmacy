package com.code.kawakuti.phonepharmacy.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.code.kawakuti.phonepharmacy.home.Med;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Cr   eated by Russelius on 26/01/16.
 */
public class DataBaseMedsHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "MedsDataBase";
    // Name of table
    private static final String TABLE_NAME = "meds";


    // All Keys used in table
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_EXPIREDATE = "expiraDate";
    private static final String KEY_SRCIMG = "srcImage";


    private static final String CREATE_TABLE_MEDS = "CREATE TABLE "
            + TABLE_NAME + "(" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT,"
            + KEY_DESCRIPTION + " TEXT,"
            + KEY_EXPIREDATE + " DATE,"
            + KEY_SRCIMG + " TEXT);";

    private static final String TAG = " DATA BASE ";

    public DataBaseMedsHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MEDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + CREATE_TABLE_MEDS;
        db.execSQL(sql);
        onCreate(db);
    }

    /**
     * This method is used to set date to Long
     *
     * @param date
     * @return
     */

    public static Long persistDate(Date date) {
        return date != null ? date.getTime() : null;
    }

    /**
     * This method is used to set Long to Date
     *
     * @param cursor , index
     * @return
     */
    public static Date loadDate(Cursor cursor, int index) {
        return cursor.isNull(index) ? null : new Date(cursor.getLong(index));
    }

    /**
     * This method is used to add Med to Meds Table
     *
     * @param med
     * @return
     */

    public long addMed(Med med) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Creating content values
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, med.getName());
        values.put(KEY_DESCRIPTION, med.getDescription());
        values.put(KEY_EXPIREDATE, persistDate(med.getExpireDate()));
        values.put(KEY_SRCIMG, med.getSrcImage());

        // insert row in Meds table
        long insert = db.insert(TABLE_NAME, null, values);

        return insert;
    }

    /**
     * This method is used to update particular Med entry
     *
     * @param med
     * @return
     */
    public int updateEntry(Med med) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Creating content values
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, med.getName());
        values.put(KEY_DESCRIPTION, med.getDescription());
        values.put(KEY_EXPIREDATE, persistDate(med.getExpireDate()));
        values.put(KEY_SRCIMG, med.getSrcImage());

        // update row in Med table base on med value
        return db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[]{String.valueOf(med.getId())});
    }

    /**
     * Used to delete particular student entry
     *
     * @param id
     */
    public void deleteEntry(long id) {
        // delete row in students table based on id
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * Used to get particular student details
     *
     * @param id
     * @return
     */

    public Med getMed(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE "
                + KEY_ID + " = " + id;

        Log.d(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();
        Med med = new Med();
        med.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        med.setName(c.getString(c.getColumnIndex(KEY_NAME)));
        med.setDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
        med.setExpireDate(loadDate(c, c.getColumnIndex(KEY_EXPIREDATE)));
        med.setSrcImage(c.getString(c.getColumnIndex(KEY_SRCIMG)));

        return med;
    }

    /**
     * Used to get detail of entire com.code.kawakuti.phonepharmacy.database and save in array list of data type
     * Med
     *
     * @return
     */
    public List<Med> getAllMedsList() {
        List<Med> medsArrayList = new ArrayList<Med>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        Log.d(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Med med = new Med();
                med.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                med.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                med.setDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
                med.setExpireDate(loadDate(c, c.getColumnIndex(KEY_EXPIREDATE)));
                med.setSrcImage(c.getString(c.getColumnIndex(KEY_SRCIMG)));
                // adding to meds list
                medsArrayList.add(med);
            } while (c.moveToNext());
        }
        return medsArrayList;
    }
}

