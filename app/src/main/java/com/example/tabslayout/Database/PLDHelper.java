package com.example.tabslayout.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.tabslayout.Javaclass.CreatePlymodel;
import com.example.tabslayout.Javaclass.Utils;
import com.example.tabslayout.Videomodel;

import java.util.ArrayList;

public class PLDHelper extends SQLiteOpenHelper {

    private static PLDHelper mInstance = null;
    private Context context;
    private static final String DB_NAME = "NEW_VIDEO_PLAYER.DB";
    private static final int DB_VERSION = 1;
    private static final String ID = "ID";
    private static final String PLY_LIST_NAME = "PLY_LIST_NAME";
    private static final String PLY_SIZE = "PLY_SIZE";
    private static final String TABLE_NAME = "TABLE_NAME";


    public static PLDHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PLDHelper(context);
        }

        return mInstance;
    }

    public PLDHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Utils.recentTable + " (" + ID + " TEXT PRIMARY KEY )");
        db.execSQL("CREATE TABLE " + Utils.favTable + " (" + ID + " TEXT PRIMARY KEY )");
        db.execSQL("CREATE TABLE " + Utils.playListTable + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PLY_LIST_NAME + " TEXT," + TABLE_NAME + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void createPlaylistTable(String tableName) {
        mInstance.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (" + ID + " TEXT PRIMARY KEY )");
    }

    public static void addToDb(String id, String table) {
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        PLDHelper.mInstance.getWritableDatabase().insert(table, null, cv);
    }

    public static boolean deleteVideo(String id, String table) {
        return mInstance.getWritableDatabase().delete(table, ID + "=?", new String[]{id}) > 0;
    }

    public static boolean isAvailable(String id, String table) {
        Cursor cursor = mInstance.getReadableDatabase().rawQuery("SELECT * FROM " + table + " WHERE " + ID + "=?", new String[]{id}, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static ArrayList<String> getAllVideos(String table) {

        SQLiteDatabase db = mInstance.getReadableDatabase();
        ArrayList<String> recentVideo = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + table, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {
                recentVideo.add(cursor.getString(0));
            } while (cursor.moveToNext());
        } else {
            recentVideo = null;
        }

        return recentVideo;

    }

    public static void createPlaylist(CreatePlymodel plyModel) {
        ContentValues cv = new ContentValues();
        cv.put(PLY_LIST_NAME, plyModel.name);
//        cv.put(PLY_THUMB, plyModel.thumb);
//        cv.put(PLY_SIZE, plyModel.size);
        cv.put(TABLE_NAME, plyModel.table);

        mInstance.getWritableDatabase().insert(Utils.playListTable, null, cv);
    }
    public static ArrayList<CreatePlymodel> getAllPlayList() {
        ArrayList<CreatePlymodel> plyList = new ArrayList<>();
        Cursor cursor = mInstance.getReadableDatabase().rawQuery("SELECT * FROM " + Utils.playListTable, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {
                plyList.add(new CreatePlymodel(
                        cursor.getString(1),
                        cursor.getString(2)
                ));
            } while (cursor.moveToNext());
        } else {
            plyList = null;
        }
        return plyList;
    }



    public static void updatePlayList(String name, String table) {
        ContentValues cv = new ContentValues();
        cv.put(PLY_LIST_NAME, name);
//        cv.put(PLY_THUMB, thumb);
        cv.put(TABLE_NAME, table);
        mInstance.getWritableDatabase().update(Utils.playListTable, cv, TABLE_NAME + "=?", new String[]{table});
    }


    public static int getPlayListSize(String table) {
        Cursor cursor = mInstance.getReadableDatabase().rawQuery("SELECT * FROM " + Utils.playListTable + " WHERE " + TABLE_NAME + "=?", new String[]{table});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return Integer.parseInt(cursor.getString(3));
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public static int getPlayListSizeCount(String table) {

        SQLiteDatabase db = mInstance.getReadableDatabase();
        int size = 0;

        Cursor cursor = db.rawQuery("SELECT * FROM " + table, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {
                Videomodel videoById = Utils.getVideoById(cursor.getString(0), mInstance.context);
                if (videoById.path != null) {
                    size += 1;
                }
            } while (cursor.moveToNext());
        } else {
            size = 0;
        }

        return size;

    }

    public static String getPlaylistThumb(String table) {
        SQLiteDatabase db = mInstance.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + table, null);
        if (cursor != null && cursor.moveToLast()) {
            cursor.moveToLast();
            do {
                Videomodel videoById = Utils.getVideoById(cursor.getString(0), mInstance.context);
                if (videoById.path != null) {
                    return videoById.path;
                }
            } while (cursor.moveToNext());
        } else {
            return null;
        }
        return null;
    }

    public static void removePlaylist(String table) {
        mInstance.getWritableDatabase().execSQL("DELETE FROM " + table);
        mInstance.getWritableDatabase().delete(Utils.playListTable, TABLE_NAME + "=?", new String[]{table});
    }

    public static boolean isTableAva(String table) {
        Cursor cursor = mInstance.getReadableDatabase().rawQuery("SELECT * FROM " + Utils.playListTable + " WHERE " + TABLE_NAME + "=?", new String[]{table}, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
