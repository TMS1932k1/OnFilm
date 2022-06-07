package com.tms.onfilm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.tms.onfilm.R;
import com.tms.onfilm.broadcasts.BroadCastInternet;
import com.tms.onfilm.listeners.CloseFullScreenVideoListener;
import com.tms.onfilm.utilities.FullScreenMediaController;

public class FullScreenVideoActivity extends Activity implements CloseFullScreenVideoListener {
    private VideoView vvEpisode;
    private MediaController mediaController;

    private BroadCastInternet broadCastInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        broadCastInternet = new BroadCastInternet();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_screen_video);

        String linkEpisode = getIntent().getStringExtra("object_link_episode");
        int positionCurrentEpisode = getIntent().getIntExtra("current_position_episode", 0);

        vvEpisode = findViewById(R.id.videoview_episode);

        Uri videoUri = Uri.parse(linkEpisode);
        vvEpisode.setVideoURI(videoUri);

        mediaController = new FullScreenMediaController(this, this);
        mediaController.setAnchorView(vvEpisode);

        vvEpisode.setMediaController(mediaController);
        vvEpisode.seekTo(positionCurrentEpisode);
        vvEpisode.start();
    }

    @Override
    public void backFullScreenVideoActivity() {
        onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadCastInternet, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadCastInternet);
    }
}