package com.example.tabslayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import static com.example.tabslayout.Javaclass.Pref.read;
import static com.example.tabslayout.Javaclass.Pref.write;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PictureInPictureParams;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Rational;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tabslayout.Adapter.PlayAdapter;
import com.example.tabslayout.Database.PLDHelper;
import com.example.tabslayout.Javaclass.Model;
import com.example.tabslayout.Javaclass.Pref;
import com.example.tabslayout.Javaclass.Utils;
import com.example.tabslayout.databinding.BrightnessDialogBinding;
import com.example.tabslayout.databinding.EqualizerLayBinding;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {
    int position;
    String videoTitle;
    TextView title;
    ArrayList<Videomodel> VideoFiles = new ArrayList<>();
    PlaybackParameters parameters;
    float speed;
    boolean isCrossChecked;
    PictureInPictureParams.Builder pictureInPicture;

    PlayerView playerView;
    SimpleExoPlayer player;
    ConcatenatingMediaSource concatenatingMediaSource;
    ImageView next, previous, pause, lock, fullscreen;
    ImageView stretch, resize, video_back;
    LinearLayout linearLayout_one, linearLayout_three, bottom_layout, un_lock;
    TextView end_position, start_position, back_home, back_to_video;
    ImageView video_more, background, equalizer_lay;
    RelativeLayout back_audio;
    long videoWatchedTime = 0;
    DefaultTimeBar exo_progress;
    RelativeLayout eqFrame;


    private ArrayList<Model> modelArrayList = new ArrayList<>();
    PlayAdapter playAdapter;
    boolean expand = false;
    boolean mute = false;
    RecyclerView play_recycler;

    private WindowManager.LayoutParams attributes;
    private float brightnessItem;
    String totalImage;
    Equalizer equalizer;
    BassBoost bassBoost;
    private String duration_formatted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullScreen();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        playerView = findViewById(R.id.exo_player_view);
        title = findViewById(R.id.videotitle);
        next = findViewById(R.id.playPause_next);
        previous = findViewById(R.id.playPause_previous);
        pause = findViewById(R.id.playPause_pause);
        lock = findViewById(R.id.lock);
        un_lock = findViewById(R.id.unlock);
        fullscreen = findViewById(R.id.fullscreen);
        stretch = findViewById(R.id.stretch);
        resize = findViewById(R.id.resize);
        video_back = findViewById(R.id.video_back);
        linearLayout_one = findViewById(R.id.linearLayout_one);
        linearLayout_three = findViewById(R.id.linearLayout_three);
        bottom_layout = findViewById(R.id.bottom_layout);
        start_position = findViewById(R.id.start_position);
        end_position = findViewById(R.id.end_position);
        video_more = findViewById(R.id.video_more);
        background = findViewById(R.id.background);
        back_audio = findViewById(R.id.back_audio);
        back_to_video = findViewById(R.id.back_to_video);
        equalizer_lay = findViewById(R.id.equalizer_lay);
        back_audio = findViewById(R.id.back_audio);
        back_home = findViewById(R.id.back_home);
        exo_progress = findViewById(R.id.exo_progress);
        eqFrame =findViewById(R.id.eqFrame);

        play_recycler = findViewById(R.id.play_recycler);


        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        pause.setOnClickListener(this);
        lock.setOnClickListener(this);
        un_lock.setOnClickListener(this);
        fullscreen.setOnClickListener(this);
        stretch.setOnClickListener(this);
        resize.setOnClickListener(this);
        video_back.setOnClickListener(this);
        video_more.setOnClickListener(this);
//Brightness and volume seekbar dialog
        brightnessItem = read("BRIGHTNESS");
        WindowManager.LayoutParams attribute2 = getWindow().getAttributes();
        attributes = attribute2;
        attribute2.screenBrightness = brightnessItem;
        getWindow().setAttributes(attributes);


        videoTitle = getIntent().getStringExtra("title");
        totalImage = getIntent().getStringExtra("time");
        position = getIntent().getIntExtra("position", position);
        VideoFiles = getIntent().getExtras().getParcelableArrayList("sel");
        title.setText(videoTitle);
        end_position.setText(totalImage);

        playVideo();
        Handler handler = new Handler();
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (player != null) {
                    int mCurrentPosition = (int) (player.getCurrentPosition() / 1000);
                    start_position.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pictureInPicture = new PictureInPictureParams.Builder();
        }


        modelArrayList.add(new Model(R.drawable.rotate, "Rotate"));
        modelArrayList.add(new Model(R.drawable.brightness, "Brightness"));
        modelArrayList.add(new Model(R.drawable.mt_btn, "Mute"));
        modelArrayList.add(new Model(R.drawable.sound, "Volume"));
//        modelArrayList.add(new Model(R.drawable.ic_baseline_circle_24,""));


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        play_recycler.setLayoutManager(layoutManager);
        playAdapter = new PlayAdapter(modelArrayList, this);
        play_recycler.setAdapter(playAdapter);
        playAdapter.notifyDataSetChanged();

        playAdapter.setOnItemClickListener(new PlayAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
// Video rotate
                if (position == 0) {
                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        //set in landscape
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        //set in portrait
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }

                }

                if (position == 1) {
//brightness dialog

                    brightnessDialog();
                }
                if (position == 2) {

                    if (mute) {
                        modelArrayList.set(position, new Model(R.drawable.mt_btn, "Mute"));
                        playAdapter.notifyDataSetChanged();
                        player.setVolume(100);
                        mute = false;


                    } else {

                        player.setVolume(0);
                        modelArrayList.set(position, new Model(R.drawable.mute, "Sound"));
                        playAdapter.notifyDataSetChanged();
                        mute = true;

                    }

                }

                if (position == 3) {
//  volume dialog
                    volumeDialog();

                }
            }


        });
        video_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlayerActivity.this);
                alertDialog.setTitle("Select PlayBack Speed").setPositiveButton("ok", null);
                String[] items = {"0.5x", "0.75f", "1x Normal Speed", "1.5", "2x"};
                int ckeckedItem = -1;
                alertDialog.setSingleChoiceItems(items, ckeckedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {

                            case 0:
                                speed = 0.5f;
                                parameters = new PlaybackParameters(speed);
                                player.setPlaybackParameters(parameters);
                                break;
                            case 1:
                                speed = 0.75f;
                                parameters = new PlaybackParameters(speed);
                                player.setPlaybackParameters(parameters);
                                break;
                            case 2:
                                speed = 1f;
                                parameters = new PlaybackParameters(speed);
                                player.setPlaybackParameters(parameters);
                                break;
                            case 3:
                                speed = 1.5f;
                                parameters = new PlaybackParameters(speed);
                                player.setPlaybackParameters(parameters);
                                break;
                            case 4:
                                speed = 2f;
                                parameters = new PlaybackParameters(speed);
                                player.setPlaybackParameters(parameters);
                                break;
                            default:
                                break;
                        }
                    }
                });
                AlertDialog alert = alertDialog.create();
                alert.show();

            }
        });
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();


            }
        });
        equalizer_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEqualizerDialog();

            }

        });

    }



    private String formattedTime(int mCurrentPosition) {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf((mCurrentPosition % 3600) / 60);
        String hrs = String.valueOf(mCurrentPosition / 3600);
        totalOut =  hrs + ":" +  minutes + ":"  + seconds;
        totalNew =  hrs + ":" +"0" + minutes + ":" +"0" + seconds;
        if (seconds.length() == 1) {
            return totalNew;
        } else if (hrs.length() == 2) {
            return totalNew;
        } else {
            return totalOut;
        }


    }

    private void dialog() {
        eqFrame.setVisibility(View.VISIBLE);
        back_to_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eqFrame.setVisibility(View.GONE);
            }
        });
    }


    private void playVideo() {

        String path = VideoFiles.get(position).getPath();
        Uri uri = Uri.parse(path);
        player = new SimpleExoPlayer.Builder(this).build();

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this
                , Util.getUserAgent(this, "app"));
        concatenatingMediaSource = new ConcatenatingMediaSource();
        for (int i = 0; i < VideoFiles.size(); i++) {
            new File(String.valueOf(VideoFiles.get(i)));
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(String.valueOf(uri)));
            concatenatingMediaSource.addMediaSource(mediaSource);
        }


        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);
        player.setPlaybackParameters(parameters);

        player.prepare(concatenatingMediaSource);
        player.seekTo(position, C.TIME_UNSET);
        playError();

        player.addListener(new Player.Listener() {

            @Override
            public void onMediaItemTransition(MediaItem mediaItem, int reason) {

                if (PLDHelper.isAvailable(String.valueOf(VideoFiles), Utils.recentTable)) {
                    PLDHelper.deleteVideo(String.valueOf(VideoFiles), Utils.recentTable);
                }

                PLDHelper.addToDb(String.valueOf(VideoFiles), Utils.recentTable);
                Utils.refreshRecent = true;

            }
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_IDLE || state == Player.STATE_ENDED) {
                    playerView.setKeepScreenOn(false);

                } else {
                    playerView.setKeepScreenOn(true);


                }
            }
        });

    }

    private void playError() {
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(PlayerActivity.this, "Video Playing Error", Toast.LENGTH_SHORT).show();
            }
        });
        player.setPlayWhenReady(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (player.isPlaying()) {
            player.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
        player.getPlaybackState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playPause_next:
                try {
                    player.stop();
                    position++;
                    title.setText(VideoFiles.get(position).getTitle());
                    end_position.setText(VideoFiles.get(position).getDuration());
                    playVideo();
                    pause.setImageResource(R.drawable.ic_pause);


                } catch (Exception e) {
                    Toast.makeText(PlayerActivity.this, "no Next Video", Toast.LENGTH_SHORT).show();
                    finish();

                }
                break;
            case R.id.playPause_previous:
                try {
                    player.stop();
                    position--;
                    title.setText(VideoFiles.get(position).getTitle());
                    end_position.setText(VideoFiles.get(position).getDuration());
                    playVideo();
                    pause.setImageResource(R.drawable.ic_pause);

                } catch (Exception e) {
                    Toast.makeText(PlayerActivity.this, "no Previous Video", Toast.LENGTH_SHORT).show();
                    finish();

                }
                break;
            case R.id.video_back:
                finish();
                break;
            case R.id.playPause_pause:
                if (player.isPlaying()) {
                    player.pause();
                    pause.setImageResource(R.drawable.ic_play);
                    player.setPlayWhenReady(false);
                } else {
                    player.setPlayWhenReady(true);
                    pause.setImageResource(R.drawable.ic_pause);
                }
                break;
            case R.id.fullscreen:
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
//                playerView.setBackgroundResource(R.drawable.strech);
                stretch.setVisibility(View.VISIBLE);
                resize.setVisibility(View.GONE);
                fullscreen.setVisibility(View.GONE);
                break;
            case R.id.stretch:
                // stretch vdo
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
//                playerView.setBackgroundResource(R.drawable.full);
                stretch.setVisibility(View.GONE);
                resize.setVisibility(View.VISIBLE);
                fullscreen.setVisibility(View.GONE);

                break;

            case R.id.resize:
                // stretch vdo
                // fit vdo
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//                playerView.setBackgroundResource(R.drawable.fullscreen);
                stretch.setVisibility(View.GONE);
                resize.setVisibility(View.GONE);
                fullscreen.setVisibility(View.VISIBLE);
                break;

            case R.id.lock:
                un_lock.setVisibility(View.VISIBLE);
                lock.setVisibility(View.GONE);
                linearLayout_three.setVisibility(View.GONE);
                linearLayout_one.setVisibility(View.GONE);
                bottom_layout.setVisibility(View.GONE);
                break;

            case R.id.unlock:
                un_lock.setVisibility(View.GONE);
                lock.setVisibility(View.VISIBLE);
                linearLayout_three.setVisibility(View.VISIBLE);
                linearLayout_one.setVisibility(View.VISIBLE);
                bottom_layout.setVisibility(View.VISIBLE);
                break;


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isCrossChecked) {
            player.release();
            finish();
        }
    }

    private void brightnessDialog() {
        Dialog dialog = new Dialog(this);
        BrightnessDialogBinding brightBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.brightness_dialog, null, false);
        dialog.setContentView(brightBinding.getRoot());
        dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        brightBinding.brightText.setVisibility(View.GONE);
        int lastBrightness = (int) (Pref.read("BRIGHTNESS") * 100.0f);
        brightBinding.brightSeekbar.setProgress(lastBrightness);
        brightBinding.brightText.setText(Integer.toString(lastBrightness));
        brightBinding.brightSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                attributes.screenBrightness = ((float) progress) / 100.0f;
                getWindow().setAttributes(attributes);
                seekBar.setProgress(progress);
                brightBinding.brightText.setText(Integer.toString(lastBrightness));

                write("BRIGHTNESS", attributes.screenBrightness);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void volumeDialog() {
        Dialog dialog = new Dialog(this);
        BrightnessDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.brightness_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        // cheak karo ho raha hai ha thik se kam kar raha hai ok ho gya
//        dialogBinding.brtDialogIcon.setImageResource(R.drawable.sound);

        dialogBinding.brightText.setVisibility(View.GONE);
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        dialogBinding.brightSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        dialogBinding.brightSeekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        dialogBinding.brightSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void showEqualizerDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(PlayerActivity.this, R.style.BottomSheetDialogTheme);
        EqualizerLayBinding bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(PlayerActivity.this), R.layout.equalizer_lay, null, false);
        bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        bottomSheetDialog.show();

        equal(bottomSheetBinding);
    }

    private void equal(EqualizerLayBinding equalizerLayBinding) {

        if (Pref.read(Pref.PrefKey.Equalizer, false)) {
            equalizerLayBinding.on.setText("ON");
            for (int i = 0; i < equalizerLayBinding.radioGroup.getChildCount(); i++) {
                View view = equalizerLayBinding.radioGroup.getChildAt(i);
                view.setEnabled(true);
            }

            for (int i = 0; i < equalizerLayBinding.ly1.getChildCount(); i++) {
                equalizerLayBinding.ly1.getChildAt(i).setEnabled(true);
            }
            equalizerLayBinding.firstBand.setEnabled(true);

            for (int i = 0; i < equalizerLayBinding.ly2.getChildCount(); i++) {
                equalizerLayBinding.ly2.getChildAt(i).setEnabled(true);
            }
            equalizerLayBinding.secondBand.setEnabled(true);

            for (int i = 0; i < equalizerLayBinding.ly3.getChildCount(); i++) {
                equalizerLayBinding.ly3.getChildAt(i).setEnabled(true);
            }
            equalizerLayBinding.thirdBand.setEnabled(true);

            for (int i = 0; i < equalizerLayBinding.ly4.getChildCount(); i++) {
                equalizerLayBinding.ly4.getChildAt(i).setEnabled(true);
            }
            equalizerLayBinding.fourBand.setEnabled(true);

            for (int i = 0; i < equalizerLayBinding.ly5.getChildCount(); i++) {
                equalizerLayBinding.ly5.getChildAt(i).setEnabled(true);
            }
            equalizerLayBinding.fiveBand.setEnabled(true);

            equalizerLayBinding.bass.setEnabled(true);
            equalizerLayBinding.bassSeekbar.setEnabled(true);
            equalizerLayBinding.verti.setEnabled(true);
            equalizerLayBinding.vertiSeekbar.setEnabled(true);

            equalizerLayBinding.switch1.setChecked(true);
            enableEqualizer();
            startEqualizer(equalizerLayBinding);

        } else {
            equalizerLayBinding.on.setText("OFF");
            for (int i = 0; i < equalizerLayBinding.radioGroup.getChildCount(); i++) {
                View view = equalizerLayBinding.radioGroup.getChildAt(i);
                view.setEnabled(false);
            }
            for (int i = 0; i < equalizerLayBinding.ly1.getChildCount(); i++) {
                equalizerLayBinding.ly1.getChildAt(i).setEnabled(false);
            }
            equalizerLayBinding.firstBand.setEnabled(false);

            for (int i = 0; i < equalizerLayBinding.ly2.getChildCount(); i++) {
                equalizerLayBinding.ly2.getChildAt(i).setEnabled(false);
            }
            equalizerLayBinding.secondBand.setEnabled(false);

            for (int i = 0; i < equalizerLayBinding.ly3.getChildCount(); i++) {
                equalizerLayBinding.ly3.getChildAt(i).setEnabled(false);
            }
            equalizerLayBinding.thirdBand.setEnabled(false);

            for (int i = 0; i < equalizerLayBinding.ly4.getChildCount(); i++) {
                equalizerLayBinding.ly4.getChildAt(i).setEnabled(false);
            }
            equalizerLayBinding.fourBand.setEnabled(false);

            for (int i = 0; i < equalizerLayBinding.ly5.getChildCount(); i++) {
                equalizerLayBinding.ly5.getChildAt(i).setEnabled(false);
            }
            equalizerLayBinding.fiveBand.setEnabled(false);

            equalizerLayBinding.bass.setEnabled(false);
            equalizerLayBinding.bassSeekbar.setEnabled(false);
            equalizerLayBinding.verti.setEnabled(false);
            equalizerLayBinding.vertiSeekbar.setEnabled(false);
        }


        equalizerLayBinding.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    equalizerLayBinding.on.setText("ON");
                    for (int i = 0; i < equalizerLayBinding.radioGroup.getChildCount(); i++) {
                        View view = equalizerLayBinding.radioGroup.getChildAt(i);
                        view.setEnabled(true);
                    }
                    for (int i = 0; i < equalizerLayBinding.ly1.getChildCount(); i++) {
                        equalizerLayBinding.ly1.getChildAt(i).setEnabled(true);
                    }
                    equalizerLayBinding.firstBand.setEnabled(true);

                    for (int i = 0; i < equalizerLayBinding.ly2.getChildCount(); i++) {
                        equalizerLayBinding.ly2.getChildAt(i).setEnabled(true);
                    }
                    equalizerLayBinding.secondBand.setEnabled(true);

                    for (int i = 0; i < equalizerLayBinding.ly3.getChildCount(); i++) {
                        equalizerLayBinding.ly2.getChildAt(i).setEnabled(true);
                    }
                    equalizerLayBinding.secondBand.setEnabled(true);

                    for (int i = 0; i < equalizerLayBinding.ly3.getChildCount(); i++) {
                        equalizerLayBinding.ly3.getChildAt(i).setEnabled(true);
                    }
                    equalizerLayBinding.thirdBand.setEnabled(true);

                    for (int i = 0; i < equalizerLayBinding.ly4.getChildCount(); i++) {
                        equalizerLayBinding.ly4.getChildAt(i).setEnabled(true);
                    }
                    equalizerLayBinding.fourBand.setEnabled(true);

                    for (int i = 0; i < equalizerLayBinding.ly5.getChildCount(); i++) {
                        equalizerLayBinding.ly5.getChildAt(i).setEnabled(true);
                    }
                    equalizerLayBinding.fiveBand.setEnabled(true);

                    equalizerLayBinding.bass.setEnabled(true);
                    equalizerLayBinding.bassSeekbar.setEnabled(true);
                    equalizerLayBinding.verti.setEnabled(true);
                    equalizerLayBinding.vertiSeekbar.setEnabled(true);

                    enableEqualizer();
                    startEqualizer(equalizerLayBinding);
                    write(Pref.PrefKey.Equalizer, true);

                } else {
                    equalizerLayBinding.on.setText("OFF");
                    for (int i = 0; i < equalizerLayBinding.radioGroup.getChildCount(); i++) {
                        View view = equalizerLayBinding.radioGroup.getChildAt(i);
                        view.setEnabled(false);
                    }
                    for (int i = 0; i < equalizerLayBinding.ly1.getChildCount(); i++) {
                        equalizerLayBinding.ly1.getChildAt(i).setEnabled(false);
                    }
                    equalizerLayBinding.firstBand.setEnabled(false);

                    for (int i = 0; i < equalizerLayBinding.ly2.getChildCount(); i++) {
                        equalizerLayBinding.ly2.getChildAt(i).setEnabled(false);
                    }
                    equalizerLayBinding.secondBand.setEnabled(false);

                    for (int i = 0; i < equalizerLayBinding.ly3.getChildCount(); i++) {
                        equalizerLayBinding.ly2.getChildAt(i).setEnabled(false);
                    }
                    equalizerLayBinding.secondBand.setEnabled(false);

                    for (int i = 0; i < equalizerLayBinding.ly3.getChildCount(); i++) {
                        equalizerLayBinding.ly3.getChildAt(i).setEnabled(false);
                    }
                    equalizerLayBinding.thirdBand.setEnabled(false);

                    for (int i = 0; i < equalizerLayBinding.ly4.getChildCount(); i++) {
                        equalizerLayBinding.ly4.getChildAt(i).setEnabled(false);
                    }
                    equalizerLayBinding.fourBand.setEnabled(false);

                    for (int i = 0; i < equalizerLayBinding.ly5.getChildCount(); i++) {
                        equalizerLayBinding.ly5.getChildAt(i).setEnabled(false);
                    }
                    equalizerLayBinding.fiveBand.setEnabled(false);

                    equalizerLayBinding.bass.setEnabled(false);
                    equalizerLayBinding.bassSeekbar.setEnabled(false);
                    equalizerLayBinding.verti.setEnabled(false);
                    equalizerLayBinding.vertiSeekbar.setEnabled(false);

                    equalizer.setEnabled(false);
                    write(Pref.PrefKey.Equalizer, false);

                }

            }
        });

    }

    private void getVal(EqualizerLayBinding bottomSheetBinding) {
        write(Pref.PrefKey.FirstBand, bottomSheetBinding.firstBand.getProgress());
        write(Pref.PrefKey.SecondBand, bottomSheetBinding.secondBand.getProgress());
        write(Pref.PrefKey.ThirdBand, bottomSheetBinding.thirdBand.getProgress());
        write(Pref.PrefKey.FourBand, bottomSheetBinding.fourBand.getProgress());
        write(Pref.PrefKey.FiveBand, bottomSheetBinding.fiveBand.getProgress());
    }

    private void startEqualizer(EqualizerLayBinding equalizerLayBinding) {
        short[] banLevelRange = this.equalizer.getBandLevelRange();
        for (int i = 0; i < banLevelRange.length; i++) {
        }


        short lowerCaseLevel = this.equalizer.getBandLevelRange()[0];

        int selectedBand = read("SELECT _BAND", 0);

        switch (selectedBand) {
            case -1:
                setCustomBand(equalizerLayBinding, lowerCaseLevel);
                equalizerLayBinding.custom.setChecked(true);
                break;


            case 0:
                this.equalizer.usePreset((short) 0);
                setBand(equalizerLayBinding, lowerCaseLevel);
                equalizerLayBinding.normal.setChecked(true);
                break;

            case 1:
                this.equalizer.usePreset((short) 1);
                setBand(equalizerLayBinding, lowerCaseLevel);
                equalizerLayBinding.classical.setChecked(true);
                break;

            case 2:
                this.equalizer.usePreset((short) 2);
                setBand(equalizerLayBinding, lowerCaseLevel);
                equalizerLayBinding.dance.setChecked(true);
                break;

            case 3:
                this.equalizer.usePreset((short) 3);
                setBand(equalizerLayBinding, lowerCaseLevel);
                equalizerLayBinding.flog.setChecked(true);
                break;

            case 4:
                this.equalizer.usePreset((short) 4);
                setBand(equalizerLayBinding, lowerCaseLevel);
                equalizerLayBinding.folk.setChecked(true);
                break;


        }
        equalizerLayBinding.firstBand.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                PlayerActivity.this.equalizer.setBandLevel((short) 0, (short) (progress + lowerCaseLevel));
                equalizerLayBinding.tvFirstBand.setText(String.format("%d dB", PlayerActivity.this.equalizer.getBandLevel((short) 0) / 100));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getVal(equalizerLayBinding);
                equalizerLayBinding.custom.setChecked(true);
            }
        });

        equalizerLayBinding.secondBand.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                PlayerActivity.this.equalizer.setBandLevel((short) 1, (short) (progress + lowerCaseLevel));
                equalizerLayBinding.tvSecondband.setText(String.format("%d dB", PlayerActivity.this.equalizer.getBandLevel((short) 1) / 100));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getVal(equalizerLayBinding);
                equalizerLayBinding.custom.setChecked(true);
            }
        });

        equalizerLayBinding.thirdBand.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                PlayerActivity.this.equalizer.setBandLevel((short) 2, (short) (progress + lowerCaseLevel));
                equalizerLayBinding.tvThirdband.setText(String.format("%d dB", PlayerActivity.this.equalizer.getBandLevel((short) 2) / 100));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getVal(equalizerLayBinding);
                equalizerLayBinding.custom.setChecked(true);
            }
        });

        equalizerLayBinding.fourBand.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                PlayerActivity.this.equalizer.setBandLevel((short) 3, (short) (progress + lowerCaseLevel));
                equalizerLayBinding.tvFourband.setText(String.format("%d dB", PlayerActivity.this.equalizer.getBandLevel((short) 3) / 100));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getVal(equalizerLayBinding);
                equalizerLayBinding.custom.setChecked(true);
            }
        });

        equalizerLayBinding.fiveBand.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                PlayerActivity.this.equalizer.setBandLevel((short) 4, (short) (progress + lowerCaseLevel));
                equalizerLayBinding.tvFiveband.setText(String.format("%d dB", PlayerActivity.this.equalizer.getBandLevel((short) 4) / 100));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getVal(equalizerLayBinding);
                equalizerLayBinding.custom.setChecked(true);
            }
        });

        equalizerLayBinding.bassSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bassBoost.setStrength((short) ((int) (((float) progress) * 50.0f)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        equalizerLayBinding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == equalizerLayBinding.custom.getId()) {
                    write("SELECTED_BAND", -1);
                    setCustomBand(equalizerLayBinding, lowerCaseLevel);
                } else if (checkedId == equalizerLayBinding.normal.getId()) {
                    PlayerActivity.this.equalizer.usePreset((short) 0);
                    setBand(equalizerLayBinding, lowerCaseLevel);
                    write("SELECTED_BAND", 0);
                } else if (checkedId == equalizerLayBinding.classical.getId()) {
                    PlayerActivity.this.equalizer.usePreset((short) 1);
                    setBand(equalizerLayBinding, lowerCaseLevel);
                    write("SELECTED_BAND", 1);
                } else if (checkedId == equalizerLayBinding.dance.getId()) {
                    PlayerActivity.this.equalizer.usePreset((short) 2);
                    setBand(equalizerLayBinding, lowerCaseLevel);
                    write("SELECTED_BAND", 2);
                } else if (checkedId == equalizerLayBinding.flog.getId()) {
                    PlayerActivity.this.equalizer.usePreset((short) 3);
                    setBand(equalizerLayBinding, lowerCaseLevel);
                    write("SELECTED_BAND", 3);
                } else if (checkedId == equalizerLayBinding.folk.getId()) {
                    PlayerActivity.this.equalizer.usePreset((short) 4);
                    setBand(equalizerLayBinding, lowerCaseLevel);
                    write("SELECTED_BAND", 4);
                }
            }
        });
    }

    private void enableEqualizer() {
        equalizer = new Equalizer(0, player.getAudioSessionId());
        equalizer.setEnabled(true);
        bassBoost = new BassBoost(0, player.getAudioSessionId());
        bassBoost.setEnabled(true);

        short numbersOfPresent = equalizer.getNumberOfPresets();
        String[] music_styles = new String[numbersOfPresent];
        for (int i = 0; i < numbersOfPresent; i++) {
            music_styles[i] = equalizer.getPresetName((short) i);
        }

        int selectedBand = read("SELECTED_BAND", 0);
        if (selectedBand >= 0) {
            equalizer.usePreset((short) selectedBand);
        } else {
            short lowerBandLevel = this.equalizer.getBandLevelRange()[0];
            equalizer.setBandLevel((short) 0, (short) (read(Pref.PrefKey.FirstBand, 0) + lowerBandLevel));
            equalizer.setBandLevel((short) 1, (short) (read(Pref.PrefKey.SecondBand, 0) + lowerBandLevel));
            equalizer.setBandLevel((short) 2, (short) (read(Pref.PrefKey.ThirdBand, 0) + lowerBandLevel));
            equalizer.setBandLevel((short) 3, (short) (read(Pref.PrefKey.FourBand, 0) + lowerBandLevel));
            equalizer.setBandLevel((short) 4, (short) (read(Pref.PrefKey.FiveBand, 0) + lowerBandLevel));
        }
        short s = (short) read(Pref.PrefKey.bassBoost, 0);

        if (bassBoost != null && bassBoost.getStrengthSupported() && s >= 0 && s <= 1000) {
            try {
                bassBoost.setStrength(s);
            } catch (IllegalArgumentException unused) {

            } catch (IllegalStateException unused2) {

            } catch (UnsupportedOperationException unused3) {

            } catch (RuntimeException unused4) {

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setBand(EqualizerLayBinding equalizerLayoutBinding, short lowerBandLevel) {
        short upperBandLevel = equalizer.getBandLevelRange()[1];

        equalizerLayoutBinding.firstBand.setMax(upperBandLevel - lowerBandLevel);
        equalizerLayoutBinding.secondBand.setMax(upperBandLevel - lowerBandLevel);
        equalizerLayoutBinding.thirdBand.setMax(upperBandLevel - lowerBandLevel);
        equalizerLayoutBinding.fourBand.setMax(upperBandLevel - lowerBandLevel);
        equalizerLayoutBinding.fiveBand.setMax(upperBandLevel - lowerBandLevel);

        equalizerLayoutBinding.firstBand.setProgress(equalizer.getBandLevel((short) 0) - lowerBandLevel);
        equalizerLayoutBinding.secondBand.setProgress(equalizer.getBandLevel((short) 1) - lowerBandLevel);
        equalizerLayoutBinding.thirdBand.setProgress(equalizer.getBandLevel((short) 2) - lowerBandLevel);
        equalizerLayoutBinding.fourBand.setProgress(equalizer.getBandLevel((short) 3) - lowerBandLevel);
        equalizerLayoutBinding.fiveBand.setProgress(equalizer.getBandLevel((short) 4) - lowerBandLevel);

        equalizerLayoutBinding.tvFirstBand.setText(equalizer.getBandLevel((short) 0) / 100 + "dB");
        equalizerLayoutBinding.tvSecondband.setText(equalizer.getBandLevel((short) 1) / 100 + "dB");
        equalizerLayoutBinding.tvThirdband.setText(equalizer.getBandLevel((short) 2) / 100 + "dB");
        equalizerLayoutBinding.tvFourband.setText(equalizer.getBandLevel((short) 3) / 100 + "dB");
        equalizerLayoutBinding.tvFiveband.setText(equalizer.getBandLevel((short) 4) / 100 + "dB");

    }

    @SuppressLint("SetTextI18n")
    private void setCustomBand(EqualizerLayBinding equalizerLayBinding, short lowerBandLevel) {
        short upperBandLevel = equalizer.getBandLevelRange()[1];

        equalizerLayBinding.firstBand.setMax(upperBandLevel - lowerBandLevel);
        equalizerLayBinding.secondBand.setMax(upperBandLevel - lowerBandLevel);
        equalizerLayBinding.thirdBand.setMax(upperBandLevel - lowerBandLevel);
        equalizerLayBinding.fourBand.setMax(upperBandLevel - lowerBandLevel);
        equalizerLayBinding.fiveBand.setMax(upperBandLevel - lowerBandLevel);

        equalizerLayBinding.firstBand.setProgress(read(Pref.PrefKey.FirstBand, 0));
        equalizerLayBinding.secondBand.setProgress(read(Pref.PrefKey.SecondBand, 0));
        equalizerLayBinding.thirdBand.setProgress(read(Pref.PrefKey.ThirdBand, 0));
        equalizerLayBinding.fourBand.setProgress(read(Pref.PrefKey.FourBand, 0));
        equalizerLayBinding.fiveBand.setProgress(read(Pref.PrefKey.FiveBand, 0));

        equalizerLayBinding.tvFirstBand.setText(equalizer.getBandLevel((short) 0) / 100 + "dB");
        equalizerLayBinding.tvSecondband.setText(equalizer.getBandLevel((short) 1) / 100 + "dB");
        equalizerLayBinding.tvThirdband.setText(equalizer.getBandLevel((short) 2) / 100 + "dB");
        equalizerLayBinding.tvFourband.setText(equalizer.getBandLevel((short) 3) / 100 + "dB");
        equalizerLayBinding.tvFiveband.setText(equalizer.getBandLevel((short) 4) / 100 + "dB");

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            // hide the navigation bar
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            // Full screen (hide status bar)
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            // Use immersive style must add this flag
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }


}