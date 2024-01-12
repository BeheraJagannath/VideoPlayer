package com.example.tabslayout.Javaclass;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Pref {
    public static SharedPreferences mSharedPref;

    private Pref() {

    }

    public interface PrefKey {
        String Equalizer = "Equalizer";
        String FirstBand = "FirstBand";
        String SecondBand = "SecondBand";
        String ThirdBand = "ThirdBand";
        String FourBand = "FourBand";
        String FiveBand = "FiveBand";
        String bassBoost= "bassBoost";
    }

    public static void init(Context context) {
        if (mSharedPref == null)
            mSharedPref = context.getSharedPreferences("VID_PREF", Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
    }

    public static int read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).apply();
    }

    public static void write(String key, Float value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putFloat(key, value).apply();
    }

    public static Float read(String key) {
        return mSharedPref.getFloat(key, 0.5f);
    }
}
