package com.tms.onfilm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

import com.tms.onfilm.R;

public class IntroActivity extends AppCompatActivity {
    private VideoView vvIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        vvIntro = findViewById(R.id.videoview_intro);

        String uriPath = "android.resource://"+  getPackageName() + "/raw/"+ R.raw.logo_video;
        Uri uri = Uri.parse(uriPath);
        vvIntro.setVideoURI(uri);
        vvIntro.start();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }, 4000);
    }

}