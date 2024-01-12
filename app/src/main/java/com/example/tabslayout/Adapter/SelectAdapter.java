package com.example.tabslayout.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tabslayout.Database.PLDHelper;
import com.example.tabslayout.Database.Sqlitedatabase;
import com.example.tabslayout.Javaclass.SelectedVideoItem;
import com.example.tabslayout.R;
import com.example.tabslayout.Videomodel;
import com.example.tabslayout.databinding.SelectVideoBinding;

import java.io.File;
import java.util.ArrayList;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter . ViewHolder> {
    ArrayList<Videomodel> videofolder;
    private Context context;
    SelectedVideoItem selectedVideoItem;
    Sqlitedatabase sqlitedatabase;
    ArrayList<Videomodel> selectedList = new ArrayList<>();
    public SelectAdapter(ArrayList<Videomodel> videofolder, Context context, SelectedVideoItem selectedVideoItem) {

        this.videofolder = videofolder;
        this.context = context;
        sqlitedatabase = new Sqlitedatabase(context);
        this.selectedVideoItem = selectedVideoItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.select_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SelectAdapter.ViewHolder holder, int position) {
        Videomodel videoClass = videofolder.get(position);
        holder.onBind(videoClass, position);

    }

    @Override
    public int getItemCount() {
        return videofolder.size();
    }
    public void addToDataBase(String table) {
        for (Videomodel videoClass : selectedList) {
            PLDHelper.addToDb(String.valueOf(videoClass.id), table);

        }
    }

    public void addAll(ArrayList<Videomodel> videoFolder) {
        this.videofolder.addAll(videoFolder);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        SelectVideoBinding abinding;
        public ViewHolder(@NonNull SelectVideoBinding itemView) {
            super(itemView .getRoot());
            abinding = itemView;

        }

        public void onBind(Videomodel videoClass, int position) {
            if(isAvailable(videoClass)){
                abinding.check.setImageResource(R.drawable.chkbx_done);
            }else {
                abinding.check.setImageResource(R.drawable.chkbx);
            }

            Glide.with(context).load(new File(videofolder.get(position).getPath())).into(abinding.thumbnail);

            abinding.videoTitle.setText(videofolder.get(position).getTitle());


            abinding.videoDuration.setText(videofolder  .get(position).getSize());

            abinding.videoSize.setText(videofolder.get(position).getDuration());

            itemView.setOnClickListener(v -> {

                if (abinding.check.getDrawable().getConstantState().equals(itemView.getResources().getDrawable(R.drawable.chkbx_done).getConstantState())) {
                    selectedList.remove(videoClass);

                    abinding.check.setImageResource(R.drawable.chkbx);
                } else {
                    selectedList.add(videoClass);

                    abinding.check.setImageResource(R.drawable.chkbx_done);
                }
                selectedVideoItem.VideoSelectCount(selectedList.size());


            });
            abinding.check.setOnClickListener(v -> {

                if (abinding.check.getDrawable().getConstantState().equals(itemView.getResources().getDrawable(R.drawable.chkbx_done).getConstantState())) {

                    selectedList.remove(videoClass);
                    abinding.check.setImageResource(R.drawable.chkbx);

                } else {
                    selectedList.add(videoClass);

                    abinding.check.setImageResource(R.drawable.chkbx_done);
                }
                selectedVideoItem.VideoSelectCount(selectedList.size());

            });
        }

    }
    private boolean isAvailable(Videomodel videoClass) {

        for (int a = 0; a < selectedList.size(); a++) {
            if (videoClass.equals(selectedList.get(a))) {
                return true;
            }
        }
        return false;
    }
}
