package com.code.kawakuti.phonepharmacy.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.code.kawakuti.phonepharmacy.home.Med;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Russelius on 26/01/16.
 */
public class DataBaseMedsHandler extends SQLiteOpenHelper {
    static DataBaseMedsHandler instance = null;
    static SQLiteDatabase database = null;

    static final String DATABASE_NAME = "MedicineDataBase";
    static final int DATABASE_VERSION = 1;
    public static final String MED_TABLE = "medicine";
    public static final String COLUMN_MED_ID = "id";
    public static final String COLUMN_MED_NAME = "name";
    public static final String COLUMN_MED_DESCRIPTION = "description";
    public static final String COLUMN_MED_EXPIREDATE = "expiraDate";
    public static final String COLUMN_MED_SRCIMG = "srcImage";

    private static final String CREATE_TABLE_MEDS = "CREATE TABLE "
            + MED_TABLE + "(" + COLUMN_MED_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_MED_NAME + " TEXT,"
            + COLUMN_MED_DESCRIPTION + " TEXT,"
            + COLUMN_MED_EXPIREDATE + " DATE,"
            + COLUMN_MED_SRCIMG + " TEXT);";

    public static void init(Context context) {
        if (null == instance) {
            instance = new DataBaseMedsHandler(context);
        }
    }

    public static SQLiteDatabase getDatabase() {
        if (null == database) {
            database = instance.getWritableDatabase();
        }
        return database;
    }

    public static void deactivate() {
        if (null != database && database.isOpen()) {
            database.close();
        }
        database = null;
        instance = null;
    }


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
        values.put(COLUMN_MED_NAME, med.getName());
        values.put(COLUMN_MED_DESCRIPTION, med.getDescription());
        values.put(COLUMN_MED_EXPIREDATE, persistDate(med.getExpireDate()));
        values.put(COLUMN_MED_SRCIMG, med.getSrcImage());

        // insert row in Meds table
        long insert = db.insert(MED_TABLE, null, values);

        return insert;
    }

    /**
     * This method is used to update particular Med entry
     *
     * @param med
     * @return
     */
    public int updateEntry(Med med) {


        // Creating content values
        ContentValues values = new ContentValues();
        values.put(COLUMN_MED_NAME, med.getName());
        values.put(COLUMN_MED_DESCRIPTION, med.getDescription());
        values.put(COLUMN_MED_EXPIREDATE, persistDate(med.getExpireDate()));
        values.put(COLUMN_MED_SRCIMG, med.getSrcImage());

        // update row in Med table base on med value
        return getDatabase().update(MED_TABLE, values, COLUMN_MED_ID + " = ?",
                new String[]{String.valueOf(med.getId())});
    }


    /**
     * Used to delete particular Med entry
     *
     * @param med
     */
    public int deleteEntry(Med med) {
        return deleteEntry(med.getId());
    }


    /**
     * Used to delete particular Med entry
     *
     * @param id
     */
    public int deleteEntry(long id) {
        // delete row in students table based on id

        return getDatabase().delete(MED_TABLE, COLUMN_MED_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static int deleteAll() {
        return getDatabase().delete(MED_TABLE, "1", null);
    }

    /**
     * Used to get particular Med details
     *
     * @param id
     * @return
     */

    public Med getMed(long id) {

        String selectQuery = "SELECT * FROM " + MED_TABLE + " WHERE "
                + COLUMN_MED_ID + " = " + id;
        Med med = null;
        Cursor c = getDatabase().rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
            med = new Med();
            med.setId(c.getInt(c.getColumnIndex(COLUMN_MED_ID)));
            med.setName(c.getString(c.getColumnIndex(COLUMN_MED_NAME)));
            med.setDescription(c.getString(c.getColumnIndex(COLUMN_MED_DESCRIPTION)));
            med.setExpireDate(loadDate(c, c.getColumnIndex(COLUMN_MED_EXPIREDATE)));
            med.setSrcImage(c.getString(c.getColumnIndex(COLUMN_MED_SRCIMG)));
        }
        c.close();
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
        String selectQuery = "SELECT  * FROM " + MED_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Med med = new Med();
                med.setId(c.getInt(c.getColumnIndex(COLUMN_MED_ID)));
                med.setName(c.getString(c.getColumnIndex(COLUMN_MED_NAME)));
                med.setDescription(c.getString(c.getColumnIndex(COLUMN_MED_DESCRIPTION)));
                med.setExpireDate(loadDate(c, c.getColumnIndex(COLUMN_MED_EXPIREDATE)));
                med.setSrcImage(c.getString(c.getColumnIndex(COLUMN_MED_SRCIMG)));
                // adding to meds list
                medsArrayList.add(med);
            } while (c.moveToNext());
        }
        return medsArrayList;
    }

    public static Cursor getCursor() {
        // TODO Auto-generated method stub
        String[] columns = new String[]{
                COLUMN_MED_ID,
                COLUMN_MED_NAME,
                COLUMN_MED_DESCRIPTION,
                COLUMN_MED_EXPIREDATE,
                COLUMN_MED_SRCIMG,
        };
        return getDatabase().query(MED_TABLE, columns, null, null, null, null,
                null);
    }


}

