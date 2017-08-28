package com.pigdroid.gameboard.view.detail.game;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.pigdroid.gameboard.R;

@SuppressLint("SetJavaScriptEnabled")
public class GameActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }
    
}
