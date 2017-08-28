package com.pigdroid.gameboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class NotConnectedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_connected);
        findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				finish();
	            System.runFinalizersOnExit(true);
				System.exit(0);
			}
		});
    }
    
}
