package com.pigdroid.gameboard.view.detail.game;

import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.QueryRow;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.pigdroid.game.GameControllerProvider;
import com.pigdroid.game.turn.controller.TurnGameController;
import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.app.GameBoardApplication;
import com.pigdroid.gameboard.util.StringUtils;
import com.pigdroid.gameboard.view.PlayerViewUtils;
import com.pigdroid.gameboard.view.detail.ItemListAbstractFragment;

public class GameListFragment extends ItemListAbstractFragment {

	@Override
	protected int getItemLayoutId() {
		return R.layout.item_game;
	}

	@Override
	protected void mapItemView(View view, QueryRow item, String tag) {
		if (tag == null && item != null && item.getDocument() != null) {
			TextView text = (TextView) view.findViewById(android.R.id.text1);
			final Map<String, Object> properties = item.getDocument().getProperties();
			text.setText(properties.get("gameName").toString());
			text = (TextView) view.findViewById(android.R.id.text2);
			String value = (String) properties.get("gameEstate");
			text.setText(StringUtils.getGameEstateDesc(value, properties, getActivity()));
			ImageView imageView = (ImageView) view.findViewById(android.R.id.icon);
            Drawable d = imageView.getDrawable();
            GameBoardApplication.getPicasso().load("screenShot://" + item.getDocumentId()).error(getResources().getDrawable(R.drawable.ic_launcher)).into(imageView);
			final ViewGroup group = (ViewGroup) view.findViewById(R.id.player_list);
			group.removeAllViews();
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					TurnGameController<?> controller = GameControllerProvider.createController(properties.get("gameName").toString());
					controller.loadModelFromSerialized((String) properties.get("saveGame"));
					PlayerViewUtils.setPlayerStatus(getActivity(), group, controller);
					return null;
				}
			}.execute();
//			Picasso.with(getActivity()).load(properties.get("imageUrl").toString()).resize(32, 32).into(imageView );
		} else {
			//Compound key, must be a detail view (nested view)
		}
	}

	@Override
	protected int getItemListLayout() {
		return R.layout.fragment_game_list;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		view.findViewById(R.id.add).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (dataService.getEffectiveContactCount() == 0) {
SnackbarManager.show(Snackbar.with(getActivity()).text("You still have no buddies to play with!").color(Color.RED).textColor(Color.WHITE));
				} else {
					Intent intent = new Intent(getActivity(), NewGameActivity.class);
					startActivityForResult(intent, 123);
				}
			}
		});
		ListView listView = null;
	}

}
