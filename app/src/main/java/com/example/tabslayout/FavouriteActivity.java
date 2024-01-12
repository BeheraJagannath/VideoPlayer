package com.example.tabslayout;

import static com.example.tabslayout.Adapter.RecentAdapter.getDuration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.example.tabslayout.Adapter.FavouriteAdapter;
import com.example.tabslayout.Database.PLDHelper;
import com.example.tabslayout.Fragements.select_fregment;
import com.example.tabslayout.Javaclass.SelectedVideoItem;
import com.example.tabslayout.Javaclass.Utils;
import com.example.tabslayout.Javaclass.VideoClickListener;
import com.example.tabslayout.databinding.ActivityFavouriteBinding;
import com.example.tabslayout.databinding.MenuDialogBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FavouriteActivity extends AppCompatActivity implements SelectedVideoItem , VideoClickListener {
    ActivityFavouriteBinding binding;
    public static String name, tableName;
    FavouriteAdapter favouriteAdapter;
    private ArrayList<Videomodel> playList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;

    ArrayList<Videomodel> selectedList = new ArrayList<>();
    boolean isActionModeEnable = false;
    select_fregment select ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_favourite);

        name = getIntent().getStringExtra("NAME");
        tableName = getIntent().getStringExtra("TABLE");

        binding.bak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                for (Videomodel usersData : playList ) {
                    if (usersData.getTitle().toLowerCase().contains(input)) {
                        arrayList.add(usersData);
                        playList = new ArrayList<>();
                        playList.addAll(arrayList);
                    }
                }

                favouriteAdapter.searchedItem(arrayList);

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


        binding.playlistAdd . setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                select = new select_fregment(tableName);
                select . show ( getSupportFragmentManager(), select_fregment.TAG);
            }
        });
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
        binding.ivOption.setOnClickListener ( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showOption();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<String> dbVideos = PLDHelper.getAllVideos(tableName);
        if (dbVideos != null && dbVideos.size() > 0) {
            playList = Utils.getDbVideos(dbVideos, this);
        }

        if (playList != null && playList.size() > 0) {
//            binding.mText.setText(String.format("%d Songs", playList.size()));
            binding.recycl.setLayoutManager(new LinearLayoutManager(this));
            Collections.reverse(playList);
            favouriteAdapter = new FavouriteAdapter(this ,this ,0,binding);
            binding.recycl.setAdapter(favouriteAdapter);
            favouriteAdapter.addAll(playList);
            Utils.refreshPlaylist = true;
            favouriteAdapter.notifyItemRemoved(playList.size());

        }
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
        binding.tvCounter.setText(String.format("%d  Selected", 0));
        favouriteAdapter.startAction();


    }
    @Override
    public void onBackPressed() {
        if (isActionModeEnable) {
            closeActionMode();
        } else
            super.onBackPressed();
    }

    private void closeActionMode() {
        isActionModeEnable = false;
        binding.relativeOne.setVisibility(View.VISIBLE);
        binding.actionBar.setVisibility(View.GONE);
        selectedList.clear();
        binding.tvCounter.setText(String.format("%d  Selected", 0));
        favouriteAdapter.stopAction();


    }

    private void showOption() {

        PopupWindow popupWindow = new PopupWindow(this);
        final MenuDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.menu_dialog, null, false);
        popupWindow.setContentView(binding.getRoot());

        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.showAsDropDown(this.binding.ivOption, -40, 0);
        binding.mDelet.setOnClickListener(v -> {
            popupWindow.dismiss();
            removemultiplevideo();

        });

    }

    private void removemultiplevideo() {

        for (int i = 0; i < selectedList.size(); i++) {
            for (int j = 0; j < playList.size(); j++) {
                if (selectedList.get(i).equals(playList.get(j))) {

                    removeVideo(selectedList.get(i), j);

                }
            }
        }
        closeActionMode();
    }

    private void removeVideo(Videomodel videoModel, int position) {

        boolean b = PLDHelper.deleteVideo(String.valueOf(videoModel.id), tableName);
        if (b) {
            playList.remove(position);
            favouriteAdapter.videoDelete(position);
//            adapter.notifyItemRemoved(position);
            PLDHelper.updatePlayList(name, tableName);
            Utils.refreshPlaylist = true;
        }
//        mFavouriteImageList.remove(position);
//        playNewVideoListAdapter.videoDelete(position);
//
//
//        Utils.refreshPlaylist = true;


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

    @Override
    public void onItemClick(ImageView imageView, int position, ArrayList<Videomodel> videoClasses, File file) {

        if ( isActionModeEnable ) {
            makeSelect(imageView, videoClasses.get(position));

        }else {

            if (PLDHelper.isAvailable(String.valueOf(videoClasses.get(position)), Utils.recentTable)) {
                PLDHelper.deleteVideo(String.valueOf(videoClasses.get(position)), Utils.recentTable);
            }

            PLDHelper.addToDb(String.valueOf(videoClasses.get(position)), Utils.recentTable);
            Utils.refreshRecent = false;
            Utils.refreshHistory = true;

            Intent intent=new Intent(this, PlayerActivity.class);
            intent.putExtra("position",position);
            intent.putExtra("title",playList.get(position).getTitle());
            intent.putExtra("time",playList .get(position).getDuration());
            Bundle bundle=new Bundle();
            bundle.putSerializable("sel",playList);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);



        }

    }

    private void makeSelect(ImageView imageView, Videomodel videoModel) {


        if (imageView.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.chkbx_done).getConstantState())) {
            selectedList.remove(videoModel);
            imageView.setImageResource(R.drawable.chkbx);
        } else {
            selectedList.add(videoModel);
            imageView.setImageResource(R.drawable.chkbx_done);
        }

        binding.tvCounter.setText(String.format("%d  Selected", selectedList.size()));
    }


    private boolean isAvailable(Videomodel videoClass) {

        for (int i = 0; i < selectedList.size(); i++) {

            if (videoClass.equals(selectedList.get(i))) {

                return true;
            }
        }
        return false;
    }

    @Override
    public void notification() {
        select.dismiss();
        Utils.refreshPlaylist = true;

    }

    @Override
    public void close() {
        select .dismiss();

    }
}