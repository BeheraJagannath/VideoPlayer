package com.example.tabslayout.Adapter;


import android.app.Dialog;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.example.tabslayout.Database.PLDHelper;
import com.example.tabslayout.Javaclass.AddToPlay;
import com.example.tabslayout.Javaclass.CreatePlymodel;
import com.example.tabslayout.R;
import com.example.tabslayout.Videomodel;
import com.example.tabslayout.databinding.FavouriteRowBinding;
import com.example.tabslayout.databinding.PlaylistRowBinding;

import java.io.File;
import java.util.ArrayList;

public class PlayListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<CreatePlymodel> plyList;
    private RequestOptions options;
    private AddToPlay addToPlaylist;
    private Videomodel videomodel;
    private Dialog dialog;

    public PlayListAdapter(ArrayList<CreatePlymodel> plyList, AddToPlay addToPlaylist, Videomodel videomodel, Dialog dialog) {

        this.plyList = plyList;
        this.addToPlaylist = addToPlaylist;
        this.dialog = dialog;
        this.videomodel = videomodel;
        options = new RequestOptions();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new MyFavViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.favourite_row, parent, false));
        } else {
            return new DiaPlayListViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.playlist_row, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CreatePlymodel model = plyList.get(position);

        switch (holder.getItemViewType()) {
            case 0:
                PlayListAdapter.MyFavViewHolder holder1 = (PlayListAdapter.MyFavViewHolder) holder;

                holder1.binding.mSize.setText(String.format("%s Videos", PLDHelper.getPlayListSizeCount(model.table)));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addToPlaylist.onAddToFavorite(videomodel, dialog);
                    }
                });
                break;

            case 1:
                PlayListAdapter.DiaPlayListViewHolder holders = (PlayListAdapter.DiaPlayListViewHolder) holder;

                holders.binding.hitSongs.setText(model.name);
                holders.binding.hitTotal.setText(String.format("%s Videos", PLDHelper.getPlayListSizeCount(model.table)));
                String thumbnail = PLDHelper.getPlaylistThumb(model.table);

                if (thumbnail != null) {

                    File file = new File(thumbnail);

                    Glide.with(holders.itemView.getContext())
                            .load(file.getPath())
                            .apply(options.centerCrop()
                                    .skipMemoryCache(true)
                                    .priority(Priority.LOW)
                                    .format(DecodeFormat.PREFER_ARGB_8888))
                            .into(holders.binding.hitImage);
                }


                holder.itemView.setOnClickListener(v -> {
                    addToPlaylist.onAddToPlayList(videomodel, model, dialog);

                });

                break;

        }

    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        else
            return 1;
    }

    @Override
    public int getItemCount() {
        return plyList != null ? plyList.size() : 0;
    }

    public class DiaPlayListViewHolder extends RecyclerView.ViewHolder {

        PlaylistRowBinding binding;

        public DiaPlayListViewHolder(@NonNull PlaylistRowBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    class MyFavViewHolder extends RecyclerView.ViewHolder {

        FavouriteRowBinding binding;

        public MyFavViewHolder(@NonNull FavouriteRowBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
