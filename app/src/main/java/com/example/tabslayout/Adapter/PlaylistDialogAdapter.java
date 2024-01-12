package com.example.tabslayout.Adapter;


import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabslayout.Database.PLDHelper;
import com.example.tabslayout.Javaclass.AddToPlay;
import com.example.tabslayout.R;
import com.example.tabslayout.Videomodel;
import com.example.tabslayout.databinding.PlaylistfolderBinding;

public class PlaylistDialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    private AddToPlay addToPlaylist;

    private Videomodel videomodel;
    private Dialog dialog;

    public PlaylistDialogAdapter(Videomodel videomodel, Dialog dialog, AddToPlay addToPlaylist, Activity activity) {
        this.videomodel = videomodel;
        this.dialog = dialog;
        this.addToPlaylist = addToPlaylist;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlayListView(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.playlistfolder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PlaylistDialogAdapter.PlayListView holders = (PlaylistDialogAdapter.PlayListView) holder;

        holders.binding.rv.setLayoutManager(new LinearLayoutManager(holders.itemView.getContext()));
        holders.binding.rv.setAdapter(new PlayListAdapter(PLDHelper.getAllPlayList(), addToPlaylist, videomodel, dialog));

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class PlayListView extends RecyclerView.ViewHolder {

        PlaylistfolderBinding binding;

        public PlayListView(@NonNull PlaylistfolderBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;

        }

    }
}
