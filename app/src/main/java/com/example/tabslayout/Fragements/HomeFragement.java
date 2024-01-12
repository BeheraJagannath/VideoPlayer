package com.example.tabslayout.Fragements;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.example.tabslayout.Adapter.FolderAdapter;
import com.example.tabslayout.Adapter.HistoryAdapter;
import com.example.tabslayout.Database.HistoryDataBase;
import com.example.tabslayout.Javaclass.Utils;
import com.example.tabslayout.R;
import com.example.tabslayout.Videomodel;
import com.example.tabslayout.databinding.FragmentHomeFragementBinding;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragement extends Fragment {
    FragmentHomeFragementBinding binding;
    FolderAdapter folderAdapter;
    private ArrayList<String> folderList = new ArrayList<String>();
    private ArrayList<Videomodel> videoList = new ArrayList<>();

    public HistoryAdapter historyAdapter;
    ArrayList< Videomodel > host= new ArrayList<>();
    HistoryDataBase historyDataBase ;
    LinearLayoutManager layoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home_fragement, container, false);

        historyAdapter = new HistoryAdapter(getActivity());

        historyDataBase = new HistoryDataBase(getActivity());
        try {
            videoList = fetchAllVideos(getContext());
            if (folderList != null && folderList.size() > 0 && videoList != null) {
                folderAdapter = new FolderAdapter(folderList, videoList, getContext());
                binding.recyclerView.setAdapter(folderAdapter);
                binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            }else {
                Toast.makeText(getContext(), "can't find any videos folder", Toast.LENGTH_SHORT).show();

            }

        }catch (Exception e){

        }

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                String input = newText.toLowerCase();

                ArrayList<String> arrayList = new ArrayList<>();
                for (String usersData : folderList ) {
                    if (usersData.toLowerCase().contains(input)) {
                        arrayList.add(usersData);
                        folderList = new ArrayList<>();
                        folderList.addAll(arrayList);
                    }
                }
                folderAdapter.searchedItem(arrayList);

                return true;
            }

        });
        binding.searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.background .setVisibility(View.GONE);

            }
        });
        binding.searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                binding.background.setVisibility(View.VISIBLE);
                return false;
            }
        });


        return binding .getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        final ArrayList<String> favList  = historyDataBase.getHisList();

        if (favList != null && favList.size() > 0){

            host = Utils.getDbVideos(favList, getContext());
        }

        if (host != null && host.size() > 0) {


            layoutManager = new LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false);
            binding.historyRecycler.setLayoutManager(layoutManager);


            binding.historyRecycler.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            binding.historyRecycler.getViewTreeObserver().removeOnPreDrawListener(this);
                            for (int i = 0; i < binding.historyRecycler.getChildCount(); i++) {
                                View v = binding.historyRecycler.getChildAt(i);
                                v.setAlpha(0.0f);
                                v.animate().alpha(1.0f)
                                        .setDuration(300)
                                        .setStartDelay(i * 50)
                                        .start();
                            }
                            return true;
                        }
                    }
            );

            binding.historyRecycler.setAdapter(historyAdapter);
            Collections.reverse(host);
            historyAdapter.addAll(host);
            historyAdapter.notifyDataSetChanged();

        }
    }

    private ArrayList<Videomodel> fetchAllVideos(Context context) {

        ArrayList<Videomodel> videoModels = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Video.Media.BUCKET_DISPLAY_NAME + " ASC";
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.RESOLUTION,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DATE_ADDED
        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, orderBy);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                String size = cursor.getString(3);
                String resolution = cursor.getString(4);
                String duration = cursor.getString(5);
                String disName = cursor.getString(6);
                String width_height = cursor.getString(7);
                String mimeType = cursor.getString(8);
                String dateAdded = cursor.getString(9);

                String bucket_display_name = null;
                Videomodel videoFiles = new Videomodel(id, path, title, size, resolution, duration, disName, width_height, resolution
                        , bucket_display_name, width_height, mimeType, dateAdded);
                int slashFirstIndex = path.lastIndexOf("/");
                String subString = path.substring(0, slashFirstIndex);
                if (!folderList.contains(subString)) {
                    folderList.add(subString);
                }
                videoModels.add(videoFiles);
            }
            cursor.close();
        }
        return videoModels;

    }

}




