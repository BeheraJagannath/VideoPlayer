package com.example.tabslayout.Adapter;

import static com.example.tabslayout.Adapter.RecentAdapter.getDuration;
import static com.example.tabslayout.Adapter.RecentAdapter.getId;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tabslayout.Database.HistoryDataBase;
import com.example.tabslayout.Javaclass.Utils;
import com.example.tabslayout.PlayerActivity;
import com.example.tabslayout.R;
import com.example.tabslayout.Videomodel;
import com.example.tabslayout.databinding.HistoryLayoutBinding;

import java.io.File;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.viewHolder> {
    ArrayList<Videomodel>hist = new ArrayList<>();
    ArrayList<Videomodel>nos =new ArrayList<>();
    Activity activity ;
    HistoryDataBase historyDataBase ;
    public HistoryAdapter(Activity activity){
        this .activity = activity;
        this .hist = hist ;
        this.historyDataBase = new HistoryDataBase(activity);

    }
    @NonNull
    @Override
    public HistoryAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryAdapter.viewHolder(DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.history_layout,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.viewHolder holder, @SuppressLint("RecyclerView") int position) {
        File file = new File(hist.get(position).getPath());

        viewHolder holders = (viewHolder) holder;

        if (file.exists()) {
            Glide.with(activity)
                    .load(file.getPath())
                    .into(holders.binding.historyImage ) ;
            holders.binding.histName.setText ( hist.get(position).getTitle() );
            holders . binding.histDuration.setText ( Utils.getDurationBreakdown ( getDuration(activity, (file.getAbsolutePath() ))));



        }
        holder.itemView .setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent( activity , PlayerActivity.class ) ;
                intent.putExtra("position",position);
                intent.putExtra("title",hist.get (position).getTitle());
                intent.putExtra("time",hist.get (position).getDuration());
                Bundle bundle=new Bundle ();
                bundle.putSerializable ("sel",hist);
                intent.putExtras ( bundle );
                historyDataBase.setHis(getId(activity, file.getPath()));
                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                activity.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {

        return hist.size();
    }
    public void addAll(ArrayList<Videomodel> list) {

        nos = new ArrayList<>();
        hist = list;
        nos.addAll(list);
        notifyDataSetChanged();

    }

    public class viewHolder extends RecyclerView.ViewHolder {
        HistoryLayoutBinding binding;
        public viewHolder(@NonNull HistoryLayoutBinding itemView) {
            super(itemView .getRoot());
            binding = itemView ;
        }
    }
}
