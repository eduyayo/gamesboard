package com.pigdroid.gameboard.view;

import com.couchbase.lite.QueryRow;
import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.app.GameBoardApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

public class KillableActivity extends AppCompatActivity {

	protected static final String INTENT_ACTION_KILL = "com.pigdroid.gameboard.KILL";

	private BroadcastReceiver killBroadcastReceiver = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		this.killBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				finish();
			}
		};
		LocalBroadcastManager.getInstance(this).registerReceiver(
				killBroadcastReceiver, 
				new IntentFilter(INTENT_ACTION_KILL));
	}

	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(killBroadcastReceiver);
		super.onDestroy();
	}

}
