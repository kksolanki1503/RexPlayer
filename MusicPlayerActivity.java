package com.example.rexplayer;

import androidx.appcompat.app.AppCompatActivity;
import pl.droidsonroids.gif.GifImageView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView titleTv,currentTimeTv,totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay,nextBtn, previousBtn;
    GifImageView musicIcon;
    ArrayList<AudioModel> songList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = RexPlayer.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        titleTv = findViewById(R.id.songs_title);
        currentTimeTv = findViewById(R.id.current_time);
        seekBar = findViewById(R.id.seek_bar);
        totalTimeTv = findViewById(R.id.total_time);
        pausePlay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);
        titleTv.setSelected(true);
        songList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");
        setResourcesWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable(){
            public void run(){
                if(mediaPlayer != null)
                {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(converToMMSS(mediaPlayer.getCurrentPosition()+""));
                            if(mediaPlayer.isPlaying())
                            {
                                pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
                            }else{
                                pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                            }

                }
                new Handler().postDelayed(this ,100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    void setResourcesWithMusic(){
        currentSong = songList.get(RexPlayer.currentIndex);
        titleTv.setText(currentSong.getTitle());
        totalTimeTv.setText(converToMMSS(currentSong.getDuration()));

        pausePlay.setOnClickListener(v->pausePlay());
        nextBtn.setOnClickListener(v->playNextSong());
        previousBtn.setOnClickListener(v->playPriviousSong());

        playMusic();
    }

    private void playMusic(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch(IOException e){
            e.printStackTrace();
        }

    }
    private void playNextSong(){
        if(RexPlayer.currentIndex == songList.size()-1){
            return;
        }
        RexPlayer.currentIndex +=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }
    private void playPriviousSong()
    {
        if(RexPlayer.currentIndex == 0){
            return;
        }
        RexPlayer.currentIndex -=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }
    private void pausePlay(){
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
        }
        else
            mediaPlayer.start();
    }
    public static String converToMMSS(String duration)
    {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

}