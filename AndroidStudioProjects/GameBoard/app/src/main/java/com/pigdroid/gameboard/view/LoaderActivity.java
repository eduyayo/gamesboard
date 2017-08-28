package com.pigdroid.gameboard.view;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import com.couchbase.lite.Query;
import com.couchbase.lite.QueryRow;
import com.pigdroid.gameboard.loader.DataLoader;

public abstract class LoaderActivity extends DrawerActivity implements LoaderCallbacks<List<QueryRow>>{

	protected static final int LOADER_ITEM_LIST = Integer.MAX_VALUE;
	
	@Override
	protected void onStart() {
		super.onStart();
		LoadingViewHandleHelper.showLoading(
				getWindow().getDecorView().findViewById(android.R.id.content));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onKickOff() {
		super.onKickOff();
		initLoaders(LOADER_ITEM_LIST);
	}

	protected void initLoaders(int ... loaderIds) {
		if (loaderIds != null) {
			int max = loaderIds.length;
			android.support.v4.app.LoaderManager manager = getSupportLoaderManager();
			for (int i = 0; i < max; i++) {
				int id = loaderIds[i];
				if (manager.getLoader(id) == null) {
					manager.destroyLoader(id);
					manager.initLoader(id, null, this);
				}
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public Loader<List<QueryRow>> onCreateLoader(
			int id, Bundle bundle) {
		DataLoader listLoader = null;
		if (id == LOADER_ITEM_LIST) {
			listLoader = new DataLoader(getListQuery());
		} else {
			listLoader = new DataLoader(getDetailQuery(id), getDetailTag(id));
		}
		return listLoader;
	}

	protected abstract Query getDetailQuery(int id);
	protected abstract String getDetailTag(int id);

	protected abstract Query getListQuery();

	@Override
	public void onLoadFinished(
			android.support.v4.content.Loader<List<QueryRow>> ploader,
			final List<QueryRow> data) {
		getSupportLoaderManager().destroyLoader(ploader.getId());
		LoadingViewHandleHelper.hideLoading(
				getWindow().getDecorView().findViewById(android.R.id.content));
		final DataLoader loader = (DataLoader) ploader;
		final String tag = loader.getTag();
		if (tag == null) {
			for (final QueryRow row : data) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						LoaderActivity.this.handleListElement(row);
					}
				});
			}
		} else {
			for (final QueryRow row : data) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (tag == null) {
							throw new RuntimeException();
						}
						LoaderActivity.this.handleDetailElement(tag, row);
					}
				});
			}
		}
	}

	protected abstract void handleDetailElement(String tag, QueryRow row);

	protected abstract void handleListElement(QueryRow row);

	@Override
	public void onLoaderReset(Loader<List<QueryRow>> loader) {
		if (loader.getId() == LOADER_ITEM_LIST) {
			//TODO ????
		}
	}

}
