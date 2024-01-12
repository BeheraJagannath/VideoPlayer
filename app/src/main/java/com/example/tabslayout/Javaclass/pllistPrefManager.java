package com.example.tabslayout.Javaclass;

import android.content.Context;
import android.content.SharedPreferences;

public class pllistPrefManager {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    public pllistPrefManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MyPref",Context.MODE_PRIVATE);
    }
    public void getPlaylist(int playlist){
        editor = sharedPreferences.edit();
        editor.putString("playlist", String.valueOf(playlist));
        editor.apply();
    }

    public String setPlaylist(){
        return sharedPreferences.getString("playlist","");
    }
}
