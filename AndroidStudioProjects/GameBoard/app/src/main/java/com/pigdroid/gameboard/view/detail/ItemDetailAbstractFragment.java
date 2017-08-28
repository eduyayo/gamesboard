package com.pigdroid.gameboard.view.detail;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couchbase.lite.Document;
import com.pigdroid.gameboard.view.LoadingViewHandleHelper;
import com.pigdroid.gameboard.view.ServiceFragment;

/**
 * A fragment representing a single Item detail screen. This fragment is either
 * contained in a {@link ItemListAbstractActivity} in two-pane mode (on tablets) or a
 * {@link ItemDetailAbstractActivity} on handsets.
 */
public abstract class ItemDetailAbstractFragment extends ServiceFragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */

	/**
	 * The dummy content this fragment is presenting.
	 */
	private String itemId = null;
	private Document item;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ItemDetailAbstractFragment() {
	}
	
	protected Document getItem() {
		if (item == null) {
			item = dataService.getDocument(itemId);
		}
		return item;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	protected void onKickOff() {
		mapItemView(getItem(), getView());
		LoadingViewHandleHelper.hideLoading(getView());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(getItemDetailId(),
				container, false);
		LoadingViewHandleHelper.showLoading(rootView);
		return rootView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (getArguments().containsKey("id")) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			itemId = getArguments().getString("id");
		}
	}

	protected abstract int getItemDetailId();
	
	protected abstract void mapItemView(Document document, View view);
	
}
