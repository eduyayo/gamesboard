package com.pigdroid.gameboard.view.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.couchbase.lite.QueryRow;
import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.view.LoaderActivity;

/**
 * An activity representing a list of Items. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ItemDetailAbstractActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListAbstractFragment} and the item details (if present) is a
 * {@link ItemDetailAbstractFragment}.
 * <p>
 * This activity also implements the required {@link ItemListAbstractFragment.Callbacks}
 * interface to listen for item selections.
 */
public abstract class ItemListAbstractActivity extends LoaderActivity {
	
	private OnLoadListener onLoadListener = null;
	
	public static interface OnLoadListener {
		void handleListElement(QueryRow row);
		void handleDetailElement(String tag, QueryRow row);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewId());

		if (findViewById(getItemDetailContainerId()) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			setTwoPane(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

//	private int getItemListId() {
//		return R.id.item_list;
//	}

	/**
	 * the item with the given ID was selected.
	 */
	public void onItemSelected(String id) {
		if (isTwoPane()) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString("id", id);
			Fragment fragment = getNewItemDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
				.replace(getItemDetailContainerId(), fragment)
				.commit();
		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, getItemDetailActivityClass());
			detailIntent.putExtra("id", id);
			startActivity(detailIntent);
			overridePendingTransition(R.anim.from_right, R.anim.to_left);
		}
	}

	protected abstract Class<?> getItemDetailActivityClass();
	
	protected abstract Fragment getNewItemDetailFragment();

	protected abstract int getItemDetailContainerId();

	protected abstract int getContentViewId();

	public void setOnLoadListener(OnLoadListener loadListener) {
		this.onLoadListener = loadListener;
	}
	
	@Override
	protected void handleListElement(QueryRow row) {
		if (onLoadListener != null) {
			onLoadListener.handleListElement(row);
		}
	}
	
	@Override
	protected void handleDetailElement(String tag, QueryRow row) {
		if (onLoadListener != null) {
			onLoadListener.handleDetailElement(tag, row);
		}
	}
	
}
