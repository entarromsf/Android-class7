package com.bytedance.videoplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Context context;
    SurfaceView surfaceView;
    private SeekBar skbProgress;
    private Player player;
    private Button btnPause, btnStart, btnLoad, btnPlay;
    private Uri mSelectedVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediaplay);
        context=this;
        surfaceView = (SurfaceView)findViewById(R.id.mediaplay_surfaceView);

        btnStart = (Button)findViewById(R.id.mediaplay_start);
        btnPause = (Button)findViewById(R.id.mediaplay_pause);
        btnLoad = (Button)findViewById(R.id.mediaplay_load);
        btnPlay = (Button)findViewById(R.id.mediaplay_playlocal);

        btnStart.setOnClickListener(new ClickEvent());
        btnPause.setOnClickListener(new ClickEvent());
        btnLoad.setOnClickListener(new ClickEvent());
        btnPlay.setOnClickListener(new ClickEvent());

        skbProgress = (SeekBar) this.findViewById(R.id.mediaplay_seekbar);

        player = new Player(surfaceView, skbProgress);

        try{
            player.mediaPlayer = new MediaPlayer();
            player.mediaPlayer.setDataSource(getResources().openRawResourceFd(R.raw.big_buck_bunny));
            player.mediaPlayer.prepare();
            player.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    player.start();
                    player.mediaPlayer.setLooping(true);
                    Log.d("mediaPlayer", "视频开始播放");
                }
            });
        } catch (IOException e){
            e.printStackTrace();
        }

    }
    class ClickEvent implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            if (view == btnPause) {
                player.pause();
            }
            else if (view == btnStart) {
                player.start();
            }
            else if (view == btnPlay) {
                if(mSelectedVideo==null){
                    Toast.makeText(MainActivity.this,"Ohhh! 还没选视频呢",Toast.LENGTH_SHORT).show();
                }
                else{
                    try{
                        player.mediaPlayer.stop();
                        player.mediaPlayer.release();
                        player.mediaPlayer = null;
                        player.mediaPlayer = new MediaPlayer();
                        player.mediaPlayer.reset();
                        player.mediaPlayer.setDataSource(MainActivity.this,mSelectedVideo);
                        player.mediaPlayer.prepare();
                        player.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                player.start();
                                player.mediaPlayer.setLooping(true);
                                Log.d("mediaPlayer", "视频开始播放");
                            }
                        });
                        ViewGroup vg = findViewById(R.id.main_layout);
                        vg.invalidate();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
            else{
                chooseVideo();
            }
        }
    }

    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),
                2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            if(requestCode == 2) {
                mSelectedVideo = data.getData();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics dm = new DisplayMetrics();
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);

            int screenWidth = dm.widthPixels;

            int screenHeigh = dm.heightPixels;
            ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
            lp.width = screenWidth;
            lp.height = screenWidth * 9/16;

            surfaceView.setLayoutParams(lp);

        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);

            int screenWidth = dm.widthPixels;

            int screenHeigh = dm.heightPixels;
            ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
            lp.width = screenHeigh*16/9;
            lp.height =screenHeigh;
            surfaceView.setLayoutParams(lp);
        }
    }
}
