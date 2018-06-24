package com.example.sarveshagrawal.edison.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sarvesh Agrawal on 24-06-2018.
 */

public class SqlDatabase extends SQLiteOpenHelper {

    public static final String DB_NAME = "cordinates_db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "table_name";
    public static final String ID = "id";
    public static final String LONGITUDE ="longitude";
    public static final String LATITUDE ="latitude";
    public static boolean isPause = false;
    public static boolean isClicked = false;

    private static volatile SqlDatabase instance;


    public SqlDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static SqlDatabase getInstance(final Context context) {
        SqlDatabase localInstance = instance;
        if (localInstance == null) {
            synchronized (SqlDatabase.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SqlDatabase(context);
                }
            }
        }
        return localInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + TABLE_NAME + "("
                + ID + " integer PRIMARY KEY AUTOINCREMENT, "
                + LATITUDE + " text, "
                + LONGITUDE + " text " + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertCodinates(double lat, double lng){
        SQLiteDatabase database = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(LATITUDE,""+lat);
            values.put(LONGITUDE,""+lng);
            database.insert(TABLE_NAME, null, values);
    }

    public String getCordinates(){
        String location ="";
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (c.moveToFirst()) {
            do {
                location = c.getString(c.getColumnIndex(LATITUDE))+","+c.getString(c.getColumnIndex(LONGITUDE));
            } while (c.moveToNext());
        }
        c.close();
        return location;
    }
}
