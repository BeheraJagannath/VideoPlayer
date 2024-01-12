package com.example.tabslayout.Javaclass;

import android.widget.ImageView;

import com.example.tabslayout.Videomodel;

import java.io.File;
import java.util.ArrayList;

public interface SelectedVideoItem {
    void VideoSelectCount(int Count);
    void onVideoLongPress();
    void onOptionClick(Videomodel videoClass, int position);
    void onBind(ImageView imageView, Videomodel videoClass);
    void onItemClick (ImageView imageView, int position, ArrayList<Videomodel> videoClasses , File file);
}
