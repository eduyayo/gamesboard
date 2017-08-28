package com.pigdroid.gameboard.view.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.couchbase.lite.QueryRow;
import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.view.DrawerActivity;
import com.pigdroid.gameboard.view.LoadingViewHandleHelper;
import com.pigdroid.gameboard.view.ServiceListFragment;
import com.pigdroid.gameboard.view.detail.ItemListAbstractActivity.OnLoadListener;

/**
 * A list fragment representing a list of Items. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link ItemDetailAbstractFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public abstract class ItemListAbstractFragment extends ServiceListFragment implements OnLoadListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int activatedPosition = ListView.INVALID_POSITION;

	private ItemListAbstractActivity itemListActivity;

	private ArrayAdapter<QueryRow> adapter = null;

	protected List<QueryRow> list;
	
	private Map<String, String> data = new HashMap<String, String>();
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ItemListAbstractFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		list = new ArrayList<QueryRow>();
		adapter = new ArrayAdapter<QueryRow>(getActivity(),
				getItemLayoutId(),
				android.R.id.text1, list) {
			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {
				View view = convertView;
				if (view == null) {
					view = getActivity().getLayoutInflater()
							.inflate(
									getItemLayoutId(),
									(ViewGroup) ItemListAbstractFragment.this.getView(), false);
				}
				QueryRow item = list.get(position);
				view.setTag(item.getKey());
				animateMapItem(view, item, null);
				return view;
			}
			
			@Override
			public boolean hasStableIds() {
				return true;
			}
			
			@Override
			public long getItemId(int position) {
				QueryRow item = getItem(position);
				if (item != null) {
					return item.getDocumentId().hashCode();
				}
				return -1;
			}
			
		};
		setListAdapter(adapter);
	}

	protected abstract int getItemLayoutId();
	protected abstract void mapItemView(View view, QueryRow item, String tag);

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// In two-pane mode, list items should be given the
		// 'activated' state when touched.
		if (activity instanceof DrawerActivity) {
			if (((DrawerActivity) activity).isTwoPane()) {
				setActivateOnItemClick(true);
			}
		}
		if (activity instanceof ItemListAbstractActivity) {
			this.itemListActivity = (ItemListAbstractActivity) activity;
			itemListActivity.setOnLoadListener(this);
		}
		LoadingViewHandleHelper.showLoading(getView());
		
	}

	@Override
	public void onDetach() {
		super.onDetach();
		itemListActivity.setOnLoadListener(null);
		itemListActivity = null;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		if (itemListActivity != null) {
			itemListActivity.onItemSelected(list.get(position).getDocumentId());
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (activatedPosition != AdapterView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, activatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE
						: AbsListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == AdapterView.INVALID_POSITION) {
			getListView().setItemChecked(activatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}
		activatedPosition = position;
	}
	
//	public void onLoad(List<QueryRow> data) {
//		list.clear();
//		int max = data.size();
//		for (int i = 0; i < max; i++) {
//			list.add(data.get(i));
//		}
//	    adapter.notifyDataSetChanged();
//	}
	
	@Override
	public void handleListElement(QueryRow row) {
		View view = getView().findViewWithTag(row.getKey());
		if (view == null) {
			if (!list.contains(row)) {
				list.add(row);
			    adapter.notifyDataSetChanged();
				LoadingViewHandleHelper.hideLoading(getView());
			}
		} else {
			animateMapItem(view, row, null);
		}
	}
	
	@Override
	public void handleDetailElement(String tag, QueryRow row) {
		View view = getView().findViewWithTag(row.getKey());
		putData(tag + row.getKey(), row.getValue().toString());
		if (view != null) {
			animateMapItem(view, row, tag);
		}
	}

	@SuppressLint("NewApi")
	private void animateMapItem(View view, QueryRow row, String tag) {
        if (android.os.Build.VERSION.SDK_INT >= 19) {
        	TransitionManager.beginDelayedTransition((ViewGroup) view);
        }
		mapItemView(view, row, tag);
		
	}

	private void putData(String key, String value) {
		data.put(key, value);
	}
	
	protected String getData(String key, String def) {
		String ret = data.get(key);
		if (ret == null) {
			ret = def;
		}
		return ret;
	}
	
	protected String getData(String key) {
		String ret = data.get(key);
		if (ret == null) {
			ret = getResources().getString(R.string.ellipsis);
		}
		return ret;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(getItemListLayout(), null);
	}

	protected abstract int getItemListLayout();

	protected void clearList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                adapter.notifyDataSetChanged();
            }

        });
	}
	
}
