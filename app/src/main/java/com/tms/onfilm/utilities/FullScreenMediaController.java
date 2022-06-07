package com.tms.onfilm.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

import com.tms.onfilm.R;
import com.tms.onfilm.activities.FullScreenVideoActivity;
import com.tms.onfilm.listeners.CloseFullScreenVideoListener;
import com.tms.onfilm.listeners.FullScreenVideoListener;

public class FullScreenMediaController extends MediaController {
    private ImageButton fullScreen;
    private String isFullScreen;
    private FullScreenVideoListener fullScreenVideoListener;
    private CloseFullScreenVideoListener closeFullScreenVideoListener;

    public FullScreenMediaController(Context context, FullScreenVideoListener fullScreenVideoListener) {
        super(context);
        this.fullScreenVideoListener = fullScreenVideoListener;
    }

    public FullScreenMediaController(Context context, CloseFullScreenVideoListener closeFullScreenVideoListener) {
        super(context);
        this.closeFullScreenVideoListener = closeFullScreenVideoListener;
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        fullScreen = new ImageButton (super.getContext());

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        params.rightMargin = 80;
        addView(fullScreen, params);

        isFullScreen =  ((Activity)getContext()).getIntent().
                getStringExtra("fullScreenInd");

        if("y".equals(isFullScreen) && isFullScreen != null){
            fullScreen.setImageResource(R.drawable.ic_fullscreen_exit);
        }else{
            fullScreen.setImageResource(R.drawable.ic_fullscreen);
        }

        fullScreen.setOnClickListener(v -> {
            if("y".equals(isFullScreen)){
                closeFullScreenVideoListener.backFullScreenVideoActivity();
            }else{
                fullScreenVideoListener.moveFullScreenVideoActivity();
            }
        });
    }
}
