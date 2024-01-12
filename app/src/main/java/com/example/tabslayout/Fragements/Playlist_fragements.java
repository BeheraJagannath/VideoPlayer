package com.example.tabslayout.Fragements;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.tabslayout.Adapter.ListAdapter;
import com.example.tabslayout.Database.PLDHelper;
import com.example.tabslayout.Database.Sqlitedatabase;
import com.example.tabslayout.Javaclass.CreatePlymodel;
import com.example.tabslayout.Javaclass.Utils;
import com.example.tabslayout.Javaclass.pllistPrefManager;
import com.example.tabslayout.R;
import com.example.tabslayout.databinding.FragmentPlaylistFragementsBinding;
import com.example.tabslayout.databinding.PlatlistMenuBinding;

import java.util.ArrayList;
public class Playlist_fragements extends Fragment {
    FragmentPlaylistFragementsBinding binding;
    ListAdapter listAdapter;
    public static ArrayList<CreatePlymodel> playlist=new ArrayList<>();
    Sqlitedatabase sqlitedatabase;
    pllistPrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_playlist_fragements, container, false);
        sqlitedatabase = new Sqlitedatabase(getActivity());
        prefManager = new pllistPrefManager(getActivity());

        StartApp();

        return  binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utils.refreshPlaylist) {

            listAdapter.addAll(PLDHelper.getAllPlayList());

            Utils.refreshPlaylist = false;

        }

    }
    private void StartApp() {
        ArrayList<CreatePlymodel> plyModels = PLDHelper.getAllPlayList();

        if (plyModels != null) {
            playlist.addAll(plyModels);
        }

        if (playlist != null && playlist.size() > 0) {

            binding.addCount.setText(String.format("%d ", playlist.size()));
            Utils.refreshPlaylist=true;

        }

        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        listAdapter = new ListAdapter(getActivity(), playlist);
        binding.recycler.setAdapter(listAdapter);
        listAdapter.addAll(playlist);
        listAdapter.notifyDataSetChanged();


        binding.nAdd.setOnClickListener(v -> {

            createPlaylist();

        });


    }

    private void createPlaylist() {
        Dialog dialog = new Dialog(getActivity());
        PlatlistMenuBinding createPlaylist = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.platlist_menu, null, false);
        dialog.setContentView(createPlaylist.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        createPlaylist.create.setOnClickListener(v1 -> {

            String pathList = createPlaylist.enterPlaylist.getText().toString().trim().replace(" ", "_").toUpperCase();
            boolean math = pathList.matches("[a-z_A-Z ]+");
            if (pathList.length() > 0) {
                if (math) {
                    if (!PLDHelper.isTableAva(pathList)) {
                        PLDHelper.createPlaylist(new CreatePlymodel(createPlaylist.enterPlaylist.getText().toString().trim(), pathList));
                        PLDHelper.createPlaylistTable(pathList);
                        this.playlist.add(new CreatePlymodel(createPlaylist.enterPlaylist.getText().toString().trim(), pathList));
                        listAdapter.addNewPlayList(new CreatePlymodel(createPlaylist.enterPlaylist.getText().toString().trim(), pathList));
                        binding.addCount.setText(String.format("%d PlayList",this.playlist.size()));
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(getActivity(), "write proper playlist name", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "write playlist name", Toast.LENGTH_SHORT).show();
            }

        });

        createPlaylist.cancel.setOnClickListener(v1 -> dialog.dismiss());

    }
}