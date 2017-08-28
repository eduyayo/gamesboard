package com.pigdroid.gameboard.view.detail.game;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.QueryEnumerator;
import com.pigdroid.game.GameControllerProvider;
import com.pigdroid.game.controller.GameController;
import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.app.GameBoardApplication;
import com.pigdroid.gameboard.util.PreferenceUtils;
import com.pigdroid.gameboard.view.ServicesActivity;

public class NewGameActivity extends ServicesActivity {
	
	private ArrayAdapter<Map<String, Object>> adapter;
	private ScrollView scrollView;
	Spinner gameSpinner;

	private boolean isValid() {
		boolean ret = true;
		int max = scrollView.getChildCount();
		for (int i = 0; i < max; i++) {
			Spinner spiner = (Spinner) scrollView.getChildAt(i);
			if (spiner.getSelectedItem() == null) {
				ret = false;
			}
		}
		return ret;
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
    }

	@Override
	protected void onKickOff() {
		super.onKickOff();

		gameSpinner = (Spinner) findViewById(R.id.game);
		gameSpinner.setAdapter(new SpinnerIconAdapter(this, R.layout.spinner_icon_value_layout, GameControllerProvider.getGameNameList(), GameControllerProvider.getGameIconList()));

		findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
		findViewById(R.id.ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String gameName = gameSpinner.getSelectedItem().toString();
				final GameController<?> controller = GameControllerProvider.createController(gameName);
				if (isValid()) {
					findViewById(R.id.button_bar).setVisibility(View.GONE);
					int max = scrollView.getChildCount();
					for (int i = 0; i < max; i++) {
						Spinner spiner = (Spinner) scrollView.getChildAt(i);
						Map<String, Object> properties = (Map<String, Object>) spiner.getSelectedItem();
						String name = (String) properties.get("email");
						Player player = new HumanPlayer(name, name);
						controller.addPlayer(player);
					}
					HumanPlayer localPlayer = new HumanPlayer();
					String m = PreferenceUtils.getUserEmail(NewGameActivity.this);
					localPlayer.setName(m);
					localPlayer.setEmail(m);
					controller.addPlayer(localPlayer);
					controller.joinPlayer(m);
					new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							controller.commit();
							Document document = restService.addGame(controller.getSerializedModel(), gameName);
							if (document != null) {
								finish();
							} else {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										findViewById(R.id.button_bar).setVisibility(View.VISIBLE);
									}
								});
							}
							return null;
						}

					}.execute();
				}
			}
		});

		gameSpinner.setSelection(getSelectedPositionFromIntent());

		scrollView = (ScrollView) findViewById(android.R.id.list);
		Spinner spiner = (Spinner) scrollView.getChildAt(0);
		spiner = new Spinner(this);
		spiner.setAdapter(getContactAdapter());
		scrollView.addView(spiner);

	}

	private int getSelectedPositionFromIntent() {
    	int ret = 0;
    	String gameName = null;
        if (getIntent() != null && getIntent().hasExtra(Intent.EXTRA_TITLE)) {
        	gameName = getIntent().getExtras().getString(Intent.EXTRA_TITLE);
        }
        if (gameName == null) {
        	gameName = "Checkers";
        }
        int max = gameSpinner.getCount();
        for (int i = 0; i < max; i++) {
        	if (gameSpinner.getItemAtPosition(i).equals(gameName)) {
        		ret = i;
        		break;
        	}
        }
        return ret;
	}

	public ArrayAdapter<Map<String, Object>> getContactAdapter() {
    	if (adapter == null) {
	    	QueryEnumerator runner = null;
	    	try {
				runner = dataService.getEffectiveContactQuery().run();
			} catch (CouchbaseLiteException e) {
				throw new RuntimeException(e);
			}
	    	adapter = new ArrayAdapter<Map<String, Object>>(this,
					android.R.layout.simple_spinner_item,
					android.R.id.text1) {
				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					View view = convertView;
					if (view == null) {
						view = super.getView(position, convertView, parent);
					}
					Map<String, Object> item = getItem(position);
					((TextView) view.findViewById(android.R.id.text1)).setText(item.get("email").toString());
					return view;
				};
				@Override
				public View getDropDownView(int position,
						View convertView, ViewGroup parent) {
					View view = convertView;
					if (view == null) {
						view = super.getDropDownView(position, convertView, parent);
					}
					Map<String, Object> item = getItem(position);
					((TextView) view.findViewById(android.R.id.text1)).setText(item.get("email").toString());
					return view; //TODO Gotta add the dropdowns to a scrollable view different than a ListView because there's almost no way of finding the pos and item in the father to
				}
	    	};
	    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	while (runner.hasNext()) {
	    		adapter.add(runner.next().getDocument().getProperties());
	    	}
    	}
    	return adapter;
    }
    
}
