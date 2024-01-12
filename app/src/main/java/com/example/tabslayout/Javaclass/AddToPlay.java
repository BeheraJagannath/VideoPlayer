package com.example.tabslayout.Javaclass;

import android.app.Dialog;

import com.example.tabslayout.Videomodel;

public interface AddToPlay {
    void onCreatePlayList(Videomodel videomodel, Dialog dialog );

    void onAddToPlayList(Videomodel videomodel, CreatePlymodel plyModel, Dialog dialog);

    void onAddToFavorite(Videomodel videomodel, Dialog dialog);
}
