package com.example.tabslayout.Javaclass;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.tabslayout.Videomodel;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static ArrayList<Videomodel> mVideosDialogList = new ArrayList<Videomodel>();
    public static String VIEW_TYPE="Grid";
    public static int COLUMN = 4;
    public static String SORTING_TYPE2 = "";
    public static boolean IsUpdateVideos=false;
    public static boolean refreshPlaylist = false;
    public static boolean refreshRecent = false;
    public static int COLUMN_TYPE = 3;
    public static boolean refreshHistory = false;
    public static boolean refreshVideos = false;
    public static final String recentTable = "RECENT_TABLE";
    public static final String favTable = "FAV_TABLE";
    public static final String playListTable = "PLAY_LIST_TABLE";
    public static boolean IsUpdate=false;
    private static String duration_formatted;

    public static boolean delete(final Context context, final File file) {

        final String where = MediaStore.MediaColumns.DATA + "=?";
        final String[] selectionArgs = new String[]{
                file.getAbsolutePath()
        };
        final ContentResolver contentResolver = context.getContentResolver();
        final Uri filesUri = MediaStore.Files.getContentUri("external");
        contentResolver.delete(filesUri, where, selectionArgs);

        if (file.exists()) {
            contentResolver.delete(filesUri, where, selectionArgs);
        }
        return !file.exists();
    }

    public static String convertTimeFromUnixTimeStamp(String date) {

        try {
            DateFormat inputFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss zz yyy");
            Date d = null;
            try {
                d = inputFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // DateFormat outputFormat = new SimpleDateFormat("MM-dd-yyyy");

            Locale loc = new Locale("en", "US");
            DateFormat outputFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, loc);
            return outputFormat.format(d);
        }catch (Exception e){
            return "not vaialble";
        }
    }
    public static String getSize(long size) {
        long kilo = 1024;
        long mega = kilo * kilo;
        long giga = mega * kilo;
        long tera = giga * kilo;
        String s = "";
        double kb = (double)size / kilo;
        double mb = kb / kilo;
        double gb = mb / kilo;
        double tb = gb / kilo;
        if(size < kilo) {
            s = size + " Bytes";
        } else if(size >= kilo && size < mega) {
            s =  String.format("%.2f", kb) + " KB";
        } else if(size >= mega && size < giga) {
            s = String.format("%.2f", mb) + " MB";
        } else if(size >= giga && size < tera) {
            s = String.format("%.2f", gb) + " GB";
        } else if(size >= tera) {
            s = String.format("%.2f", tb) + " TB";
        }
        return s;
    }

    public static String getDurationBreakdown(long millis) {

        if (millis < 0) {
            return "0";
        }
        int sec = (int) ((millis / 1000) % 60);
        int min = (int) ((millis / (1000 * 60)) % 60);
        int hrs = (int) (millis / (1000 * 60 * 60));

        if (hrs == 0) {
            duration_formatted = String.valueOf(min)
                    .concat(":".concat(String.format(Locale.UK, "%02d", sec)));
        } else {
            duration_formatted = String.valueOf(hrs)
                    .concat(":".concat(String.format(Locale.UK, "%02d", min)
                            .concat(":".concat(String.format(Locale.UK, "%02d", sec)))));
        }
        return duration_formatted;
    }
//    public static String getDurationBreakdown(long millis) {
//        if (millis < 0) {
//            return "0";
//        }
//
//        long days = TimeUnit.MILLISECONDS.toDays(millis);
//        millis -= TimeUnit.DAYS.toMillis(days);
//        long hours = TimeUnit.MILLISECONDS.toHours(millis);
//        millis -= TimeUnit.HOURS.toMillis(hours);
//        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
//        millis -= TimeUnit.MINUTES.toMillis(minutes);
//        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
//
//        StringBuilder sb = new StringBuilder(64);
//
//        if (days != 0) {
//            sb.append(days + ":");
//        }
//        if (hours != 0) {
//            sb.append(hours + ":");
//        }
//        sb.append(":"+ minutes + ":");
//        sb.append(":"+ seconds);
//
//        return (sb.toString());
//    }

    public static ArrayList<Videomodel> getDbVideos(ArrayList<String> dbVideos, Context context) {
        ArrayList<Videomodel> videoList = new ArrayList<>();

        for (String imageId : dbVideos) {
            Videomodel videoModel = getVideoById(imageId, context);
            if (videoModel.path != null)
                videoList.add(getVideoById(imageId, context));
        }

        if (videoList.size() > 0) {
            return videoList;
        } else {
            return null;
        }
    }

    public static ArrayList<Videomodel> getDbAlbumVideos(ArrayList<String> dbVideos, Context context) {
        ArrayList<Videomodel> videoList = new ArrayList<>();

        for (String imageId : dbVideos) {
            Videomodel videoModel = getVideoById(imageId, context);
            if (videoModel.path != null)
                videoList.add(getVideoById(imageId, context));
        }

        if (videoList.size() > 0) {
            return videoList;
        } else {
            return null;
        }
    }

    public static Videomodel getVideoById(String imageId, Context context) {
        Videomodel videoModel = new Videomodel();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns.TITLE,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.SIZE,
                MediaStore.Video.VideoColumns.DATE_ADDED,
                MediaStore.Video.VideoColumns.DURATION,
                MediaStore.Video.VideoColumns.WIDTH,
                MediaStore.Video.VideoColumns.HEIGHT,
                MediaStore.Video.VideoColumns.MIME_TYPE,
                MediaStore.Video.VideoColumns.BUCKET_ID,
        };
        String selection = MediaStore.Images.ImageColumns._ID + " = ?";
        String[] selectionArgs = new String[]{imageId};

        Cursor cursor = context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);



        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();

            do {

                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID)));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.TITLE));
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DISPLAY_NAME));
                String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.SIZE));
                String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_ADDED));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION));
                String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.MIME_TYPE));
                String width = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.WIDTH));
                String height = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.HEIGHT));
                long longSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.SIZE));
                String buckateId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.BUCKET_ID));

                if (path != null)
                    videoModel = new Videomodel(String.valueOf(id), path, title, fileName, size, dateAdded, duration, mimeType
                            , height, width, buckateId, mimeType, dateAdded);
                else
                    videoModel = null;

            } while (cursor.moveToNext());
            cursor.close();
        }


        return videoModel;

    }
}
