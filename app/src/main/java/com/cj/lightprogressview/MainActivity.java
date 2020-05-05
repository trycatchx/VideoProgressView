package com.cj.lightprogressview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cj.videoprogressview.LightProgressView;
import com.cj.videoprogressview.VolumeProgressView;

public class MainActivity extends AppCompatActivity {
    LightProgressView mLightPeogressView;
    VolumeProgressView mVolumeProgressView;
    AudioManager mAudioManager;
    GestureDetector mGestureDetector;
    protected int mStreamVolume;
    protected float mBrightness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mLightPeogressView = findViewById(R.id.lpv);
        mVolumeProgressView = findViewById(R.id.vpv);
        mBrightness = PlayerUtils.scanForActivity(this).getWindow().getAttributes().screenBrightness;
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this, new MyGestureListener());

        mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mBrightness = MainActivity.this.getWindow().getAttributes().screenBrightness;
        slideToChangeBrightness(0);
        slideToChangeVolume(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    protected class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private boolean mFirstTouch;
        private boolean mChangeBrightness;
        private boolean mChangeVolume;

        @Override
        public boolean onDown(MotionEvent e) {
            mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mBrightness = MainActivity.this.getWindow().getAttributes().screenBrightness;
            mFirstTouch = true;
            mChangeBrightness = false;
            mChangeVolume = false;
            return true;
        }


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (e1 == null || e2 == null) return false;
            float deltaY = e1.getY() - e2.getY();
            if (mFirstTouch) {
                if (Math.abs(distanceX) < Math.abs(distanceY)) {
                    if (e2.getX() > PlayerUtils.getScreenWidth(MainActivity.this, true) / 2) {
                        mChangeVolume = true;
                    } else {
                        mChangeBrightness = true;
                    }
                }
                mFirstTouch = false;
            }
           if (mChangeBrightness) {
                slideToChangeBrightness(deltaY);
            } else if (mChangeVolume) {
                slideToChangeVolume(deltaY);
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }
    }

    protected void slideToChangeBrightness(float deltaY) {
        Window window = PlayerUtils.scanForActivity(this).getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        int height = PlayerUtils.getScreenHeight(getApplicationContext(),false);
        if (mBrightness == -1.0f) mBrightness = 0.5f;
        float brightness = deltaY * 2 / height * 1.0f + mBrightness;
        if (brightness < 0) {
            brightness = 0f;
        }
        if (brightness > 1.0f) brightness = 1.0f;
        mLightPeogressView.setProgress(brightness);
        attributes.screenBrightness = brightness;
        window.setAttributes(attributes);
    }

    protected void slideToChangeVolume(float deltaY) {
        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int height = PlayerUtils.getScreenHeight(getApplicationContext(),false);
        float deltaV = deltaY * 2 / height * streamMaxVolume;
        float index = mStreamVolume + deltaV;
        if (index > streamMaxVolume) index = streamMaxVolume;
        if (index < 0) {
            index = 0;
        }
        mVolumeProgressView.setProgress(index / streamMaxVolume);

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) index, 0);
    }






}
