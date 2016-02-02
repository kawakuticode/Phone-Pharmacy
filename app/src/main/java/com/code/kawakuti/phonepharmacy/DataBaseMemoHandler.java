package com.code.kawakuti.phonepharmacy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Russelius on 02/02/16.
 */
public class DataBaseMemoHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "MemoDataBase";
    // Name of table
    private static final String TABLE_NAME = "memo";


    // All Keys used in table
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "medicine_to_take";
    private static final String KEY_TIME = "time";


    private static final String CREATE_TABLE_MEMO = "CREATE TABLE "
            + TABLE_NAME + "(" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT,"
            + KEY_TIME + " TEXT);";
    private static final String TAG = " DATA BASE ";

    public DataBaseMemoHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MEMO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + CREATE_TABLE_MEMO;
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
     * @param memo
     * @return
     */

    public long addMemo(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();


        // Creating content values
        ContentValues values = new  ContentValues();
        values.put(KEY_NAME, memo.getMedicine_to_take());
        values.put(KEY_TIME, memo.getTextClock());


        // insert row in Meds table
        long insert = db.insert(TABLE_NAME, null, values);

        return insert;
    }

    /**
     * This method is used to update particular Med entry
     *
     * @param memo
     * @return
     */
    public int updateEntry(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Creating content values
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, memo.getMedicine_to_take());
        values.put(KEY_TIME, memo.getTextClock());

        // update row in Med table base on med value
        return db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[]{String.valueOf(memo.getId())});
    }

    /**
     * Used to delete particular memo entry
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
     * Used to get particular memo details
     *
     * @param id
     * @return
     */

    public Memo getMemo(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE "
                + KEY_ID + " = " + id;

        Log.d(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();
        Memo memo = new Memo();
        memo.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        memo.setMedicine_to_take(c.getString(c.getColumnIndex(KEY_NAME)));
        memo.setTextClock(c.getString(c.getColumnIndex(KEY_TIME)));

        return memo;
    }

    /**
     * Used to get detail of entire database and save in array list of data type
     * Med
     *
     * @return
     */
    public List<Memo> getAllMemoList() {
        List<Memo> memoArrayList = new ArrayList<Memo>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        Log.d(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Memo memo = new Memo();
                memo.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                memo.setMedicine_to_take(c.getString(c.getColumnIndex(KEY_NAME)));
                memo.setTextClock(c.getString(c.getColumnIndex(KEY_TIME)));

                memoArrayList.add(memo);
            } while (c.moveToNext());
        }
        return memoArrayList;
    }

}
