package com.example.tabslayout.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tabslayout.Database.PLDHelper;
import com.example.tabslayout.FavouriteActivity;
import com.example.tabslayout.Javaclass.CreatePlymodel;
import com.example.tabslayout.Javaclass.Utils;
import com.example.tabslayout.R;
import com.example.tabslayout.databinding.FavListBinding;
import com.example.tabslayout.databinding.PlaylistBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    private ArrayList<CreatePlymodel> playList = new ArrayList<>();

    public ListAdapter(Context context, ArrayList<CreatePlymodel> playlist) {
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new FavViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.fav_list,
                    parent, false));
        }else {
            return new ListViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.playlist,
                    parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        CreatePlymodel model = playList.get(position);

        switch (holder.getItemViewType()){
            case 0:
                FavViewHolder holder1 = (FavViewHolder) holder;
                holder1.binding.tFav.setText(String.format("%s Videos", PLDHelper.getPlayListSizeCount(model.table)));
                Utils.refreshPlaylist=true;



                break;
            case 1:

                ListViewHolder holders = (ListViewHolder) holder;
                holders.binding.pName.setText(model.name);
                holders.binding.pPlaylist.setText(String.format("%s Videos", PLDHelper.getPlayListSizeCount(model.table)));

                String thumb = PLDHelper.getPlaylistThumb(model.table);

                if (thumb != null) {
                    File file = new File(thumb);
                    if (file.exists()) {

                        Glide.with(context).load(file.getPath()).into(holders.binding.playlistImage);
                    }
                }

                holders.binding.playlistMenu.setOnClickListener(v -> {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
                    View view = LayoutInflater.from(context.getApplicationContext())
                            .inflate(R.layout.playlist_delete , null);
                    bottomSheetDialog.setContentView(view);
                    bottomSheetDialog.show();

                    view.findViewById(R.id.hit_rename).setOnClickListener(v1 -> {
                        RenamePlayitem( model,position);
                        bottomSheetDialog.dismiss();
                    });

                    view.findViewById(R.id.hit_delete).setOnClickListener(v1 -> {
                        PLDHelper.removePlaylist(model.table);
                        deletePlayList(position);
//                        holders.binding.mSize.setText(String.format("%d Playlist", playList.size()));
                        bottomSheetDialog.dismiss();
                    });
                });
                break;


        }

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(holder.itemView.getContext(), FavouriteActivity.class);
            i.putExtra("TABLE", model.table);
            i.putExtra("NAME", model.name);
            holder.itemView.getContext().startActivity(i);

        });

    }


    private void RenamePlayitem( CreatePlymodel playList, int position) {

        Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.rename_layout);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        dialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( android.graphics.Color.TRANSPARENT));
        dialog.show();

        EditText file_name = (EditText)dialog.findViewById(R.id.rename);
        file_name.setText(playList.name);
        dialog.findViewById(R.id.ok).setOnClickListener(v1 -> {

            String pathList = file_name.getText().toString().trim().replace(" ", "_").toUpperCase();
            boolean math = pathList.matches("[a-z_A-Z ]+");
            if (pathList.length() > 0) {
                if (math) {
                    PLDHelper.updatePlayList(file_name.getText().toString().trim(), playList.table);
                    Rename(position, new CreatePlymodel(file_name.getText().toString().trim(), playList.table));
                    this.playList.remove(position);
                    this.playList.add(position, new CreatePlymodel(file_name.getText().toString().trim(), playList.table));
                    dialog.dismiss();

                } else {
                    Toast.makeText(context, "write proper playlist name", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "write playlist name", Toast.LENGTH_SHORT).show();
            }

        });
        dialog.findViewById(R.id.rename_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }


    public void Rename(int position, CreatePlymodel model) {
        this.playList.remove(position);
        this.playList.add(position, model);
        notifyItemChanged(position);
    }

    private void deletePlayList(int position) {
        this.playList.remove(position);
        notifyItemRemoved(position);

    }


    @Override
    public int getItemCount() {

        return playList != null ? playList.size() : 0;
    }

    public void addNewPlayList(CreatePlymodel crePlyModel) {
        this.playList.add(crePlyModel);
        notifyItemInserted(playList.size() == 0 ? 0 : playList.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        else
            return 1;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addAll(ArrayList < CreatePlymodel > model) {
        this.playList.clear();
        this.playList.addAll(model);
        notifyDataSetChanged();
    }
    static class FavViewHolder extends RecyclerView.ViewHolder {

        FavListBinding binding;

        public FavViewHolder(@NonNull FavListBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;

        }

    }

    class ListViewHolder extends RecyclerView.ViewHolder {

        PlaylistBinding binding;

        public ListViewHolder(@NonNull PlaylistBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;

        }

    }
}
