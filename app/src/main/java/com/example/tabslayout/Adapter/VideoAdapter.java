package com.example.tabslayout.Adapter;

import static android.content.Intent.createChooser;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import static com.example.tabslayout.Adapter.RecentAdapter.getId;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tabslayout.Database.HistoryDataBase;
import com.example.tabslayout.Database.PLDHelper;
import com.example.tabslayout.Database.Sqlitedatabase;
import com.example.tabslayout.Javaclass.AddToPlay;
import com.example.tabslayout.Javaclass.CreatePlymodel;
import com.example.tabslayout.Javaclass.SelectedVideoItem;
import com.example.tabslayout.Javaclass.Utils;
import com.example.tabslayout.R;
import com.example.tabslayout.Videomodel;
import com.example.tabslayout.databinding.AFilesviewBinding;
import com.example.tabslayout.databinding.ActivityVideoFolderBinding;
import com.example.tabslayout.databinding.CreatePlaylistBinding;
import com.example.tabslayout.databinding.PlatlistMenuBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewViewHolders> implements AddToPlay{
    public static ArrayList<Videomodel> videosList = new ArrayList<>();
    private final Activity activity;
    HistoryDataBase historyDataBase;
    Sqlitedatabase sqlitedatabase;
    ActivityVideoFolderBinding binding;
    SelectedVideoItem selectedVideoItem;
    int From;
    boolean inActionOn;


    public VideoAdapter(ArrayList<Videomodel> videosList, Activity activity, SelectedVideoItem selectedVideoItem,
                        int from, ActivityVideoFolderBinding binding){
        this.activity= activity;
        this.binding =binding ;
        this .selectedVideoItem =selectedVideoItem ;
        this.From = from;
        this.videosList=videosList;
        this.historyDataBase=new HistoryDataBase(activity);
        this.sqlitedatabase = new Sqlitedatabase(activity);
    }



    @NonNull
    @Override
    public ViewViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewViewHolders(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.a_filesview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewViewHolders holder, int position) {
        holder.onBind(videosList.get(position), position);

    }

    @Override
    public int getItemCount() {
        return videosList.size();
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

    public void searchedItem(ArrayList<Videomodel> arrayList) {
        videosList = new ArrayList<>();
        videosList.addAll(arrayList);
        notifyDataSetChanged();

    }

    class ViewViewHolders extends RecyclerView.ViewHolder {

        AFilesviewBinding binding;

        public ViewViewHolders(@NonNull AFilesviewBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind(Videomodel videomodel, int position) {

            if (From == 0) {
                binding.videoMenu.setVisibility(View.VISIBLE);
            } else {
                binding.videoMenu.setVisibility(View.GONE);
            }

            if (inActionOn) {
                binding.mSelect.setImageResource(R.drawable.chkbx);
                binding.videoMenu.setVisibility(View.GONE);
                binding.mSelect.setVisibility(View.VISIBLE);

                selectedVideoItem.onBind(binding.mSelect, videomodel);

            } else {
                binding.mSelect.setImageResource(R.drawable.chkbx_done);
                binding.mSelect.setVisibility(View.GONE);
                if (From == 0) {
                    binding.videoMenu.setVisibility(View.VISIBLE);
                } else {
                    binding.videoMenu.setVisibility(View.GONE);
                }
            }
            File file = new File(videomodel.path);

            if (file.exists()) {

                Glide.with(itemView.getContext())
                        .load(file.getPath())
                        .into(binding.thumbnail);
                binding.videoTitle.setText(videosList.get(position).getTitle());
                binding.videoDuration.setText(videosList.get(position).getDuration());
                binding.videoSize.setText(videosList.get(position).getSize());

            }
            itemView.setOnLongClickListener(v -> {

                selectedVideoItem.onVideoLongPress();

                return false;
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    historyDataBase.setHis(getId(activity, file.getPath()));


                    selectedVideoItem.onItemClick(binding.mSelect, position, videosList ,file);
                }
            });
             binding.videoMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bottomsheet(videomodel, position);


            }

                 private void Bottomsheet(Videomodel videomodel, int position) {
                     BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetDialogTheme);
                     View bottomsheetView = LayoutInflater.from(activity).inflate(R.layout.bottom_row, null);
                     bottomSheetDialog.setContentView(bottomsheetView);
                     bottomSheetDialog.show();
                    
                     bottomsheetView.findViewById(R.id.bt_share).setOnClickListener(v -> {
                         bottomSheetDialog.dismiss();
                         shareFiles(position);
                     });
                     bottomsheetView.findViewById(R.id.bt_playlist).setOnClickListener(v -> {
                         bottomSheetDialog.dismiss();
                         showPlaylist(videomodel);
                     });
                     bottomsheetView.findViewById(R.id.bt_rename).setOnClickListener(v -> {
                         bottomSheetDialog.dismiss();
                         renameFiles(position, v);
                     });
                     bottomsheetView.findViewById(R.id.bt_delete).setOnClickListener(v -> {
                         bottomSheetDialog.dismiss();
                         deleteFiles(position, v);
                     });
                     bottomsheetView.findViewById(R.id.bt_properties).setOnClickListener(v -> {
                         bottomSheetDialog.dismiss();
                         properties(position);
                     });
                 }
             });

        }
    }
    private void properties(int position) {

        try {
            if (videosList != null && videosList.size() > 0){

                File filePath = new File(videosList.get(position).getPath());

                Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.properties);
                dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                String name = videosList.get(position).getTitle();
                String path = videosList.get(position).getPath();
                String size = videosList.get(position).getDuration();
                String duration = videosList.get(position).getSize();
                String resolution = videosList.get(position).getResolution();

                TextView tit = dialog.findViewById(R.id.detail_title);
                TextView loc = dialog.findViewById(R.id.detail_location);
                TextView siz = dialog.findViewById(R.id.detail_size);
                TextView date = dialog.findViewById(R.id.detail_date);
                TextView len = dialog.findViewById(R.id.detail_length);
                TextView res = dialog.findViewById(R.id.detail_resolution);
                TextView format = dialog.findViewById(R.id.detail_format);

                tit .setText(name);
                loc .setText(path);
                siz.setText(size);
                len.setText(duration);

                String dateTime;
                SimpleDateFormat simpleDateFormat;

                simpleDateFormat = new SimpleDateFormat("dd LLL yyyy, HH:mm aaa");
                dateTime = simpleDateFormat.format(filePath.lastModified());
                date.setText(dateTime);

                String someFilepath = filePath.getName();
                String extension = someFilepath.substring(someFilepath.lastIndexOf("."));
                format.setText( extension);

                res.setText(getResolution(activity, (filePath.getAbsolutePath())));
                dialog.show();

                dialog.findViewById(R.id.detail_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }

        }catch (Exception e){

        }
    }
    @SuppressLint("Range")
    private String getResolution(Context context, String absolutePath) {

        String duration = null;
        String width = null;
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]
                        {MediaStore.Video.VideoColumns.HEIGHT, MediaStore.Video.VideoColumns.WIDTH}, MediaStore.Images.ImageColumns.DATA + " = ?",
                new String[]{absolutePath}, null);

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {

                duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.HEIGHT));
                width = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.WIDTH));
            }

            cursor.close();
        }

        return duration + "*" + width;
    }

    private void renameFiles(int position, View v) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.rename_layout);
        dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText editText =dialog.findViewById(R.id.rename);
        String path = videosList.get(position).getPath();
        final File file = new File(path);
        String videoName = file.getName();
        videoName = videoName.substring(0, videoName.lastIndexOf("."));
        editText.setText(videoName);
