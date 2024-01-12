package com.example.tabslayout.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tabslayout.Database.HistoryDataBase;
import com.example.tabslayout.Database.Sqlitedatabase;
import com.example.tabslayout.Fragements.History_fragment;
import com.example.tabslayout.Javaclass.SelectedVideoItem;
import com.example.tabslayout.Javaclass.Utils;
import com.example.tabslayout.PlayerActivity;
import com.example.tabslayout.R;
import com.example.tabslayout.Videomodel;
import com.example.tabslayout.databinding.HistoryFragmentBinding;
import com.example.tabslayout.databinding.RecentLayoutBinding;

import java.io.File;
import java.util.ArrayList;

public class RecentAdapter  extends RecyclerView.Adapter< RecyclerView.ViewHolder> {
    Activity activity;
    ArrayList<Videomodel> object = new ArrayList<>();
    ArrayList<Videomodel> mArray = new ArrayList<>();
    Sqlitedatabase sqlitedatabase;
    HistoryDataBase historyDataBase;
    HistoryFragmentBinding binding;
    SelectedVideoItem selectedVideoItem;
    int From;
    boolean inActionOn;

    public RecentAdapter(ArrayList<Videomodel> recent, Activity activity, SelectedVideoItem selectedVideoItem, int from
            , HistoryFragmentBinding binding){
        this.activity = activity;
        this.sqlitedatabase = new Sqlitedatabase(activity);
        this.historyDataBase = new HistoryDataBase(activity);
        this.binding = binding;

        this.selectedVideoItem = selectedVideoItem;
        this.From = from;


    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecentAdapter.ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.recent_layout,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        File file = new File(object.get(position).path);

        RecentAdapter.ViewHolder holders = (RecentAdapter.ViewHolder) holder;

        Videomodel videoModel = object.get(position);

        if (inActionOn) {
            holders.binding.mSelect.setImageResource(R.drawable.chkbx);

            holders.binding.mSelect.setVisibility(View.VISIBLE);

            selectedVideoItem.onBind(holders.binding.mSelect, videoModel);

        } else {
            holders.binding.mSelect.setImageResource(R.drawable.chkbx_done);
            holders.binding.mSelect.setVisibility(View.GONE);

        }

        if (file.exists()) {

            Glide.with(activity)
                    .load(file.getPath())
                    .into( holders.binding.thumbnail);
            holders.binding.videoTitle.setText(object.get(position).getTitle());
            holders.binding.videoDuration.setText(Utils.getDurationBreakdown ( getDuration(activity, (file.getAbsolutePath() ))));
            holders.binding.videoSize.setText(Utils.getSize(file.length()));
            holders.binding.videoTitle.setSelected(true);


        }
        holder.itemView.setOnLongClickListener(v -> {

            selectedVideoItem.onVideoLongPress();

            return false;
        });
        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedVideoItem.onItemClick ( holders.binding.mSelect, position, object,file );
            }
        });

    }

    @Override
    public int getItemCount() {
        
        return object.size();
    }
    public void addAll(ArrayList < Videomodel > list) {

        mArray = new ArrayList<>();
        object = list;
        mArray.addAll(list);
        notifyDataSetChanged();

    }

    public void searchedItem ( ArrayList < Videomodel > arrayList) {
        object = new ArrayList<>();
        object.addAll ( arrayList );
        notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void startAction() {
        inActionOn = true;
        notifyDataSetChanged();
    }

    public void stopAction() {
        inActionOn = false;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RecentLayoutBinding binding;

        public ViewHolder ( @NonNull RecentLayoutBinding itemView ) {
            super ( itemView.getRoot() ) ;

            binding = itemView;

        }


    }
    @SuppressLint({"SetTextI18n", "SimpleDateFormat", "Range"})

    public static String getId ( Context context, String uri ) {
        String id = null;
        Cursor cursor = context.getContentResolver().query ( MediaStore.Video.Media . EXTERNAL_CONTENT_URI, new String[]
                        { MediaStore.Video.VideoColumns._ID }, MediaStore.Images.ImageColumns.DATA + " = ?",
                new String[]{ uri }, null);

        if (cursor != null) {
            cursor.moveToFirst();
            id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID));

            cursor.close();
        }

        return id;
    }

    @SuppressLint("Range")
    public static long getDuration(Context context, String uri) {
        long duration = 0L;
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]
                        {MediaStore.Video.VideoColumns.DURATION}, MediaStore.Images.ImageColumns.DATA + " = ?",
                new String[]{uri}, null);

        if (cursor != null) {
            cursor.moveToFirst();
            duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION));
            cursor.close();
        }
        return duration;
    }
}
