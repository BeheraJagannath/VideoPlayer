package com.example.tabslayout.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Sqlitedatabase extends SQLiteOpenHelper {
    private Context context;
    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "FavoriteButton.DB";
    private static String TABLE_NAME = "favorite_Video";
    public static String CALL_PATH = "path";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + CALL_PATH + " TEXT PRIMARY KEY)";


    public Sqlitedatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
    public ArrayList<String> getFavList() {

        ArrayList<String> favModelList;
        Cursor cursor;
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            favModelList = new ArrayList<>();
            cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        }
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
