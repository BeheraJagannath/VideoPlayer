package com.example.tabslayout.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabslayout.R;
import com.example.tabslayout.VideoFolder;
import com.example.tabslayout.Videomodel;

import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder>{
    private ArrayList<String> folderName;
    private ArrayList<Videomodel> videoModels;
    private final Context context;

    public FolderAdapter(ArrayList<String> folderName, ArrayList<Videomodel> videoModels, Context context) {
        this.folderName = folderName;
        this.videoModels = videoModels;
        this.context = context;
    }
    @NonNull
    @Override
    public FolderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int index = folderName.get(position).lastIndexOf("/");
        String folderNames = folderName.get(position).substring(index + 1);

        holder.foldername.setText(folderNames);
        holder.tatalvideo.setText((countVideos(folderName.get(position))+ " Videos"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, VideoFolder.class);
                intent.putExtra("folderName",folderName.get(position));
                // this line is for get the folder name & set it in a_VideoFolder title
                intent.putExtra("name",  holder.foldername.getText().toString());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return folderName.size();
    }
    int countVideos(String folders) {
        int count = 0;
        for (Videomodel model : videoModels) {
            if (model.getPath().substring(0,
                    model.getPath().lastIndexOf("/"))
                    .endsWith(String.valueOf(folders))) {
                count++;
            }
        }
        return count;
    }

    public void searchedItem(ArrayList<String> arrayList) {
        folderName = new ArrayList<>();
        folderName.addAll(arrayList);
        notifyDataSetChanged();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView foldername,tatalvideo;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            foldername=itemView.findViewById(R.id.foldername);
            tatalvideo=itemView.findViewById(R.id.totalvideo);
        }
    }
}
