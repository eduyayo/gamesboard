package com.pigdroid.gameboard.view.detail.game;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.util.DialogFragmentCallback;
import com.pigdroid.gameboard.view.detail.ItemDetailAbstractActivity;

public class GameDetailActivity extends ItemDetailAbstractActivity implements DialogFragmentCallback {
	
	protected Class<?> getParentActivityClass() {
		return GameListActivity.class;
	}

	protected int getItemDetailContainerId() {
		return R.id.item_detail_container;
	}

	protected Fragment getNewItemDetailFragment() {
		return new GameDetailFragment();
	}

	protected int getContentViewId() {
		return R.layout.activity_game_detail;
	}

	@Override
	public void fragmentDialogCallback(String tag, String command) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                restService.leaveGame(getIntent().getStringExtra("id"));
                return null;
            }
        }.execute();
        finish();

	}

}
