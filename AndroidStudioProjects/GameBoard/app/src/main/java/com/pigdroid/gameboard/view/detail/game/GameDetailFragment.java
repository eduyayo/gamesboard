package com.pigdroid.gameboard.view.detail.game;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.app.GameBoardApplication;
import com.pigdroid.gameboard.util.JSONUtils;
import com.pigdroid.gameboard.view.detail.ItemDetailAbstractFragment;
import com.pigdroid.gameboard.view.detail.game.tile.TileBoardGameFragmentController;
import com.pigdroid.hub.model.message.GameMessage;

public class GameDetailFragment extends ItemDetailAbstractFragment {

	TileBoardGameFragmentController controllerFragment = null;
	private BroadcastReceiver gameBroadcastReceiver;
	private String modelId = null;
    private boolean viewCreated;

    protected int getItemDetailId() {
        return R.layout.fragment_game_detail;
    }

    @Override
    protected void mapItemView(Document document, View view) {
        this.modelId = (String) document.getProperty("modelId");
//		if (document != null) {
//			GameBoardApplication app = ((GameBoardApplication) getActivity().getApplication());
//			int count = app.getCountGames(document.getProperties().get("email").toString());
//			((TextView) view.findViewById(R.id.item_detail))
//					.setText(document.getProperties().get("login").toString() + " " + count);
//		}		
    }

	private void createControllerIf() {
		if (controllerFragment == null) {
			controllerFragment = new TileBoardGameFragmentController(this);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        if (controllerFragment != null) {
            controllerFragment.onDestroy();
            controllerFragment = null;
        }
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
        this.viewCreated = true;
	}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.viewCreated = false;
    }

    private void processPendingMessages() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				final List<Document> pendingMessageDocuments = dataService.getMessagesByGame(modelId);
				final ObjectMapper mapper = JSONUtils.createObjectMapper();
				for (final Document messageDocument : pendingMessageDocuments) {
					if (messageDocument != null && messageDocument.getProperties() != null) {
						final GameMessage message = mapper.convertValue(messageDocument.getProperties(), GameMessage.class);
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								controllerFragment.processMessage(message);
							}
						});
						try {
							messageDocument.delete();
						} catch (CouchbaseLiteException e) {
							throw new RuntimeException(e);
						}
					}
				}
				return null;
			}
		}.execute();
	}

	private void registerBroadcastReceiver() {
		gameBroadcastReceiver =  new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle extras = intent.getExtras();
				String destModelId = extras.getString("modelId");
				if (modelId != null && modelId.equals(destModelId)) {
					//gotta handle this message
					Document messageDocument = dataService.loadDocument(extras.getString("id"));
					ObjectMapper mapper = JSONUtils.createObjectMapper();
					final GameMessage message = mapper.convertValue(messageDocument.getProperties(), GameMessage.class);
					intent.putExtra(Intent.EXTRA_DATA_REMOVED, true);
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							controllerFragment.processMessage(message);
						}
					});
				}
			}
		};
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                gameBroadcastReceiver,
                new IntentFilter(GameBoardApplication.INTENT_ACTION_GAME_MSG));
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		registerBroadcastReceiver();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		unregisterBroadcastReceiver();
	}
	
	private void unregisterBroadcastReceiver() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(gameBroadcastReceiver);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (controllerFragment != null) {
			controllerFragment.onPause();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
        kickOffIf();
	}

    @Override
    protected void kickOffIf() {
        if (viewCreated) {
            super.kickOffIf();
        }
    }

    @Override
    protected void onKickOff() {
        super.onKickOff();
        createControllerIf();
        controllerFragment.onViewCreated(getView(), null);
        controllerFragment.setGame(dataService.getGameDocumentByModelId(modelId));
        processPendingMessages();
    }

}
