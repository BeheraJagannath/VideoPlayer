package com.example.tabslayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.tabslayout.Database.PLDHelper;
import com.example.tabslayout.Fragements.HomeFragement;
import com.example.tabslayout.Fragements.History_fragment;
import com.example.tabslayout.Fragements.Playlist_fragements;
import com.example.tabslayout.Fragements.Video_frg;
import com.example.tabslayout.Javaclass.CreatePlymodel;
import com.example.tabslayout.Javaclass.Pref;
import com.example.tabslayout.Javaclass.Utils;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tablayout;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tablayout=findViewById(R.id.tablayout);
        viewPager=findViewById(R.id.viewPager);
        tablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        PLDHelper.getInstance(this);
        Pref.init(this);

        Boolean result = checkPermission();
        if (result == true){
            StartApp();
        }

    }

    private void StartApp() {
        SharedPreferences fav_video = getSharedPreferences("fav_video", MODE_PRIVATE);
        boolean isAvailable = fav_video.getBoolean("isAvailable", false);
        if (!isAvailable) {
            PLDHelper.createPlaylist(new CreatePlymodel("My Favourite", Utils.favTable));
            Utils.refreshPlaylist = true;
            fav_video.edit().putBoolean("isAvailable", true).apply();
        }

        TabLayoutAdapter adapter = new TabLayoutAdapter(this, getSupportFragmentManager(), tablayout.getTabCount());
        tablayout.getTabAt(0).setText("Folder");
        tablayout.getTabAt(1).setText("Video");
        tablayout.getTabAt(2).setText("Playlist");
        tablayout.getTabAt(3).setText("History");
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));
//            tablayout.getTabAt(0).getIcon().setTint(getResources().getColor(R.color.purple_700, getTheme()));


        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Utils.refreshPlaylist = true ;
//                 tab.getIcon().setTint(getResources().getColor(R.color.purple_700,getTheme()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                tab.getIcon().setTint(getResources().getColor(R.color.white,getTheme()));

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.WRITE_CALENDAR)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    StartApp();
                } else {
                    //code for deny
                }
                break;
        }
    }


    @Override
    public void onBackPressed() {
        finishAffinity();

    }

    public class TabLayoutAdapter extends FragmentPagerAdapter {
        Context mContext;
        int mTotalTabs;

        public TabLayoutAdapter(Context context , FragmentManager fragmentManager , int totalTabs) {
            super(fragmentManager);
            mContext = context;
            mTotalTabs = totalTabs;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Log.d("asasas" , position + "");
            switch (position) {
                case 0:
                    return new HomeFragement();
                case 1:
                    return new Video_frg();
                case 2:
                    return new Playlist_fragements();
                case 3:
                    return new History_fragment();
                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            return mTotalTabs;

        }
    }



}
