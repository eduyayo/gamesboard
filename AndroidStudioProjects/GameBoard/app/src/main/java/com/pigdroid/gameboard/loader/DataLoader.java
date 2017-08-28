package com.pigdroid.gameboard.loader;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.content.AsyncTaskLoader;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;

public class DataLoader extends AsyncTaskLoader<List<QueryRow>> {

	private Query query;
	private String tag = null;

	public DataLoader(Query query) {
		super(((AndroidContext) query.getDatabase().getManager().getContext()).getWrappedContext());
		this.query = query;
	}
	
	public DataLoader(Query query, String tag) {
		this(query);
		this.tag = tag;
	}
	
	public String getTag() {
		return tag;
	}

	@Override
	protected void onStartLoading() {
	    forceLoad();
	}

	@Override
	public List<QueryRow> loadInBackground() {
		try {
			List<QueryRow> ret = new ArrayList<QueryRow>();
			QueryEnumerator enumerator = query.run();
			if (enumerator != null) { 
				while (enumerator.hasNext()) {
					QueryRow row = enumerator.next();
					ret.add(row);
				}
			}
			return ret;
		} catch (CouchbaseLiteException e) {
			throw new RuntimeException(e);
		}
	}

}
