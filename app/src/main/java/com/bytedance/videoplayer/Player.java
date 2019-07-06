package com.bytedance.videoplayer;

import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;

public class Player  implements OnBufferingUpdateListener,OnCompletionListener, MediaPlayer.OnPreparedListener, SurfaceHolder.Callback{
    private int videoWidth;
    private int videoHeight;
    public MediaPlayer mediaPlayer = null;
    private SurfaceHolder surfaceHolder;
    private SeekBar skbProgress;

    private Timer mTimer=new Timer();
    public Player(SurfaceView surfaceView,SeekBar skbProgress)
    {
        this.skbProgress=skbProgress;
        this.skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        surfaceHolder=surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run()
        {
            if(mediaPlayer==null)
                return;
            if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
                handleProgress.sendEmptyMessage(0);
            }
        }
    };

    Handler handleProgress = new Handler() {
        public void handleMessage(Message msg) {
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();

            if (duration > 0) {
                long pos = skbProgress.getMax() * position / duration;
                skbProgress.setProgress((int) pos);
            }
        };
    };



    public void pause()
    {
        mediaPlayer.pause();
    }

    public void start()
    {
        mediaPlayer.start();
    }


    public void seekTo(int ms)
    {
        mediaPlayer.seekTo(ms);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
            Log.d("mediaPlayer", "视频准备就绪");
        } catch (Exception e) {
            Log.e("mediaPlayer", "error", e);
        }

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
        Log.d("mediaPlayer", "surface变化");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("mediaPlayer", "surface销毁");
    }



    @Override
    public void onPrepared(MediaPlayer mp) {
        videoWidth = mediaPlayer.getVideoWidth();
        videoHeight = mediaPlayer.getVideoHeight();
        if (videoHeight != 0 && videoWidth != 0) {
            mp.start();
        }
        Log.d("mediaPlayer", "视频启动");
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("mediaPlayer", "视频完成");
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        skbProgress.setSecondaryProgress(percent);
        int currentProgress=skbProgress.getMax()*mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration();
        Log.d("播放进度"+currentProgress+"% play", "缓冲进度"+percent + "% buffer");
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
            this.progress = progress * mediaPlayer.getDuration()/seekBar.getMax();
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekTo(progress);
        }
    }
}
