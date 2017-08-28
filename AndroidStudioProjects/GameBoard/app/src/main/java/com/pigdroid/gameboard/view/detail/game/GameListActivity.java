package com.pigdroid.gameboard.view.detail.game;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.app.GameBoardApplication;
import com.pigdroid.gameboard.util.DialogFragmentCallback;
import com.pigdroid.gameboard.util.PreferenceUtils;
import com.pigdroid.gameboard.util.StringUtils;
import com.pigdroid.gameboard.view.PlayerViewUtils;
import com.pigdroid.gameboard.view.detail.ItemListAbstractActivity;

public class GameListActivity extends ItemListAbstractActivity implements DialogFragmentCallback {

	private String id;
	
	@Override
	protected void onStart() {
		super.onStart();
		showDialogIf(getIntent());
	}

	protected Class<?> getItemDetailActivityClass() {
		return GameDetailActivity.class;
	}

	protected Fragment getNewItemDetailFragment() {
		return new GameDetailFragment();
	}
	
	protected int getItemDetailContainerId() {
		return R.id.item_detail_container;
	}

	protected int getContentViewId() {
		return R.layout.activity_game_list;
	}

	@Override
	protected Query getDetailQuery(int id) {
		return dataService.getContactQuery();
	}

	@Override
	protected Query getListQuery() {
        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_EMAIL)) {
            return dataService.getGamesByContactQuery(intent.getExtras().getString(Intent.EXTRA_EMAIL));
        }
		return dataService.getGameQuery();
	}

	@Override
	protected String getDetailTag(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onItemSelected(String id) {
		this.id = id;
        Document document = dataService.loadDocument(id);
		if (isWaitingForMe(document)) {
			Bundle args = new Bundle();
			args.putString("id", document.getId());
			JoinGameDialogFragment fragment = new JoinGameDialogFragment();
			fragment.setArguments(args);
			fragment.show(getSupportFragmentManager(), "dialog");
		} else {
			super.onItemSelected(id);
		}
//			if (itemListActivity != null) {
//				itemListActivity.onItemSelected(list.get(position).getDocumentId());
//			}
	}

	private boolean isWaitingForMe(Document document) {
		boolean ret = false;
		if ("WAITING_PLAYERS".equals(document.getProperty("gameEstate"))) {
			List<Map<String, String>> players = (List<Map<String, String>>) document.getProperty("players");
			String userEmail = PreferenceUtils.getUserEmail(this);
			for (Map<String, String> player : players) {
				if (userEmail.equals(player.get("email")) && "INVITED".equals(player.get("status"))) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	@Override
	public void fragmentDialogCallback(String tag, String command) {
		if ("ok".equals(command)) {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					restService.joinGame(id);
					id = null;
                    //TODO listen to the database view/query/listener or whatever changes instead?
					initLoaders(LOADER_ITEM_LIST);
					return null;
				}
			}.execute();
		} else if ("reject".equals(command)) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    restService.leaveGame(id);
                    id = null;
                    //TODO listen to the database view/query/listener or whatever changes instead?
                    initLoaders(LOADER_ITEM_LIST);
                    return null;
                }
            }.execute();
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		showDialogIf(intent);
	}
	
	private void showDialogIf(Intent intent) {
		if (intent.hasExtra("id")) {
			id = intent.getExtras().getString("id");
			intent.removeExtra("id");
			Bundle args = new Bundle();
			args.putString("id", id);
			JoinGameDialogFragment fragment = new JoinGameDialogFragment();
			fragment.setArguments(args);
			fragment.show(getSupportFragmentManager(), "dialog");
		}
	}

	@Override
	protected void onKickOff() {
		super.onKickOff();
        View contactView = findViewById(R.id.contact);
        contactView.setVisibility(View.GONE);
        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_EMAIL)) {
            Document document = dataService.getContactDocumentByEmail(intent.getExtras().getString(Intent.EXTRA_EMAIL));
            Map<String, Object> properties = document.getProperties();
            TextView text = (TextView) contactView.findViewById(android.R.id.text1);
            text.setText(properties.get("email").toString());
            ImageView imageView = (ImageView) contactView.findViewById(android.R.id.icon);
            GameBoardApplication.getPicasso().with(this).load((String)properties.get("imageUrl")).resize(32, 32).into(imageView);
            text = (TextView) contactView.findViewById(R.id.game_count);
            text.setVisibility(View.GONE);
			contactView.findViewById(R.id.game_count_icon).setVisibility(View.GONE);

            text = (TextView) contactView.findViewById(android.R.id.text2);
            text.setText(StringUtils.getAcceptedEstate(properties));
            contactView.setVisibility(View.VISIBLE);
        }
	}
}
