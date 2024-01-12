package com.example.tabslayout.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabslayout.Javaclass.Model;
import com.example.tabslayout.R;

import java.util.ArrayList;

public class PlayAdapter extends RecyclerView.Adapter<PlayAdapter.ViewHolder>{
    private ArrayList<Model> ModelList;
    private Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener =  listener;
    }

    public PlayAdapter(ArrayList<Model> iconModelList, Context context) {
        this.ModelList = iconModelList;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.model, parent, false);

        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.icon.setImageResource(ModelList.get(position).getImageView());
        holder.iconName.setText(ModelList.get(position).getIconTitle());

    }

    @Override
    public int getItemCount() {
        return ModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView iconName;
        ImageView icon;

        public ViewHolder(@NonNull View itemView , OnItemClickListener listener) {
            super(itemView);

            iconName = itemView.findViewById(R.id.txt_background);
            icon = itemView.findViewById(R.id.img_background);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.OnItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
