package com.pigdroid.gameboard.view.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.view.DrawerActivity;

/**
 * An activity representing a single Item detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link ItemListAbstractActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link ItemDetailAbstractFragment}.
 */
public abstract class ItemDetailAbstractActivity extends DrawerActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewId());

		// Show the Up button in the action bar.
		android.support.v7.app.ActionBar ab = getSupportActionBar();
		if (ab != null) {
			ab.setDisplayHomeAsUpEnabled(true);
		}

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString("id", getIntent()
					.getStringExtra("id"));
			Fragment fragment = getNewItemDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(getItemDetailContainerId(), fragment).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			navigateUpTo(new Intent(this, getParentActivityClass()));
			overridePendingTransition(R.anim.to_right, R.anim.from_left);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected abstract Class<?> getParentActivityClass();

	protected abstract int getItemDetailContainerId();

	protected abstract Fragment getNewItemDetailFragment();

	protected abstract int getContentViewId();
	
}