//        dialog.setView(editText);
        editText.requestFocus();
        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String onlyPath = file.getParentFile().getAbsolutePath();
                String ext = file.getAbsolutePath();
                ext = ext.substring(ext.lastIndexOf("."));
                String newPath = onlyPath + "/" +editText.getText().toString()+ ext;
                File newFile = new File(newPath);
                boolean rename = file.renameTo(newFile);
                if(rename){
                    ContentResolver resolver = activity.getApplicationContext().getContentResolver();
                    resolver.delete(MediaStore.Files.getContentUri("external"), MediaStore.MediaColumns.DATA
                            + "=?", new String[]{file.getAbsolutePath()});
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(newFile));
                    activity.getApplicationContext().sendBroadcast(intent);
                    notifyDataSetChanged();
                    Toast.makeText(activity, "Video Renamed", Toast.LENGTH_SHORT).show();

                    SystemClock.sleep(100);
                    ((Activity) activity).recreate();
                    dialog.dismiss();
                }else {
                    Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        dialog.findViewById(R.id.rename_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void deleteFiles(int position,View view) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.delete_dialog);
        dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        String name = videosList.get(position).getTitle();
        TextView tit = dialog.findViewById(R.id.Delete_video);
        tit.setText(name);
        dialog.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        Long.parseLong(videosList.get(position).getId()));
                File file = new File(videosList.get(position).getPath());
                boolean deleted = file.delete();
                if (deleted){
                    activity.getApplicationContext().getContentResolver()
                            .delete(contentUri,
                                    null, null);
                    videosList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,videosList.size());
                    Snackbar.make(view,"File Deleted Success",
                            Snackbar.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else {
                    Snackbar.make(view,"File Deleted Fail",
                            Snackbar.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        dialog.findViewById(R.id.d_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void shareFiles(int position) {
        Uri uri = Uri.parse(videosList.get(position).getPath());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("audio/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        activity.startActivity(createChooser(intent,"share"));
        Toast.makeText(activity, "loading..", Toast.LENGTH_SHORT).show();
    }
//
    private void showPlaylist(Videomodel videomodel) {
        Dialog dialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        CreatePlaylistBinding playlistMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.create_playlist, null,
                false);
        dialog.setContentView(playlistMenuBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        playlistMenuBinding.playlistSongs.setLayoutManager(new LinearLayoutManager(activity));
        PlaylistDialogAdapter playlistDialogAdapter = new PlaylistDialogAdapter( videomodel , dialog, (AddToPlay) this,activity);
        playlistMenuBinding.playlistSongs.setAdapter( playlistDialogAdapter );

        playlistMenuBinding.newPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreatePlayList(videomodel,dialog);

            }
        });
        dialog.show();
    }
    public void onCreatePlayList(Videomodel videomodel,Dialog dial) {
        dial.dismiss();
        Dialog dialog=new Dialog(activity);
        PlatlistMenuBinding createPlaylist= DataBindingUtil.inflate(LayoutInflater.from(activity),R.layout.platlist_menu,null,false);
        dialog.setContentView(createPlaylist.getRoot());
        dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        createPlaylist.create.setOnClickListener(v1 -> {
            String playList = createPlaylist.enterPlaylist.getText().toString().trim().replace(" ", "_").toUpperCase();
            boolean matches = playList.matches("[a-z_A-Z ]+");
            if (playList.length() > 0) {
                if (matches){
                    if (!PLDHelper.isTableAva(playList)) {
                        PLDHelper.createPlaylist(new CreatePlymodel(createPlaylist.enterPlaylist.getText().toString().trim(), playList));
                        PLDHelper.createPlaylistTable(playList);
                        PLDHelper.addToDb(String.valueOf(videomodel.id), playList);
                        dialog.dismiss();
                        Utils.refreshPlaylist = true;

                    }

                }else {
                    Toast.makeText(activity, "write proper playlist name", Toast.LENGTH_SHORT).show();
                }

            }

        });
        createPlaylist.cancel.setOnClickListener(v1-> dialog.dismiss());
    }
    @Override
    public void onAddToPlayList(Videomodel videomodel, CreatePlymodel plyModel, Dialog dialog) {
        if (!PLDHelper.isAvailable(String.valueOf(videomodel.id), plyModel.table)) {
            PLDHelper.addToDb(String.valueOf(videomodel.id), plyModel.table);
            PLDHelper.updatePlayList(plyModel.name, plyModel.table);
//            raddedDatabase.setHi(getI(activity, audioFiles.get(position).getPath()));
            dialog.dismiss();
        } else {
            Toast.makeText(activity, "Video is Already available in this playlist.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onAddToFavorite(Videomodel videomodel, Dialog dialog) {
        if (!PLDHelper.isAvailable(String.valueOf(videomodel.id), Utils.favTable)) {
            PLDHelper.addToDb(String.valueOf(videomodel.id), Utils.favTable);
            PLDHelper.updatePlayList("My Favourite", Utils.favTable);
//            raddedDatabase.setHi(getI(activity, audioFiles.get(position).getPath()));
            dialog.dismiss();

        } else {
            Toast.makeText(activity, "Video is Already available in Favourite.", Toast.LENGTH_SHORT).show();
        }

    }


}
