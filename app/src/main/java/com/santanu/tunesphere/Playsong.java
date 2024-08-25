package com.santanu.tunesphere;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class Playsong extends AppCompatActivity {

    TextView textView;
    ImageView play, next, previous;
    ArrayList<String> songs;
    MediaPlayer mediaPlayer;
    int position;
    SeekBar seekBar;
    Thread updateSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);

        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        songs = intent.getStringArrayListExtra("songPaths");
        String textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("position", 0);

        if (songs != null && !songs.isEmpty() && position >= 0 && position < songs.size()) {
            Uri uri = Uri.parse(songs.get(position));
            mediaPlayer = MediaPlayer.create(this, uri);
            mediaPlayer.start();
        } else {
            textView.setText("Error loading song");
        }

        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Handle progress change
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Handle start of tracking
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeek = new Thread() {
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while (currentPosition < mediaPlayer.getDuration()) {
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                } else {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = (position != 0) ? position - 1 : songs.size() - 1;

                if (songs != null && !songs.isEmpty() && position >= 0 && position < songs.size()) {
                    Uri uri = Uri.parse(songs.get(position));
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                    mediaPlayer.start();

                } else {

                    textView.setText("Error loading song");
                }



                seekBar.setMax(mediaPlayer.getDuration());
                String textContent = new File(songs.get(position)).getName();
                textView.setText(textContent);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = (position != songs.size() - 1) ? position + 1 : 0;

                if (songs != null && !songs.isEmpty() && position >= 0 && position < songs.size()) {
                    Uri uri = Uri.parse(songs.get(position));
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                    mediaPlayer.start();


                } else {

                    textView.setText("Error loading song");
                }



                seekBar.setMax(mediaPlayer.getDuration());
                String textContent = new File(songs.get(position)).getName();
                textView.setText(textContent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (updateSeek != null) {
            updateSeek.interrupt();
        }
    }
}
