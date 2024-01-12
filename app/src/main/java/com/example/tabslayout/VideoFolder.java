package com.example.tabslayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.example.tabslayout.Adapter.VideoAdapter;
import com.example.tabslayout.Database.HistoryDataBase;
import com.example.tabslayout.Database.PLDHelper;
import com.example.tabslayout.Javaclass.SelectedVideoItem;
import com.example.tabslayout.Javaclass.Utils;
import com.example.tabslayout.databinding.ActivityVideoFolderBinding;
import com.example.tabslayout.databinding.MenuDialogBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class VideoFolder extends AppCompatActivity   implements SelectedVideoItem {
    ActivityVideoFolderBinding binding;
    VideoAdapter videoAdapter;
    private ArrayList<Videomodel> videoModelArrayList = new ArrayList<>();
    HistoryDataBase historyDataBase;

    private String name;
    private String folderTitle;
    int position;
    Uri uri;

    ArrayList<Videomodel> selectedList = new ArrayList<>();
    public boolean isActionModeEnable = false;
    private boolean isSelectAll;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_video_folder);

        name = getIntent().getStringExtra("folderName");
        folderTitle = getIntent().getStringExtra("name");

        binding.searchTitle.setText(folderTitle);

        binding.mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                a_playersAdapter.getFilter().filter(newText);

                String input = newText.toLowerCase();
                ArrayList<Videomodel> arrayList = new ArrayList<>();
                for (Videomodel usersData : videoModelArrayList ) {
                    if (usersData.getTitle().toLowerCase().contains(input)) {
                        arrayList.add(usersData);
                        videoModelArrayList = new ArrayList<>();
                        videoModelArrayList.addAll(arrayList);
                    }
                }

                videoAdapter.searchedItem(arrayList);

                return true;
            }
        });
        binding.mSearch.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.mTextLin .setVisibility(View.GONE);

            }
        });
        binding.mSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
               binding.mTextLin.setVisibility(View.VISIBLE);
                return false;
            }
        });
        binding.refr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadVideos();
                binding.refr.setRefreshing(false);
            }
        });
        binding.bak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                onBackPressed();
                Utils.refreshVideos = true;
            }
        });

        binding.ivOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOption();
            }
        });



        loadVideos();

    }
    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loadVideos() {
        videoModelArrayList = getallVideoFromFolder(this, name);
        if (name != null && videoModelArrayList.size() > 0) {

            videoAdapter = new VideoAdapter(videoModelArrayList, this, this,0, binding);
            //if your recyclerview lagging then just add this line
            binding.rec.setHasFixedSize(true);
            binding.rec.setItemViewCacheSize(20);
            binding.rec.setDrawingCacheEnabled(true);
            binding.rec.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            binding.rec.setNestedScrollingEnabled(false);

            binding.rec.setAdapter(videoAdapter);
            binding.rec.setLayoutManager(new LinearLayoutManager(this,
                    RecyclerView.VERTICAL, false));
        } else {
            Toast.makeText(this, "can't find any videos", Toast.LENGTH_SHORT).show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private ArrayList<Videomodel> getallVideoFromFolder(Context context, String name) {

        ArrayList<Videomodel> list = new ArrayList<>();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Video.Media.DATE_ADDED ;
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.RESOLUTION,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DATE_ADDED
        };
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%" + name + "%"};

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, orderBy);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                int size = cursor.getInt(3);
                String resolution = cursor.getString(4);
                int duration = cursor.getInt(5);
                String disName = cursor.getString(6);
                String bucket_display_name = cursor.getString(7);
                String width_height = cursor.getString(8);
                String mimeType = cursor.getString(9);
                String dateAdded = cursor.getString(10);

                //this method convert 1024 in 1MB
                String human_can_read = null;
                if (size < 1024) {
                    human_can_read = String.format(context.getString(R.string.size_in_b), (double) size);
                } else if (size < Math.pow(1024, 2)) {
                    human_can_read = String.format(context.getString(R.string.size_in_kb), (double) (size / 1024));
                } else if (size < Math.pow(1024, 3)) {
                    human_can_read = String.format(context.getString(R.string.size_in_mb), size / Math.pow(1024, 2));
                } else {
                    human_can_read = String.format(context.getString(R.string.size_in_gb), size / Math.pow(1024, 3));
                }

                //this method convert any random video duration like 1331533132 into 1:21:12
                String duration_formatted;
                int sec = (duration / 1000) % 60;
                int min = (duration / (1000 * 60)) % 60;
                int hrs = duration / (1000 * 60 * 60);

                if (hrs == 0) {
                    duration_formatted = String.valueOf(min)
                            .concat(":".concat(String.format(Locale.UK, "%02d", sec)));
                } else {
                    duration_formatted = String.valueOf(hrs)
                            .concat(":".concat(String.format(Locale.UK, "%02d", min)
                                    .concat(":".concat(String.format(Locale.UK, "%02d", sec)))));
                }


                Videomodel files = new Videomodel(id, path, title,
                        human_can_read, resolution, duration_formatted,
                        disName, width_height, resolution, bucket_display_name, width_height, mimeType, dateAdded);
                if (name.endsWith(bucket_display_name))
                    list.add(files);

            }
            cursor.close();
        }

        return list;
    }



    @Override
    public void VideoSelectCount(int Count) {

    }

    @Override
    public void onVideoLongPress() {
        if (!isActionModeEnable) {
            startActionMode();
        }

    }
    private void startActionMode() {
        isActionModeEnable = true;

        binding.relativeOne.setVisibility(View.GONE);
        binding.actionBar.setVisibility(View.VISIBLE);
        selectedList.clear();
        binding.tvCounter.setText(String.format("%d  Selected", 0));
        videoAdapter.startAction();
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onBackPressed() {

        if (isActionModeEnable) {
            closeActionMode();
            Utils.refreshVideos = true;
        } else{
            finish();
            Utils.refreshVideos = true;

        }

    }
    private void closeActionMode() {

        isActionModeEnable = false;
        binding.relativeOne.setVisibility(View.VISIBLE);
        binding.actionBar.setVisibility(View.GONE);
        selectedList.clear();
        binding.tvCounter.setText(String.format("%d  Selected", 0));
        videoAdapter.stopAction();
    }
    private void showOption() {

        PopupWindow popupWindow = new PopupWindow(this);
        final MenuDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.menu_dialog, null, false);
        popupWindow.setContentView(binding.getRoot());

        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.showAsDropDown(this.binding.ivOption, -40, 0);
        binding.mDelet.setOnClickListener(v -> {
            popupWindow.dismiss();
            deleteMultipleVideo();

        });

    }


    private void deleteMultipleVideo() {
        for (int i = 0; i < selectedList.size() ; i++) {

            for (int j = 0; j < videoModelArrayList.size(); j++) {

                if (selectedList.get(i).equals(videoModelArrayList.get(j))){
                    videoModelArrayList.remove(videoModelArrayList.get(j));


                }
            }
        }
        Utils.refreshRecent = false;
        closeActionMode();

    }
    @Override
    public void onOptionClick(Videomodel videoClass, int position) {

    }

    @Override
    public void onBind(ImageView imageView, Videomodel videoClass) {
        if (isAvailable(videoClass)) {
            imageView.setImageResource(R.drawable.chkbx_done);
        } else {
            imageView.setImageResource(R.drawable.chkbx);
        }

    }

    @Override
    public void onItemClick(ImageView imageView, int position, ArrayList<Videomodel> videoClasses, File file) {
        if ( isActionModeEnable ) {
            makeSelect(imageView, videoClasses.get(position));

        }else {

            if (PLDHelper.isAvailable(String.valueOf(videoClasses.get(position)), Utils.recentTable)) {
                PLDHelper.deleteVideo(String.valueOf(videoClasses.get(position)), Utils.recentTable);
            }

            PLDHelper.addToDb(String.valueOf(videoClasses.get(position)), Utils.recentTable);
            Utils.refreshRecent = false;
            Utils.refreshHistory = true;

            Intent intent=new Intent(this, PlayerActivity.class);
            intent.putExtra("position",position);
            intent.putExtra("title",videoModelArrayList.get(position).getTitle());
            intent.putExtra("time",videoModelArrayList.get(position).getDuration());
            Bundle bundle=new Bundle();
            bundle.putSerializable("sel",videoModelArrayList);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);



        }

    }
    private void makeSelect(ImageView imageView, Videomodel videoModel) {


        if (imageView.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.chkbx_done).getConstantState())) {
            selectedList.remove(videoModel);
            imageView.setImageResource(R.drawable.chkbx);
        } else {
            selectedList.add(videoModel);
            imageView.setImageResource(R.drawable.chkbx_done);
        }

        binding.tvCounter.setText(String.format("%d  Selected", selectedList.size()));
    }


    private boolean isAvailable(Videomodel videoClass) {

        for (int i = 0; i < selectedList.size(); i++) {

            if (videoClass.equals(selectedList.get(i))) {

                return true;
            }
        }
        return false;
    }
    @SuppressLint("Range")
    public static String getId(Context context, String uri) {

        String id = null;
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]
                        {MediaStore.Video.VideoColumns._ID}, MediaStore.Images.ImageColumns.DATA + " = ?",
                new String[]{uri}, null);

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID));
            }

            cursor.close();
        }

        return id;

    }
}