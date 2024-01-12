package com.example.tabslayout.Fragements;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabslayout.Adapter.SelectAdapter;
import com.example.tabslayout.Database.Sqlitedatabase;
import com.example.tabslayout.Javaclass.SelectedVideoItem;
import com.example.tabslayout.Javaclass.VideoClickListener;
import com.example.tabslayout.R;
import com.example.tabslayout.Videomodel;
import com.example.tabslayout.databinding.FragmentSelectFregmentBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;


public class select_fregment extends BottomSheetDialogFragment implements SelectedVideoItem,View.OnClickListener {
    public static final String TAG = "SelectItemFragment";
    SelectAdapter selectAdapter ;
    FragmentSelectFregmentBinding binding ;
    private ArrayList<Videomodel> videoFolder = new ArrayList<>();
    Sqlitedatabase sqlitedatabase;
    VideoClickListener listener ;
    String table;
    public select_fregment() {
        // Required empty public constructor
    }

    public select_fregment(String table){
        this.table = table;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_select_fregment, container, false);
        binding = FragmentSelectFregmentBinding.inflate( inflater );
        return binding.getRoot() ;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onStart() {
        super.onStart();
        videoFolder = getAllVideos(requireContext());

        if(videoFolder != null && videoFolder.size() > 0)

        {
            selectAdapter = new SelectAdapter(videoFolder, this.requireActivity(), this);

            binding.Recycler.setAdapter ( selectAdapter);
            binding.Recycler.setLayoutManager ( new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

            if (videoFolder != null && videoFolder.size() > 0){
                selectAdapter.addAll(videoFolder);
            }

        }else {
            Toast.makeText(getContext(), "there is no video files here", Toast.LENGTH_SHORT).show();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ArrayList<Videomodel> getAllVideos(Context context) {
        ArrayList<Videomodel> tempVideoFiles = new ArrayList<>();

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
//        String selection = MediaStore.Video.Media.DATA + " like?";
//        String[] selectionArgs = new String[]{"%" + name + "%"};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, orderBy);


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

                //this method convert 1204 in 1MB
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

                Videomodel files = new Videomodel(id, path, title, duration_formatted,
                        resolution, human_can_read,
                        disName, width_height, resolution, bucket_display_name, width_height, mimeType, dateAdded);

                tempVideoFiles.add(files);

            }
            cursor.close();
        }
        return tempVideoFiles;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.ivDone.setOnClickListener(this);
        binding.ivClose.setOnClickListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof VideoClickListener){
            listener = (VideoClickListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " ");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == binding.ivClose.getId()){

            listener.close();

        }else if (v.getId() == binding.ivDone.getId()){
            selectAdapter.addToDataBase(table);

            listener.notification();

        }

    }

    @Override
    public void VideoSelectCount(int Count) {
        if (Count <= 0 ){
            binding.ivDone.setVisibility(View.GONE);
        }else {
            binding.ivDone.setVisibility(View.VISIBLE);
        }
        binding.Text.setText(String.format("%d Selected", Count));

    }

    @Override
    public void onVideoLongPress() {

    }

    @Override
    public void onOptionClick(Videomodel videoClass, int position) {

    }

    @Override
    public void onBind(ImageView imageView, Videomodel videoClass) {

    }

    @Override
    public void onItemClick(ImageView imageView, int position, ArrayList<Videomodel> videoClasses, File file) {

    }



}