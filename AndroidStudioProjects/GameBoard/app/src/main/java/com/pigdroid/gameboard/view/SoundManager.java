package com.pigdroid.gameboard.view;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by edu on 03/06/2015.
 */
public class SoundManager {

    private SoundPool soundPool = null;
    private Map<Integer, Integer> soundIds = new HashMap<Integer, Integer>();
    private Context context = null;

    public SoundManager(Context context) {
        if (android.os.Build.VERSION.SDK_INT > 20) {
            soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        this.context = context;
    }

    public void release() {
        soundPool.release();
        context = null;
        soundIds = null;
    }

    public void play(int resId) {
        int soundId = getSoundId(resId);
        soundPool.play(soundId, 1.0F, 1.0F, 0, 0, 1.0F);
    }

    private int getSoundId(int resId) {
        Integer ret = soundIds.get(resId);
        if (ret == null) {
            soundIds.put(resId, ret = soundPool.load(context, resId, 1));
        }
        return ret;
    }

}
