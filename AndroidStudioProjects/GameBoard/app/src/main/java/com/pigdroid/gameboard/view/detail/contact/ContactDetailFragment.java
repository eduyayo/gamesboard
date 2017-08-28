package com.pigdroid.gameboard.view.detail.contact;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;

import com.couchbase.lite.Document;
import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.view.detail.ItemDetailAbstractFragment;

public class ContactDetailFragment extends ItemDetailAbstractFragment {

	private BroadcastReceiver chatBroadcastReceiver = null;
	
	protected int getItemDetailId() {
		return R.layout.fragment_contact_detail;
	}

	@Override
	protected void mapItemView(final Document document, final View view) {
//		if (document != null) {
//			final TextView localView = (TextView) view.findViewById(R.id.item_detail);
//			localView.setText("...");
//			getActivity().runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					GameBoardApplication app = ((GameBoardApplication) getActivity().getApplication());
////					int count = app.getCountGames(document.getProperties().get("email").toString());
////					localView.setText(document.getProperties().get("login").toString() + " " + count);
//					localView.setText(document.getProperties().get("login").toString());
//				}
//			});
//		}		
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		final TabHost tabHost = (TabHost) view.findViewById(android.R.id.tabhost);
    	
		tabHost.setup();
//        TabHost.TabSpec spec = tabHost.newTabSpec("Chat");
//        spec.setIndicator("Chat");
//        spec.setContent(new TabHost.TabContentFactory() {
//			@Override
//			public View createTabContent(String tag) {
//				return inflater.inflate(R.layout.game_chat, null);
//			}
//		});
//        tabHost.addTab(spec);

		TabHost.TabSpec spec = tabHost.newTabSpec("Games");
        spec.setIndicator("Games");
        spec.setContent(new TabHost.TabContentFactory() {

            @Override
            public View createTabContent(String tag) {
                return (new Button(getActivity()));
            }
        });
        tabHost.addTab(spec);
        
        return view;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(chatBroadcastReceiver);
	}

	@Override
	protected void onKickOff() {
		super.onKickOff();
//		chatBroadcastReceiver = ChatHelper.initChatController(getActivity(), getView(), (String) getItem().getProperties().get("email"));
	}
}
