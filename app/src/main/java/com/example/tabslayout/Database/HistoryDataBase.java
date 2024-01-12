package com.example.tabslayout.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class HistoryDataBase extends SQLiteOpenHelper {
    private Context context;
    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "HistoryButton.DB";
    private static String TABLE_NAME = "history_Video";
    public static String CALL_PATH = "path";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + CALL_PATH + " TEXT PRIMARY KEY)";

    public HistoryDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean checkWhenUserExit(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String where = CALL_PATH +" LIKE '%"+id+"%'";
        Cursor c = db.query(true, TABLE_NAME, null,
                where, null, null, null, null, null);
        if(c.getCount()>0)
            return true;
        else
            return false;

    }

    public void setHis(String path) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CALL_PATH, path);
        db.insert(TABLE_NAME, null, cv);

    }

    public boolean removeFav(String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, CALL_PATH + "=?", new String[]{path}) > 0;
    }

    public boolean getStatus(String path) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + CALL_PATH + "=?", new String[]{path}, null);
        if (cursor != null) {

            if (cursor.moveToFirst()) {

                return true;
            }else {
                return  false;
            }

        } else {
            return false;
        }
    }

    public ArrayList<String> getHisList() {

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> favModelList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {

                favModelList.add(cursor.getString(0));

            } while (cursor.moveToNext());

        } else {
            favModelList = null;
        }

        return favModelList;

    }

    public ArrayList<String> getVideosList() {

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> favModelList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {

                favModelList.add(cursor.getString(0));

            } while (cursor.moveToNext());

        } else {
            favModelList = null;
        }

        return favModelList;

    }
}
