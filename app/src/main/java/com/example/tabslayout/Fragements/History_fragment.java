package com.example.tabslayout.Fragements;

import static com.example.tabslayout.Adapter.RecentAdapter.getDuration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.example.tabslayout.Adapter.AllVideoAdapter;
import com.example.tabslayout.Adapter.RecentAdapter;
import com.example.tabslayout.Database.HistoryDataBase;
import com.example.tabslayout.Database.PLDHelper;
import com.example.tabslayout.Database.Sqlitedatabase;
import com.example.tabslayout.Javaclass.SelectedVideoItem;
import com.example.tabslayout.Javaclass.Utils;
import com.example.tabslayout.PlayerActivity;
import com.example.tabslayout.R;
import com.example.tabslayout.Videomodel;
import com.example.tabslayout.databinding.HistoryFragmentBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class History_fragment extends Fragment implements SelectedVideoItem {
    HistoryFragmentBinding binding;
    RecentAdapter recentAdapter;
    SwipeRefreshLayout refresh;
    ArrayList<Videomodel> recent = new ArrayList<>();
    HistoryDataBase historyDatabase;
    Sqlitedatabase sqlitedatabase;
    LinearLayoutManager linearLayoutManager;
    boolean isActionModeEnable = false;

    ArrayList<Videomodel> selectedList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.history_fragment, container, false);

        recentAdapter = new RecentAdapter(recent,getActivity(), this, 0, binding);

        historyDatabase = new HistoryDataBase(getContext());
        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onStart();
                binding.refresh.setRefreshing(false);
            }
        });

        binding.mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                a_playersAdapter.getFilter().filter(newText);

                String input = newText.toLowerCase();
                ArrayList<Videomodel> arrayList = new ArrayList<>();
                for (Videomodel usersData : recent ) {
                    if (usersData.getTitle().toLowerCase().contains(input)) {
                        arrayList.add(usersData);
                        recent = new ArrayList<>();
                        recent.addAll(arrayList);
                    }
                }

                recentAdapter.searchedItem(arrayList);

                return true;
            }
        });
        binding.mSearch.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.mTextLin .setVisibility(View.GONE);

            }
        });
        binding.mSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                binding.mTextLin.setVisibility(View.VISIBLE);
                return false;
            }
        });
        binding.close.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActionMode();
            }
        });
        binding.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMultipleVideo();
            }


        });



        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        final ArrayList<String> favList = historyDatabase.getHisList();

        if (favList != null && favList.size() > 0) {

            recent = Utils.getDbVideos(favList, getContext());
        }
        if (recent != null && recent.size() > 0) {

            linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            binding.recycler.setLayoutManager(linearLayoutManager);


            binding.recycler.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                           binding. recycler.getViewTreeObserver().removeOnPreDrawListener(this);
                            for (int i = 0; i < binding.recycler.getChildCount(); i++) {
                                View v = binding.recycler.getChildAt(i);
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

            binding.recycler.setAdapter(recentAdapter);
            Collections.reverse(recent);
            recentAdapter.addAll(recent);
            recentAdapter.notifyDataSetChanged();

        }
    }
    @SuppressLint({"SetTextI18n", "SimpleDateFormat", "Range"})

    public static String getId(Context context, String path) {
        String id = null;
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]
                        {MediaStore.Video.VideoColumns._ID}, MediaStore.Images.ImageColumns.DATA + " = ?",
                new String[]{path}, null);

        if (cursor != null) {
            cursor.moveToFirst();
            id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID));

            cursor.close();
        }

        return id;
    }

    @Override
    public void VideoSelectCount(int Count) {

    }

    @Override
    public void onVideoLongPress() {
        if (!isActionModeEnable) {
            startActionMode();
        }

    }
    private void startActionMode() {

        isActionModeEnable = true;
        binding.relativeOne.setVisibility(View.GONE);
        binding.actionBar.setVisibility(View.VISIBLE);
        selectedList.clear();
        binding.videoCount.setText(String.format("%d  Selected", 0));
        recentAdapter.startAction();


    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void onBackPressed() {

        if (isActionModeEnable) {
            closeActionMode();
        } else{

            onBackPressed();

        }

    }

    private void closeActionMode() {

        isActionModeEnable = false;
        binding.relativeOne.setVisibility(View.VISIBLE);
        binding.actionBar.setVisibility(View.GONE);
        selectedList.clear();
        binding.videoCount.setText(String.format("%d  Selected", 0));
        recentAdapter.stopAction();
    }

    @Override
    public void onOptionClick(Videomodel videoClass, int position) {

    }

    @Override
    public void onBind(ImageView imageView, Videomodel videoClass) {
        if (isAvailable(videoClass)) {
            imageView.setImageResource(R.drawable.chkbx_done);
        } else {
            imageView.setImageResource(R.drawable.chkbx);
        }

    }
    private boolean isAvailable(Videomodel videoModel) {
        for (int i = 0; i < selectedList.size(); i++) {
            if (videoModel.equals(selectedList.get(i))) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onItemClick(ImageView imageView, int position, ArrayList<Videomodel> videoClasses, File file) {

        if ( isActionModeEnable ) {
            makeSelect(imageView, videoClasses.get(position));

        }else{
            if (PLDHelper.isAvailable(String.valueOf(videoClasses.get(position)), Utils.recentTable)) {
                PLDHelper.deleteVideo(String.valueOf(videoClasses.get(position)), Utils.recentTable);
            }

            PLDHelper.addToDb(String.valueOf(videoClasses.get(position)), Utils.recentTable);
            Utils.refreshRecent = false;
            Utils.refreshHistory = true;

            Intent intent=new Intent(getContext(), PlayerActivity.class);
            intent.putExtra("position",position);
            intent.putExtra("title",recent.get(position).getTitle());
            intent.putExtra("time",recent.get(position).Utils.getDurationBreakdown ( getDuration(getContext( ) , (file.getAbsolutePath() ))));
            Bundle bundle=new Bundle();
            bundle.putSerializable("sel",recent);
            intent.putExtras(bundle);
            this.startActivity(intent);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        }


    }
    private void removeMultipleVideo() {

        for (int i = 0; i < selectedList.size(); i++) {
            for (int j = 0; j < recent.size(); j++) {
                if (selectedList.get(i).equals(recent.get(j))) {

                    historyDatabase.removeFav(getId(getContext(), recent.get(j).getPath()));
                    onStart();

                }
            }
        }
        closeActionMode();
    }

    private void makeSelect(ImageView imageView, Videomodel videoModel) {


        if (imageView.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.chkbx_done).getConstantState())) {
            selectedList.remove(videoModel);
            imageView.setImageResource(R.drawable.chkbx);
        } else {
            selectedList.add(videoModel);
            imageView.setImageResource(R.drawable.chkbx_done);
        }

        binding.videoCount.setText(String.format("%d  Selected", selectedList.size()));
    }
}