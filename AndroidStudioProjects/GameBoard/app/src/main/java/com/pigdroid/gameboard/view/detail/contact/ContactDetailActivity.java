package com.pigdroid.gameboard.view.detail.contact;

import android.support.v4.app.Fragment;

import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.view.detail.ItemDetailAbstractActivity;

public class ContactDetailActivity extends ItemDetailAbstractActivity {


	protected Class<?> getParentActivityClass() {
		return ContactListActivity.class;
	}

	protected int getItemDetailContainerId() {
		return R.id.item_detail_container;
	}

	protected Fragment getNewItemDetailFragment() {
		return new ContactDetailFragment();
	}

	protected int getContentViewId() {
		return R.layout.activity_contact_detail;
	}
}
